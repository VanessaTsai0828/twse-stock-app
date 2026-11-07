package com.example.bankapp.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bankapp.domain.model.StockItem
import com.example.bankapp.ui.theme.StockGreen
import com.example.bankapp.ui.theme.StockGreenLight
import com.example.bankapp.ui.theme.StockRed
import com.example.bankapp.ui.theme.StockRedLight

/**
 * Redesigned card with clearer visual hierarchy:
 * - Row 1: Code badge (large) + Name (large) + Change badge (large)
 * - Row 2: Closing price (prominent) vs Monthly avg
 * - Row 3: Trade volume summary (compact, secondary)
 */
@Composable
fun StockCard(
    stock: StockItem,
    onClick: (StockItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .clickable { onClick(stock) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ── Row 1: Header ────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Code badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = stock.code,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Stock name
                Text(
                    text = stock.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Change badge
                ChangeBadge(change = stock.change, changeValue = stock.changeValue)
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                thickness = 0.5.dp
            )

            // ── Row 2: Key prices ─────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                // Closing price — most prominent
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "收盤價",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stock.closingPrice.ifBlank { "--" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = closingPriceColor(stock.isAboveMonthlyAverage)
                    )
                }

                // Monthly avg + High / Low — secondary
                Column(horizontalAlignment = Alignment.End) {
                    PricePair(
                        label = "月均",
                        value = stock.monthlyAveragePrice.ifBlank { "--" }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PricePair(label = "最高", value = stock.highestPrice.ifBlank { "--" })
                        PricePair(label = "最低", value = stock.lowestPrice.ifBlank { "--" })
                    }
                }
            }

            // ── Row 3: Volume summary ─────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                VolumeChip(label = "成交量", value = formatVolume(stock.tradeVolume))
                VolumeChip(label = "筆數", value = formatNumber(stock.transaction))
                VolumeChip(label = "金額", value = formatTradeValue(stock.tradeValue))
            }
        }
    }
}

// ── Sub-composables ───────────────────────────────────────────────────────────

@Composable
private fun ChangeBadge(change: String, changeValue: Double?) {
    val isPositive = changeValue?.let { it > 0 }
    val bgColor = when (isPositive) {
        true -> StockRedLight
        false -> StockGreenLight
        null -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when (isPositive) {
        true -> StockRed
        false -> StockGreen
        null -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val icon = when (isPositive) {
        true -> Icons.AutoMirrored.Filled.TrendingUp
        false -> Icons.AutoMirrored.Filled.TrendingDown
        null -> Icons.AutoMirrored.Filled.TrendingFlat
    }
    val prefix = when (isPositive) {
        true -> "▲ "
        false -> "▼ "
        null -> ""
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "$prefix${change.ifBlank { "--" }}",
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

/** Label on top, value below — used for secondary price columns */
@Composable
private fun PricePair(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

/** Compact label + value chip for the bottom volume row */
@Composable
private fun VolumeChip(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun closingPriceColor(isAboveMonthlyAverage: Boolean?): Color {
    return when (isAboveMonthlyAverage) {
        true -> StockRed
        false -> StockGreen
        null -> MaterialTheme.colorScheme.onSurface
    }
}

private fun formatVolume(raw: String): String {
    val num = raw.replace(",", "").toLongOrNull() ?: return raw
    return when {
        num >= 100_000_000 -> "%.1f億".format(num / 100_000_000.0)
        num >= 10_000 -> "%.1f萬".format(num / 10_000.0)
        else -> raw
    }
}

private fun formatTradeValue(raw: String): String {
    val num = raw.replace(",", "").toLongOrNull() ?: return raw
    return when {
        num >= 100_000_000 -> "%.1f億".format(num / 100_000_000.0)
        num >= 10_000 -> "%.1f萬".format(num / 10_000.0)
        else -> raw
    }
}

private fun formatNumber(raw: String): String {
    val num = raw.replace(",", "").toLongOrNull() ?: return raw
    return "%,d".format(num)
}
