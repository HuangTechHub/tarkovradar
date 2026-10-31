package com.example.data.market

data class MarketItem(
    val id: String,
    val name: String,
    val shortName: String,
    val basePrice: Int,
    val avg24hPrice: Int,
    val lastLowPrice: Int,
    val changeLast48hPercent: Double,
    val bestTraderName: String? = null,
    val bestTraderPriceRUB: Int = 0,
    val iconLink: String? = null,
    val category: String = "General",
    val slots: Int = 1,
    val updated: String? = null
) {
    val effectivePrice: Int
        get() = if (lastLowPrice > 0) lastLowPrice else if (avg24hPrice > 0) avg24hPrice else basePrice

    val pricePerSlot: Int
        get() = if (slots > 0) effectivePrice / slots else effectivePrice

    val isTrendingUp: Boolean
        get() = changeLast48hPercent > 0.0

    val formattedChange: String
        get() = String.format(java.util.Locale.US, "%+.1f%%", changeLast48hPercent)
}

sealed class MarketSyncState {
    object Idle : MarketSyncState()
    object Syncing : MarketSyncState()
    data class Synced(val itemCount: Int, val timestamp: Long, val timeAgo: String) : MarketSyncState()
    data class OfflinePreset(val itemCount: Int, val notice: String) : MarketSyncState()
    data class Error(val message: String) : MarketSyncState()
}
