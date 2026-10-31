package com.example.ui.hud

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.model.ExtractFaction
import com.example.model.ExtractionPoint
import com.example.ui.components.FactionBadge
import com.example.ui.components.TacticalChip
import com.example.ui.theme.TacticalAmber
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
fun ExtractChecklistDialog(
    mapName: String,
    extracts: List<ExtractionPoint>,
    visitedExtractIds: Set<String>,
    onToggleVisited: (String) -> Unit,
    onSelectExtract: (ExtractionPoint) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedFactionFilter by remember { mutableStateOf<ExtractFaction?>(null) }

    val filteredList = extracts.filter { extract ->
        when (selectedFactionFilter) {
            null -> true
            ExtractFaction.PMC_ONLY -> extract.faction == ExtractFaction.PMC_ONLY || extract.faction == ExtractFaction.ALL
            ExtractFaction.SCAV_ONLY -> extract.faction == ExtractFaction.SCAV_ONLY || extract.faction == ExtractFaction.ALL
            ExtractFaction.ALL -> extract.faction == ExtractFaction.ALL
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(BorderStroke(1.5.dp, TacticalEmerald), RoundedCornerShape(12.dp))
                .testTag("extract_checklist_dialog"),
            color = TarkovSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EXTRACTION CHECKLIST",
                            color = TacticalEmerald,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$mapName (${extracts.size} Points)",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("close_extract_checklist_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Faction Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TacticalChip(
                        text = "ALL",
                        isSelected = selectedFactionFilter == null,
                        onClick = { selectedFactionFilter = null },
                        activeColor = TacticalEmerald,
                        testTag = "filter_extract_all"
                    )
                    TacticalChip(
                        text = "PMC ONLY",
                        isSelected = selectedFactionFilter == ExtractFaction.PMC_ONLY,
                        onClick = { selectedFactionFilter = ExtractFaction.PMC_ONLY },
                        activeColor = TacticalEmerald,
                        testTag = "filter_extract_pmc"
                    )
                    TacticalChip(
                        text = "SCAV ONLY",
                        isSelected = selectedFactionFilter == ExtractFaction.SCAV_ONLY,
                        onClick = { selectedFactionFilter = ExtractFaction.SCAV_ONLY },
                        activeColor = TacticalAmber,
                        testTag = "filter_extract_scav"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // List of Extracts
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .height(340.dp)
                ) {
                    items(filteredList, key = { it.id }) { extract ->
                        val isVisited = visitedExtractIds.contains(extract.id)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isVisited) TarkovSurfaceBorder.copy(alpha = 0.3f) else TarkovSurfaceElevated)
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        if (isVisited) TacticalEmerald else TarkovSurfaceBorder
                                    ),
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    onSelectExtract(extract)
                                    onDismiss()
                                }
                                .padding(10.dp)
                                .testTag("extract_item_${extract.id}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isVisited,
                                    onCheckedChange = { onToggleVisited(extract.id) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = TacticalEmerald,
                                        uncheckedColor = TextSecondary,
                                        checkmarkColor = TarkovSurface
                                    ),
                                    modifier = Modifier.size(24.dp)
                                )

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 10.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = extract.name,
                                            color = if (isVisited) TextSecondary else TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        FactionBadge(faction = extract.faction)
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = extract.extractType.label,
                                        color = if (extract.hasGreenSmoke || extract.hasFlares) TacticalHazardRed else TacticalAmber,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )

                                    Text(
                                        text = extract.requirements,
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().testTag("close_extract_sheet_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = TarkovSurfaceElevated),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("DONE", color = TextPrimary, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}
