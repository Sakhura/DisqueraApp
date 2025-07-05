package com.fcterryamigos.disqueraapp.utils

object Constants {
    // Base URLs (para futuras integraciones de API)
    const val BASE_URL = "https://api.disqueraapp.com/"
    const val IMAGE_BASE_URL = "https://images.disqueraapp.com/"

    // Preferencias
    const val PREFS_NAME = "DisqueraAppPrefs"
    const val PREF_USER_ID = "user_id"
    const val PREF_IS_LOGGED_IN = "is_logged_in"

    // Database
    const val DATABASE_NAME = "disquera_database"
    const val DATABASE_VERSION = 1

    // Timeouts
    const val NETWORK_TIMEOUT = 30L
    const val CONNECTION_TIMEOUT = 10L

    // Pagination
    const val DEFAULT_PAGE_SIZE = 20
    const val PREFETCH_DISTANCE = 5

    // Validation
    const val MIN_PASSWORD_LENGTH = 6
    const val MAX_CART_QUANTITY = 10

    // Formats
    const val DATE_FORMAT = "dd/MM/yyyy"
    const val DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm"
    const val CURRENCY_LOCALE = "es_MX"

    // Request codes
    const val RC_SIGN_IN = 9001
    const val RC_PICK_IMAGE = 9002

    // Bundle keys
    const val KEY_DISCO_ID = "disco_id"
    const val KEY_USER_ID = "user_id"
    const val KEY_ORDER_ID = "order_id"

    // Error messages
    const val ERROR_NETWORK = "Error de conexión. Verifica tu internet."
    const val ERROR_EMPTY_CART = "Tu carrito está vacío"
    const val ERROR_OUT_OF_STOCK = "Producto sin stock"
    const val ERROR_SESSION_EXPIRED = "Tu sesión ha expirado. Inicia sesión nuevamente."

    // Success messages
    const val SUCCESS_ADDED_TO_CART = "Producto agregado al carrito"
    const val SUCCESS_ORDER_PLACED = "Pedido realizado exitosamente"
    const val SUCCESS_PROFILE_UPDATED = "Perfil actualizado correctamente"
}

