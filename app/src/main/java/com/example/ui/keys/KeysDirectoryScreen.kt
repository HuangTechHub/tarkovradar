package com.example.ui.keys

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KeyEntry
import com.example.ui.components.TacticalChip
import com.example.ui.theme.RarityLegendary
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TarkovBackground
import com.example.ui.theme.TarkovSurface
import com.example.ui.theme.TarkovSurfaceBorder
import com.example.ui.theme.TarkovSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun KeysDirectoryScreen(
    keysList: List<KeyEntry>,
    onSelectKeyMap: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedMapFilter by remember { mutableStateOf<String?>(null) }

    val maps = remember(keysList) {
        listOf("ALL") + keysList.map { it.mapName }.distinct()
    }

    val filteredKeys = remember(keysList, searchQuery, selectedMapFilter) {
        keysList.filter { key ->
            val matchesSearch = key.name.contains(searchQuery, ignoreCase = true) ||
                    key.lockLocation.contains(searchQuery, ignoreCase = true) ||
                    key.notableLoot.any { it.contains(searchQuery, ignoreCase = true) }
            val matchesMap = selectedMapFilter == null || selectedMapFilter == "ALL" || key.mapName == selectedMapFilter
            matchesSearch && matchesMap
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TarkovBackground)
            .padding(14.dp)
            .testTag("keys_directory_screen")
    ) {
        Text(
            text = "KEY & LOCK DIRECTORY",
            color = TacticalAmber,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "High-tier locked doors, keys, and behind-door loot values",
            color = TextSecondary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search key, room, or loot (e.g. 314, Red, LEDX)...", color = TextMuted, fontSize = 13.sp) },
            modifier = Modifier.fillMaxWidth().testTag("keys_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = TacticalAmber,
                unfocusedBorderColor = TarkovSurfaceBorder
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Map filter chips
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            maps.forEach { mapName ->
                val isSelected = (selectedMapFilter == null && mapName == "ALL") || selectedMapFilter == mapName
                TacticalChip(
                    text = mapName.uppercase(),
                    isSelected = isSelected,
                    onClick = { selectedMapFilter = if (mapName == "ALL") null else mapName },
                    activeColor = TacticalCyan,
                    testTag = "key_map_chip_$mapName"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredKeys, key = { it.id }) { key ->
                KeyCard(key)
            }
        }
    }
}

@Composable
private fun KeyCard(key: KeyEntry) {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    val priceFormatted = "₽ ${formatter.format(key.estimatedValue)}"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(TarkovSurface)
            .border(BorderStroke(1.dp, TarkovSurfaceBorder), RoundedCornerShape(8.dp))
            .padding(12.dp)
            .testTag("key_card_${key.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "Key",
                        tint = TacticalAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = " ${key.name}",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = priceFormatted,
                    color = RarityLegendary,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(TarkovSurfaceElevated)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = key.mapName,
                        color = TacticalCyan,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Floor: ${key.floorName}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Lock Location: ${key.lockLocation}",
                color = TextSecondary,
                fontSize = 12.sp
            )

            if (key.requiredQuests.isNotEmpty()) {
                Text(
                    text = "Quests: ${key.requiredQuests.joinToString(", ")}",
                    color = TacticalAmber,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Notable Loot: ${key.notableLoot.joinToString(", ")}",
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}
