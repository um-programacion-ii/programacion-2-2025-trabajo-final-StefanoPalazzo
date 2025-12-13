package com.stefanopalazzo.eventosapp.presentation.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventoId: Long,
    onBackClick: () -> Unit,
    onSelectSeatsClick: (Long) -> Unit,
    viewModel: EventDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(eventoId) {
        viewModel.loadEvento(eventoId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Evento") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("<") // TODO: Use proper icon
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.error ?: "Error desconocido",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadEvento(eventoId) }) {
                            Text("Reintentar")
                        }
                    }
                }
                uiState.evento != null -> {
                    val evento = uiState.evento!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Imagen del evento
                        AsyncImage(
                            model = evento.imagen,
                            contentDescription = evento.titulo,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )
                        
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = evento.titulo,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = evento.eventoTipo.nombre,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            InfoRow(label = "Fecha:", value = evento.fecha.substringBefore('T'))
                            InfoRow(label = "Lugar:", value = evento.direccion)
                            InfoRow(label = "Precio:", value = "$${evento.precioEntrada}")
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Descripción",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = evento.descripcion,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            if (evento.integrantes.isNotEmpty()) {
                                Text(
                                    text = "Integrantes",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                evento.integrantes.forEach { integrante ->
                                    Text(
                                        text = "• ${integrante.nombre} ${integrante.apellido} (${integrante.identificacion})",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            Button(
                                onClick = { onSelectSeatsClick(evento.id) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Seleccionar Asientos")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "$label ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
