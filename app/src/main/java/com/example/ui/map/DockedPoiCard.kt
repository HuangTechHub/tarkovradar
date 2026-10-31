package com.example.ui.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Rarity
import com.example.ui.components.FactionBadge
import com.example.ui.components.RarityBadge
import com.example.ui.theme.RarityLegendary
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalCyan
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.TacticalHazardRed
import com.example.ui.theme.TarkovSurface
import com.example.ui.theme.TarkovSurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

import com.example.data.market.MarketItem

@Composable
fun DockedPoiCard(
    marker: TacticalMarker,
    onOpenFullDetail: () -> Unit,
    onCenterOnMarker: () -> Unit,
    onDismiss: () -> Unit,
    getLivePrice: ((String) -> MarketItem?)? = null,
    modifier: Modifier = Modifier
) {
    val formatter = NumberFormat.getNumberInstance(Locale.US)

    val (title, subtitle, accentColor, floorNum) = when (marker) {
        is TacticalMarker.Loot -> {
            val liveItem = getLivePrice?.invoke(marker.spawn.name)
            val priceVal = liveItem?.effectivePrice ?: marker.spawn.estValueRoubles
            val trendStr = if (liveItem != null) " [LIVE ${liveItem.formattedChange}]" else ""
            val priceStr = "₽ ${formatter.format(priceVal)}$trendStr"
            Quad(marker.spawn.name, "${marker.spawn.containerType} • $priceStr", marker.spawn.rarity.color, marker.spawn.floorIndex)
        }
        is TacticalMarker.Extract -> {
            Quad(marker.extract.name, marker.extract.extractType.label, marker.extract.factionColor, marker.extract.floorIndex)
        }
        is TacticalMarker.Boss -> {
            Quad("Boss: ${marker.boss.name}", "${marker.boss.spawnChancePercent}% Spawn Rate • ${marker.boss.guardCount} Guards", TacticalHazardRed, marker.boss.floorIndex)
        }
        is TacticalMarker.Quest -> {
            Quad(marker.quest.questName, "Trader: ${marker.quest.trader}", TacticalAmber, marker.quest.floorIndex)
        }
        is TacticalMarker.LockedDoor -> {
            val liveKey = getLivePrice?.invoke(marker.lock.keyName)
            val keyPriceVal = liveKey?.effectivePrice ?: marker.lock.estKeyPriceRubles
            val liveTag = if (liveKey != null) " [LIVE ${liveKey.formattedChange}]" else ""
            val keyPriceStr = "Key: ₽ ${formatter.format(keyPriceVal)}$liveTag"
            val markedTag = if (marker.lock.isMarkedRoom) "[MARKED] " else ""
            Quad(
                "$markedTag${marker.lock.doorName}",
                "Requires ${marker.lock.keyName} • $keyPriceStr",
                if (marker.lock.isMarkedRoom) Color(0xFFA855F7) else TacticalAmber,
                marker.lock.floorIndex
            )
        }
        is TacticalMarker.UserPin -> {
            Quad("Waypoint: ${marker.pin.title}", "Custom Tactical Pin (${marker.pin.tagType})", TacticalCyan, marker.pin.floorIndex)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF141C26).copy(alpha = 0.96f))
            .border(BorderStroke(1.5.dp, accentColor), RoundedCornerShape(12.dp))
            .padding(14.dp)
            .testTag("docked_poi_card")
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
                    when (marker) {
                        is TacticalMarker.Loot -> RarityBadge(rarity = marker.spawn.rarity)
                        is TacticalMarker.Extract -> FactionBadge(faction = marker.extract.faction)
                        is TacticalMarker.LockedDoor -> {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background((if (marker.lock.isMarkedRoom) Color(0xFFA855F7) else TacticalAmber).copy(alpha = 0.2f))
                                    .border(BorderStroke(1.dp, if (marker.lock.isMarkedRoom) Color(0xFFA855F7) else TacticalAmber), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (marker.lock.isMarkedRoom) "MARKED ROOM" else "LOCKED DOOR",
                                    color = if (marker.lock.isMarkedRoom) Color(0xFFC084FC) else TacticalAmber,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        else -> {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(accentColor.copy(alpha = 0.2f))
                                    .border(BorderStroke(1.dp, accentColor), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "TARGET INTEL",
                                    color = accentColor,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (floorNum == 0) "GROUND" else "${floorNum}F",
                            color = TacticalCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp).testTag("close_docked_card_button")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                color = accentColor,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCenterOnMarker,
                    modifier = Modifier.weight(1f).testTag("center_marker_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, TarkovSurfaceBorder),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(imageVector = Icons.Default.LocationSearching, contentDescription = null, tint = TacticalCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("CENTER", color = TacticalCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onOpenFullDetail,
                    modifier = Modifier.weight(1f).testTag("full_intel_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("FULL INTEL", color = Color.Black, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
