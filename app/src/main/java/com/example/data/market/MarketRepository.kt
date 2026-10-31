package com.example.data.market

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MarketRepository(
    private val service: TarkovMarketService = TarkovMarketService(),
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    private val _items = MutableStateFlow(TarkovMarketPresets.defaultItems)
    val items: StateFlow<List<MarketItem>> = _items.asStateFlow()

    private val _syncState = MutableStateFlow<MarketSyncState>(
        MarketSyncState.OfflinePreset(
            itemCount = TarkovMarketPresets.defaultItems.size,
            notice = "Wipe verified baseline loaded"
        )
    )
    val syncState: StateFlow<MarketSyncState> = _syncState.asStateFlow()

    init {
        // Trigger initial background sync
        refresh()
    }

    fun refresh() {
        externalScope.launch {
            _syncState.value = MarketSyncState.Syncing

            val result = service.fetchMarketPrices()
            result.onSuccess { liveList ->
                if (liveList.isNotEmpty()) {
                    _items.value = liveList
                    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    _syncState.value = MarketSyncState.Synced(
                        itemCount = liveList.size,
                        timestamp = System.currentTimeMillis(),
                        timeAgo = "Live • ${timeFormat.format(Date())}"
                    )
                } else {
                    _syncState.value = MarketSyncState.OfflinePreset(
                        itemCount = _items.value.size,
                        notice = "Server returned empty set. Using presets."
                    )
                }
            }.onFailure { error ->
                _syncState.value = MarketSyncState.OfflinePreset(
                    itemCount = _items.value.size,
                    notice = "Tarkov.dev: ${error.message ?: "Offline"} (Using presets)"
                )
            }
        }
    }

    /**
     * Looks up market data by item name or partial match (useful for key names and loot spawn tags)
     */
    fun findItemByFuzzyName(query: String): MarketItem? {
        val clean = query.trim().lowercase()
        val currentList = _items.value
        // Exact name match
        currentList.firstOrNull { it.name.equals(clean, ignoreCase = true) }?.let { return it }
        // Exact short name match
        currentList.firstOrNull { it.shortName.equals(clean, ignoreCase = true) }?.let { return it }
        // Substring match
        return currentList.firstOrNull {
            it.name.contains(clean, ignoreCase = true) || it.shortName.contains(clean, ignoreCase = true) ||
                    clean.contains(it.shortName.lowercase())
        }
    }
}
