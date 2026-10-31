package com.example.ui.market

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CurrencyRuble
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.market.MarketItem
import com.example.data.market.MarketSyncState
import com.example.ui.components.TacticalChip
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.TacticalHazardRed
import com.example.ui.theme.TarkovBackground
import com.example.ui.theme.TarkovSurface
import com.example.ui.theme.TarkovSurfaceBorder
import com.example.ui.theme.TarkovSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

enum class MarketSortOption(val label: String) {
    PRICE_DESC("Price: High → Low"),
    SLOT_VALUE("Value / Slot"),
    TREND_DESC("24h Trend %"),
    NAME_ASC("Name: A → Z")
}

@Composable
fun LiveMarketScreen(
    items: List<MarketItem>,
    syncState: MarketSyncState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var selectedSort by remember { mutableStateOf(MarketSortOption.PRICE_DESC) }

    val categories = listOf("ALL", "Keys & Cards", "Valuables & Tech", "Medical", "Ammo & Armor")

    val filteredAndSortedItems = remember(items, searchQuery, selectedCategory, selectedSort) {
        val filtered = items.filter { item ->
            val matchesSearch = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.shortName.contains(searchQuery, ignoreCase = true) ||
                    (item.bestTraderName?.contains(searchQuery, ignoreCase = true) == true)

            val matchesCategory = selectedCategory == "ALL" || item.category.equals(selectedCategory, ignoreCase = true)
            matchesSearch && matchesCategory
        }

        when (selectedSort) {
            MarketSortOption.PRICE_DESC -> filtered.sortedByDescending { it.effectivePrice }
            MarketSortOption.SLOT_VALUE -> filtered.sortedByDescending { it.pricePerSlot }
            MarketSortOption.TREND_DESC -> filtered.sortedByDescending { it.changeLast48hPercent }
            MarketSortOption.NAME_ASC -> filtered.sortedBy { it.name }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TarkovBackground)
            .padding(14.dp)
            .testTag("live_market_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = TacticalAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "FLEA MARKET INTEL",
                        color = TacticalAmber,
                        fontSize = 17.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Live pricing, slot efficiency & trader comparison",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            // Sync Status Pill & Refresh button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(TarkovSurface)
                    .border(BorderStroke(1.dp, TarkovSurfaceBorder), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clickable { onRefresh() }
                    .testTag("market_sync_pill")
            ) {
                when (syncState) {
                    is MarketSyncState.Syncing -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            color = TacticalCyan,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SYNCING...",
                            color = TacticalCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    is MarketSyncState.Synced -> {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TacticalEmerald)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE",
                            color = TacticalEmerald,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    is MarketSyncState.OfflinePreset -> {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TacticalAmber)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OFFLINE",
                            color = TacticalAmber,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    is MarketSyncState.Error, MarketSyncState.Idle -> {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TacticalHazardRed)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "STANDBY",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .size(28.dp)
                        .padding(start = 4.dp)
                        .testTag("market_refresh_button")
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh Market Data",
                        tint = TacticalAmber,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search items, keys, ammo (e.g. 314, LEDX, GPU)", color = TextMuted, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TacticalAmber, modifier = Modifier.size(18.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TacticalAmber,
                unfocusedBorderColor = TarkovSurfaceBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = TacticalAmber
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("market_search_input"),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                TacticalChip(
                    text = cat,
                    isSelected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    activeColor = TacticalAmber,
                    testTag = "filter_cat_${cat.replace(" ", "_")}"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Sort Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SORT:",
                color = TextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            MarketSortOption.values().forEach { sortOpt ->
                TacticalChip(
                    text = sortOpt.label,
                    isSelected = selectedSort == sortOpt,
                    onClick = { selectedSort = sortOpt },
                    activeColor = TacticalCyan,
                    testTag = "sort_${sortOpt.name.lowercase()}"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Items list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("market_items_list"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredAndSortedItems, key = { it.id }) { item ->
                MarketItemCard(item = item)
            }
        }
    }
}

@Composable
fun MarketItemCard(item: MarketItem, modifier: Modifier = Modifier) {
    val formatter = remember { NumberFormat.getNumberInstance(Locale.US) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(TarkovSurface)
            .border(BorderStroke(1.dp, TarkovSurfaceBorder), RoundedCornerShape(8.dp))
            .padding(12.dp)
            .testTag("market_item_${item.id}")
    ) {
        Column {
            // Top Row: Short name + Trend indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.shortName,
                        color = TacticalAmber,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    // Slots badge
                    Text(
                        text = "[${item.slots} ${if (item.slots == 1) "slot" else "slots"}]",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // 24h/48h trend badge
                val trendColor = if (item.changeLast48hPercent >= 0) TacticalEmerald else TacticalHazardRed
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(trendColor.copy(alpha = 0.15f))
                        .border(BorderStroke(1.dp, trendColor.copy(alpha = 0.5f)), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = if (item.changeLast48hPercent >= 0) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = trendColor,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = item.formattedChange,
                        color = trendColor,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Full Item Name
            Text(
                text = item.name,
                color = TextSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
            )

            // Pricing Grid: Flea Price vs Slot Value vs Trader
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(TarkovSurfaceElevated)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flea Price
                Column {
                    Text(text = "FLEA AVG", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Text(
                        text = "₽ ${formatter.format(item.effectivePrice)}",
                        color = TacticalEmerald,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Price Per Slot
                Column {
                    Text(text = "PER SLOT", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Text(
                        text = "₽ ${formatter.format(item.pricePerSlot)}",
                        color = TacticalCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Best Trader
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "BEST TRADER",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    if (item.bestTraderName != null && item.bestTraderPriceRUB > 0) {
                        Text(
                            text = "${item.bestTraderName}: ₽ ${formatter.format(item.bestTraderPriceRUB)}",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    } else {
                        Text(
                            text = "Flea Only",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
