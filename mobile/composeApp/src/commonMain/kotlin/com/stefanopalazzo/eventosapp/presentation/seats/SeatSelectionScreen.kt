package com.stefanopalazzo.eventosapp.presentation.seats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.stefanopalazzo.eventosapp.data.models.AsientoRedis
import com.stefanopalazzo.eventosapp.data.models.AsientoSimple
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    eventoId: Long,
    onBackClick: () -> Unit,
    onNavigateToCheckout: (Long, String) -> Unit, // Pasamos eventoId y asientos serializados (simple way)
    viewModel: SeatSelectionViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(eventoId) {
        viewModel.loadData(eventoId)
    }
    
    LaunchedEffect(uiState.navigateToCheckout) {
        if (uiState.navigateToCheckout) {
            // Serializamos los asientos seleccionados para pasarlos (en una app real usaríamos un SharedViewModel o DB local)
            // Aquí simplificamos pasando solo el ID, y el CheckoutViewModel leerá el estado o lo pasamos como argumento
            // Por ahora pasamos una cadena simple "f1c1,f1c2"
            val asientosStr = uiState.asientosSeleccionados.joinToString(",") { "${it.fila}:${it.columna}" }
            onNavigateToCheckout(eventoId, asientosStr)
            viewModel.navigationHandled()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Selección de Asientos") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("<")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${uiState.asientosSeleccionados.size} seleccionados",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Button(
                        onClick = { viewModel.bloquearAsientos() },
                        enabled = uiState.asientosSeleccionados.isNotEmpty() && !uiState.isBlocking
                    ) {
                        if (uiState.isBlocking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Continuar")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.evento != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Leyenda
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LegendItem(color = Color.LightGray, text = "Libre")
                        LegendItem(color = MaterialTheme.colorScheme.primary, text = "Tuyo")
                        LegendItem(color = Color(0xFFFFA500), text = "Ocupado")
                        LegendItem(color = Color.Red, text = "Vendido")
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Pantalla (referencia visual)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(8.dp)
                            .background(Color.DarkGray, RoundedCornerShape(4.dp))
                    )
                    Text("PANTALLA", style = MaterialTheme.typography.labelSmall)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Grid de asientos
                    // Usamos scroll bidireccional por si el mapa es muy grande
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .horizontalScroll(rememberScrollState())
                    ) {
                        SeatGrid(
                            rows = uiState.evento!!.filaAsientos,
                            cols = uiState.evento!!.columnAsientos,
                            occupiedSeats = uiState.asientosOcupados,
                            selectedSeats = uiState.asientosSeleccionados,
                            onSeatClick = { r, c -> viewModel.toggleSeatSelection(r, c) }
                        )
                    }
                }
            }
            
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun SeatGrid(
    rows: Int,
    cols: Int,
    occupiedSeats: List<AsientoRedis>,
    selectedSeats: List<AsientoSimple>,
    onSeatClick: (Int, Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (r in 1..rows) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (c in 1..cols) {
                    val occupiedSeat = occupiedSeats.find { it.fila == r && it.columna == c }
                    val isSelected = selectedSeats.any { it.fila == r && it.columna == c }
                    
                    val status = when {
                        isSelected -> SeatStatus.SELECTED
                        occupiedSeat != null -> {
                            when (occupiedSeat.estado.uppercase()) {
                                "VENDIDO" -> SeatStatus.SOLD
                                "BLOQUEADO" -> SeatStatus.BLOCKED
                                "LIBRE" -> SeatStatus.FREE
                                else -> SeatStatus.FREE // Default to free if unknown/unexpected
                            }
                        }
                        else -> SeatStatus.FREE
                    }
                    
                    SeatItem(
                        row = r,
                        col = c,
                        status = status,
                        onClick = { onSeatClick(r, c) }
                    )
                }
            }
        }
    }
}

enum class SeatStatus { FREE, BLOCKED, SOLD, SELECTED }

@Composable
fun SeatItem(
    row: Int,
    col: Int,
    status: SeatStatus,
    onClick: () -> Unit
) {
    val backgroundColor = when (status) {
        SeatStatus.FREE -> Color.LightGray
        SeatStatus.BLOCKED -> Color(0xFFFFA500) // Orange
        SeatStatus.SOLD -> Color.Red
        SeatStatus.SELECTED -> MaterialTheme.colorScheme.primary // Purple/Primary
    }
    
    val isClickable = status == SeatStatus.FREE || status == SeatStatus.SELECTED
    
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = if (status == SeatStatus.SELECTED) Color.White else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = isClickable) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$row-$col",
            style = MaterialTheme.typography.labelSmall,
            color = if (status == SeatStatus.FREE) Color.Black else Color.White,
            fontSize = androidx.compose.ui.unit.TextUnit.Unspecified
        )
    }
}
