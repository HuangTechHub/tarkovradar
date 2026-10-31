package com.example.ui.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import com.example.model.TarkovMap
import com.example.ui.components.FactionBadge
import com.example.ui.components.RarityBadge
import com.example.ui.theme.RarityLegendary
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.TacticalHazardRed
import com.example.ui.theme.TarkovSurface
import com.example.ui.theme.TarkovSurfaceBorder
import com.example.ui.theme.TarkovSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PoiDirectorySheet(
    map: TarkovMap,
    onSelectAndFocusMarker: (TacticalMarker, Float, Float, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val formatter = NumberFormat.getNumberInstance(Locale.US)

    // Collect all POIs
    val allPois = remember(map, searchQuery) {
        val list = mutableListOf<Triple<String, String, TacticalMarker>>()

        map.extractions.forEach { ex ->
            if (ex.name.contains(searchQuery, ignoreCase = true) || ex.requirements.contains(searchQuery, ignoreCase = true)) {
                list.add(Triple("EXTRACTION", ex.name, TacticalMarker.Extract(ex)))
            }
        }

        map.bosses.forEach { boss ->
            if (boss.name.contains(searchQuery, ignoreCase = true) || boss.locationDescription.contains(searchQuery, ignoreCase = true)) {
                list.add(Triple("BOSS", boss.name, TacticalMarker.Boss(boss)))
            }
        }

        map.lootSpawns.forEach { loot ->
            if (loot.name.contains(searchQuery, ignoreCase = true) || loot.description.contains(searchQuery, ignoreCase = true)) {
                list.add(Triple("LOOT", loot.name, TacticalMarker.Loot(loot)))
            }
        }

        map.quests.forEach { q ->
            if (q.questName.contains(searchQuery, ignoreCase = true) || q.objectiveDescription.contains(searchQuery, ignoreCase = true)) {
                list.add(Triple("QUEST", q.questName, TacticalMarker.Quest(q)))
            }
        }

        list
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(BorderStroke(1.5.dp, TacticalAmber), RoundedCornerShape(12.dp))
                .testTag("poi_directory_dialog"),
            color = TarkovSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MAP INTEL & POI DIRECTORY",
                            color = TacticalAmber,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${map.name} • ${allPois.size} Points of Interest",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("close_poi_directory_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter extracts, loot, boss, rooms...", color = TextMuted, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TacticalAmber) },
                    modifier = Modifier.fillMaxWidth().testTag("poi_search_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TacticalAmber,
                        unfocusedBorderColor = TarkovSurfaceBorder
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(allPois, key = { it.second + it.first }) { (type, name, marker) ->
                        val (x, y, floor, accentColor, detailText) = when (marker) {
                            is TacticalMarker.Loot -> Tuple5(marker.spawn.x, marker.spawn.y, marker.spawn.floorIndex, marker.spawn.rarity.color, "₽ ${formatter.format(marker.spawn.estValueRoubles)} • ${marker.spawn.containerType}")
                            is TacticalMarker.Extract -> Tuple5(marker.extract.x, marker.extract.y, marker.extract.floorIndex, marker.extract.factionColor, marker.extract.extractType.label)
                            is TacticalMarker.Boss -> Tuple5(marker.boss.x, marker.boss.y, marker.boss.floorIndex, TacticalHazardRed, "${marker.boss.spawnChancePercent}% Spawn Rate")
                            is TacticalMarker.Quest -> Tuple5(marker.quest.x, marker.quest.y, marker.quest.floorIndex, TacticalAmber, "Trader: ${marker.quest.trader}")
                            is TacticalMarker.UserPin -> Tuple5(marker.pin.x, marker.pin.y, marker.pin.floorIndex, TacticalCyan, marker.pin.tagType)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(TarkovSurfaceElevated)
                                .border(BorderStroke(1.dp, TarkovSurfaceBorder), RoundedCornerShape(6.dp))
                                .clickable {
                                    onSelectAndFocusMarker(marker, x, y, floor)
                                    onDismiss()
                                }
                                .padding(10.dp)
                                .testTag("poi_item_${name}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(accentColor.copy(alpha = 0.2f))
                                                .border(BorderStroke(1.dp, accentColor), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = type,
                                                color = accentColor,
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Text(
                                            text = name,
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = detailText,
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF1E293B))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (floor == 0) "G" else "${floor}F",
                                            color = TacticalCyan,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Icon(
                                        imageVector = Icons.Default.LocationSearching,
                                        contentDescription = "Focus",
                                        tint = TacticalAmber,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().testTag("close_poi_dialog_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = TarkovSurfaceElevated),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("CLOSE", color = TextPrimary, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

private data class Tuple5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)
