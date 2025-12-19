package com.stefanopalazzo.eventosapp.data.api

object ApiConfig {
    /**
     * Configuración de la URL base del backend
     * 
     * INSTRUCCIONES PARA DESARROLLO:
     * 
     * 1. Android Emulator:
     *    - Usa: "http://10.0.2.2:8081"
     *    - El emulador mapea 10.0.2.2 al localhost de tu PC
     * 
     * 2. iOS Simulator:
     *    - Usa: "http://localhost:8081"
     *    - El simulador comparte la red con macOS
     * 
     * 3. Dispositivo físico (Android/iOS):
     *    - Usa: "http://TU_IP_LOCAL:8081"
     *    - Ejemplo: "http://192.168.1.100:8081"
     *    - Para encontrar tu IP:
     *      - Windows: ipconfig
     *      - Mac/Linux: ifconfig | grep inet
     * 
     * 4. Producción:
     *    - Usa: "https://api.tuapp.com"
     *    - Servidor real en internet
     */
    
    // Cambiar según tu entorno de desarrollo
    private const val DEVELOPMENT_URL = "http://10.0.2.2:8081" // Android Emulator por defecto
    val PROXY_URL = "http://10.0.2.2:8085" // Android Emulator por defecto
    
    // URL de producción (cuando la app esté publicada)
    private const val PRODUCTION_URL = "https://api.tuapp.com"
    
    // Determina automáticamente si estamos en desarrollo o producción
    val BASE_URL: String = DEVELOPMENT_URL // Cambiar a PRODUCTION_URL para release
    
    /**
     * Para facilitar el testing, puedes crear diferentes configuraciones:
     */
    object Environments {
        const val ANDROID_EMULATOR = "http://10.0.2.2:8081"
        const val IOS_SIMULATOR = "http://localhost:8081"
        const val PHYSICAL_DEVICE = "http://192.168.1.100:8081" // Cambiar por tu IP
        const val PRODUCTION = "https://api.tuapp.com"
    }
}
