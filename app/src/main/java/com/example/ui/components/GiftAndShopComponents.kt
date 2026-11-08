package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.StarPackage
import com.example.data.model.VirtualGift
import com.example.ui.theme.NiborjaPrimary
import com.example.ui.theme.NiborjaSecondary
import com.example.ui.theme.NiborjaTertiaryGold

val MIVORZA_GIFTS = listOf(
    VirtualGift("gift_rose", "Rose", 10, "🌹", "small"),
    VirtualGift("gift_heart", "Heart", 50, "💖", "small"),
    VirtualGift("gift_tiger", "Tiger", 1000, "🐯", "medium"),
    VirtualGift("gift_elephant", "Elephant", 2000, "🐘", "high"),
    VirtualGift("gift_dragon", "Dragon", 3000, "🐉", "premium"),
    VirtualGift("gift_castle", "Luxury Castle", 5000, "🏰", "premium")
)

val NEXORA_GIFTS = MIVORZA_GIFTS
val NIBORJA_GIFTS = MIVORZA_GIFTS

val STAR_PACKAGES = listOf(
    StarPackage(30, 0.30, bonusStars = 10),
    StarPackage(80, 0.80, bonusStars = 20),
    StarPackage(150, 1.50, bonusStars = 50),
    StarPackage(300, 3.00),
    StarPackage(500, 5.00),
    StarPackage(1000, 10.00, isPopular = true),
    StarPackage(2000, 20.00),
    StarPackage(3000, 30.00),
    StarPackage(5000, 50.00)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftBottomSheet(
    creatorName: String,
    currentStarsBalance: Long,
    onDismiss: () -> Unit,
    onSendGift: (VirtualGift) -> Unit,
    onOpenStarShop: () -> Unit
) {
    var selectedGift by remember { mutableStateOf<VirtualGift?>(NEXORA_GIFTS.first()) }
    var showAnimation by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .testTag("gift_bottom_sheet")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Send Virtual Gift",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Supporting $creatorName • 70/30 Split",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Star Balance Badge
                Surface(
                    color = NiborjaTertiaryGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.clickable { onOpenStarShop() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⭐", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$currentStarsBalance",
                            fontWeight = FontWeight.Bold,
                            color = NiborjaTertiaryGold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Buy Stars",
                            tint = NiborjaTertiaryGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 70/30 Split Explanation Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = NiborjaTertiaryGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    val giftValUsd = (selectedGift?.stars ?: 10) * 0.01
                    val creatorShare = giftValUsd * 0.70
                    val adminShare = giftValUsd * 0.30
                    Text(
                        text = "Split for ${selectedGift?.name}: Creator gets $${String.format("%.2f", creatorShare)} (70%) • Platform fee $${String.format("%.2f", adminShare)} (30%)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid of Gifts
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(NEXORA_GIFTS) { gift ->
                    val isSelected = selectedGift == gift
                    val borderColor = if (isSelected) NiborjaPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    val bgColor = if (isSelected) NiborjaPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedGift = gift }
                            .testTag("gift_item_${gift.id}"),
                        colors = CardDefaults.cardColors(containerColor = bgColor)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = gift.emoji, fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = gift.name,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⭐", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${gift.stars}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NiborjaTertiaryGold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Send Button or Low Balance alert
            val selected = selectedGift
            val hasEnoughStars = selected != null && currentStarsBalance >= selected.stars

            if (hasEnoughStars) {
                Button(
                    onClick = {
                        selected?.let {
                            onSendGift(it)
                            showAnimation = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("send_gift_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NiborjaPrimary)
                ) {
                    Text(
                        text = "Send ${selected?.name} (⭐ ${selected?.stars} Stars)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            } else {
                Button(
                    onClick = { onOpenStarShop() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("recharge_stars_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NiborjaTertiaryGold)
                ) {
                    Icon(imageVector = Icons.Default.Stars, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Recharge Stars (Need ${selected?.stars ?: 0} Stars)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StarShopDialog(
    currentStarsBalance: Long,
    onDismiss: () -> Unit,
    onBuyPackage: (StarPackage) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("star_shop_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NiborjaTertiaryGold.copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("⭐", fontSize = 20.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "MIVORZA Star Shop",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "1 Star = $0.01 USD Baseline",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Current Balance Banner
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Current Wallet Balance", style = MaterialTheme.typography.bodyMedium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⭐", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$currentStarsBalance Stars",
                                fontWeight = FontWeight.Bold,
                                color = NiborjaTertiaryGold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Packages List
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    STAR_PACKAGES.forEach { pkg ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onBuyPackage(pkg)
                                    onDismiss()
                                }
                                .testTag("star_pkg_${pkg.stars}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (pkg.isPopular)
                                    NiborjaPrimary.copy(alpha = 0.12f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            border = if (pkg.isPopular)
                                androidx.compose.foundation.BorderStroke(1.5.dp, NiborjaPrimary)
                            else null,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⭐", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${pkg.stars} Stars",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                            if (pkg.bonusStars > 0) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = Color(0xFF10B981),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "+${pkg.bonusStars} BONUS",
                                                        color = Color.White,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                        if (pkg.isPopular) {
                                            Text(
                                                text = "Most Popular Creator Package",
                                                color = NiborjaPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                Surface(
                                    color = NiborjaPrimary,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "$${String.format("%.2f", pkg.priceUsd)}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
