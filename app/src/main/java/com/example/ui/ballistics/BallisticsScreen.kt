package com.example.ui.ballistics

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
import com.example.model.AmmoEntry
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

@Composable
fun BallisticsScreen(
    ammoList: List<AmmoEntry>,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCaliber by remember { mutableStateOf<String?>(null) }

    val calibers = remember(ammoList) {
        listOf("ALL") + ammoList.map { it.caliber }.distinct()
    }

    val filteredAmmo = remember(ammoList, searchQuery, selectedCaliber) {
        ammoList.filter { ammo ->
            val matchesSearch = ammo.name.contains(searchQuery, ignoreCase = true) ||
                    ammo.caliber.contains(searchQuery, ignoreCase = true)
            val matchesCaliber = selectedCaliber == null || selectedCaliber == "ALL" || ammo.caliber == selectedCaliber
            matchesSearch && matchesCaliber
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TarkovBackground)
            .padding(14.dp)
            .testTag("ballistics_screen")
    ) {
        Text(
            text = "BALLISTICS & PENETRATION MATRIX",
            color = TacticalAmber,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Penetration power vs Body Armor Classes 1 to 6",
            color = TextSecondary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search cartridge (e.g. M995, BS, M61, BP)...", color = TextMuted, fontSize = 13.sp) },
            modifier = Modifier.fillMaxWidth().testTag("ballistics_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = TacticalAmber,
                unfocusedBorderColor = TarkovSurfaceBorder
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Caliber chips
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            calibers.forEach { cal ->
                val isSelected = (selectedCaliber == null && cal == "ALL") || selectedCaliber == cal
                TacticalChip(
                    text = cal,
                    isSelected = isSelected,
                    onClick = { selectedCaliber = if (cal == "ALL") null else cal },
                    activeColor = TacticalCyan,
                    testTag = "caliber_chip_$cal"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Ammo List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredAmmo, key = { it.id }) { ammo ->
                AmmoCard(ammo)
            }
        }
    }
}

@Composable
private fun AmmoCard(ammo: AmmoEntry) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(TarkovSurface)
            .border(BorderStroke(1.dp, TarkovSurfaceBorder), RoundedCornerShape(8.dp))
            .padding(12.dp)
            .testTag("ammo_card_${ammo.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = ammo.name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = ammo.caliber,
                        color = TacticalAmber,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatPill("DMG", "${ammo.damage}", TacticalEmerald)
                    StatPill("PEN", "${ammo.penetration}", TacticalCyan)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Armor Class Effectiveness Grid (Class 1 to 6)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CLASS EFFECTIVENESS:",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ammo.classEffectiveness.forEachIndexed { index, score ->
                        val classNum = index + 1
                        val color = when {
                            score >= 5 -> TacticalEmerald
                            score >= 3 -> TacticalAmber
                            else -> TacticalHazardRed
                        }

                        Box(
                            modifier = Modifier
                                .size(width = 30.dp, height = 24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(color.copy(alpha = 0.2f))
                                .border(BorderStroke(1.dp, color), RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "C$classNum",
                                color = color,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Velocity: ${ammo.projectileVelocity} m/s",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Frag: ${ammo.fragChancePct}% • Armor Dmg: ${ammo.armorDamagePct}%",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun StatPill(label: String, value: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(TarkovSurfaceElevated)
            .border(BorderStroke(1.dp, color.copy(alpha = 0.5f)), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "$label: ",
                color = TextSecondary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = value,
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
