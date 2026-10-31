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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import com.example.model.Rarity
import com.example.ui.components.FactionBadge
import com.example.ui.components.RarityBadge
import com.example.ui.components.TacticalCard
import com.example.ui.theme.RarityLegendary
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

@Composable
fun TacticalDetailDialog(
    marker: TacticalMarker,
    onDismiss: () -> Unit,
    onDeletePin: ((Long) -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(BorderStroke(1.5.dp, TacticalAmber), RoundedCornerShape(12.dp))
                .testTag("tactical_detail_dialog"),
            color = TarkovSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val headerCategory = when (marker) {
                        is TacticalMarker.Loot -> "LOOT SPAWN INTEL"
                        is TacticalMarker.Extract -> "EXTRACTION POINT"
                        is TacticalMarker.Boss -> "SCAV BOSS THREAT"
                        is TacticalMarker.Quest -> "TASK OBJECTIVE"
                        is TacticalMarker.LockedDoor -> if (marker.lock.isMarkedRoom) "MARKED ROOM INTEL" else "LOCKED DOOR & KEY INTEL"
                        is TacticalMarker.UserPin -> "OPERATOR WAYPOINT"
                    }

                    Text(
                        text = headerCategory,
                        color = if (marker is TacticalMarker.LockedDoor && marker.lock.isMarkedRoom) Color(0xFFC084FC) else TacticalAmber,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.2.sp
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("close_detail_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                when (marker) {
                    is TacticalMarker.Loot -> LootDetailContent(marker.spawn)
                    is TacticalMarker.Extract -> ExtractDetailContent(marker.extract)
                    is TacticalMarker.Boss -> BossDetailContent(marker.boss)
                    is TacticalMarker.Quest -> QuestDetailContent(marker.quest)
                    is TacticalMarker.LockedDoor -> LockedDoorDetailContent(marker.lock)
                    is TacticalMarker.UserPin -> PinDetailContent(marker.pin, onDeletePin, onDismiss)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().testTag("confirm_dismiss_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = TarkovSurfaceElevated),
                    border = BorderStroke(1.dp, TarkovSurfaceBorder),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("CLOSE INTEL", color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun LootDetailContent(loot: com.example.model.LootSpawn) {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    val rublesText = "₽ ${formatter.format(loot.estValueRoubles)}"

    Column {
        Text(
            text = loot.name,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RarityBadge(rarity = loot.rarity)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(TarkovSurfaceElevated)
                    .border(BorderStroke(1.dp, TarkovSurfaceBorder), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "Floor ${if (loot.floorIndex == 0) "Ground" else "${loot.floorIndex}F"}",
                    color = TacticalCyan,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TacticalCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = TarkovBackground
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Container Type:", color = TextSecondary, fontSize = 12.sp)
                    Text(loot.containerType, color = TacticalAmber, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Estimated Flea Value:", color = TextSecondary, fontSize = 12.sp)
                    Text(rublesText, color = RarityLegendary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("Notable Items & Drops:", color = TextMuted, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = loot.description,
            color = TextPrimary,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun ExtractDetailContent(extract: com.example.model.ExtractionPoint) {
    Column {
        Text(
            text = extract.name,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FactionBadge(faction = extract.faction)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(TacticalAmber.copy(alpha = 0.15f))
                    .border(BorderStroke(1.dp, TacticalAmber), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = extract.extractType.label,
                    color = TacticalAmber,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TacticalCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = TarkovBackground
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (extract.hasGreenSmoke) {
                    Text("● Green Smoke Signal Required", color = TacticalEmerald, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                }
                if (extract.hasFlares) {
                    Text("● Signal Flare Zone (Fire flare before entering)", color = TacticalHazardRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                }
                if (extract.costRubles > 0) {
                    Text("● Fee: ₽ ${extract.costRubles} Roubles (SUV departure)", color = TacticalAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = extract.requirements,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun BossDetailContent(boss: com.example.model.BossSpawn) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = boss.name,
                color = TacticalHazardRed,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(TacticalHazardRed.copy(alpha = 0.2f))
                    .border(BorderStroke(1.dp, TacticalHazardRed), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("${boss.spawnChancePercent}% Spawn Rate", color = TacticalHazardRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Text(boss.title, color = TextMuted, fontSize = 12.sp, fontFamily = FontFamily.Monospace)

        Spacer(modifier = Modifier.height(10.dp))

        TacticalCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = TarkovBackground
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Guards: ${boss.guardCount} Heavily Armed Escorts", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Weaponry: ${boss.weapons}", color = TacticalAmber, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Combat Tactics:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(boss.tacticsHint, color = TextPrimary, fontSize = 12.sp, lineHeight = 17.sp)
            }
        }
    }
}

@Composable
private fun QuestDetailContent(quest: com.example.model.QuestMarker) {
    Column {
        Text(quest.questName, color = TacticalAmber, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Trader: ${quest.trader}", color = TextSecondary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)

        Spacer(modifier = Modifier.height(10.dp))

        TacticalCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = TarkovBackground
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(quest.objectiveDescription, color = TextPrimary, fontSize = 13.sp)
                if (quest.requiredKey != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Required Key: ${quest.requiredKey}", color = TacticalCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PinDetailContent(
    pin: com.example.data.local.CustomPinEntity,
    onDeletePin: ((Long) -> Unit)?,
    onDismiss: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(pin.title, color = TacticalCyan, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            if (onDeletePin != null) {
                IconButton(
                    onClick = {
                        onDeletePin(pin.id)
                        onDismiss()
                    },
                    modifier = Modifier.testTag("delete_pin_button")
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Pin", tint = TacticalHazardRed)
                }
            }
        }

        Text("TAG: ${pin.tagType}", color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)

        Spacer(modifier = Modifier.height(10.dp))

        TacticalCard(modifier = Modifier.fillMaxWidth(), backgroundColor = TarkovBackground) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(pin.note.ifBlank { "No notes specified." }, color = TextPrimary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Coordinates: X=${pin.x.toInt()}, Y=${pin.y.toInt()}", color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
private fun LockedDoorDetailContent(lock: com.example.model.LockedDoorMarker) {
    val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale.US)
    val accentColor = if (lock.isMarkedRoom) Color(0xFFA855F7) else TacticalAmber

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = lock.doorName,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(accentColor.copy(alpha = 0.2f))
                    .border(BorderStroke(1.dp, accentColor), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = if (lock.isMarkedRoom) "MARKED ROOM" else "KEY LOCKED",
                    color = accentColor,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = "Location: ${lock.mapLocation}",
            color = TextSecondary,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Required Key & Value Card
        TacticalCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = accentColor.copy(alpha = 0.5f),
            backgroundColor = TarkovBackground
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("REQUIRED KEY", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(lock.keyName, color = TacticalAmber, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("EST. FLEA PRICE", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("₽ ${formatter.format(lock.estKeyPriceRubles)}", color = TacticalCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TacticalCard(modifier = Modifier.fillMaxWidth(), backgroundColor = TarkovBackground) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(lock.description, color = TextPrimary, fontSize = 13.sp)

                if (lock.notableLoot.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("NOTABLE LOOT INSIDE:", color = TacticalAmber, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    lock.notableLoot.forEach { item ->
                        Text("• $item", color = TextSecondary, fontSize = 12.sp)
                    }
                }

                if (lock.requiredForQuests.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("REQUIRED FOR TASKS:", color = TacticalCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    lock.requiredForQuests.forEach { quest ->
                        Text("◆ $quest", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
