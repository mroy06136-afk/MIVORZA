package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CreatorEarning
import com.example.data.model.PayoutRequest
import com.example.data.model.UserProfile
import com.example.ui.theme.NiborjaPrimary
import com.example.ui.theme.NiborjaSecondary
import com.example.ui.theme.NiborjaTertiaryGold

@Composable
fun CreatorStudioScreen(
    userProfile: UserProfile?,
    earnings: List<CreatorEarning>,
    payoutRequests: List<PayoutRequest>,
    onRequestPayout: (amount: Double, method: String, details: String) -> Unit
) {
    var selectedTimeframe by remember { mutableStateOf("Last 30 Days") }
    var showPayoutDialog by remember { mutableStateOf(false) }

    // Aggregate calculations for 70/30 split
    val totalCreatorEarningsUsd = earnings.sumOf { it.creatorShareUsd }
    val totalAdminShareUsd = earnings.sumOf { it.adminShareUsd }
    val totalGrossUsd = totalCreatorEarningsUsd + totalAdminShareUsd

    if (showPayoutDialog) {
        PayoutRequestDialog(
            availableBalanceUsd = totalCreatorEarningsUsd,
            onDismiss = { showPayoutDialog = false },
            onSubmit = { amount, method, details ->
                onRequestPayout(amount, method, details)
                showPayoutDialog = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("creator_studio_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Professional Dashboard",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Creator",
                            tint = NiborjaPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Real-Time 70/30 Monetization & Performance Engine",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Timeframe selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Today", "Last 7 Days", "Last 30 Days", "All Time").forEach { time ->
                    val isSelected = selectedTimeframe == time
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTimeframe = time },
                        label = { Text(time, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NiborjaSecondary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Hero 70/30 Earnings Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    NiborjaPrimary.copy(alpha = 0.15f),
                                    NiborjaSecondary.copy(alpha = 0.15f),
                                    NiborjaTertiaryGold.copy(alpha = 0.15f)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Available Creator Net Balance",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                color = NiborjaTertiaryGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "70% Net Share",
                                    color = NiborjaTertiaryGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "$${String.format("%.2f", totalCreatorEarningsUsd)} USD",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // 70/30 Split Breakdown bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Creator Share (70%)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$${String.format("%.2f", totalCreatorEarningsUsd)}", fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Platform Share (30%)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$${String.format("%.2f", totalAdminShareUsd)}", fontWeight = FontWeight.Bold, color = NiborjaSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showPayoutDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("request_withdrawal_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NiborjaPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Request Payout / Withdrawal", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Performance Metrics Grid
        item {
            Text(
                text = "Performance & Reach",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AnalyticsMetricCard(
                    title = "Post Reach",
                    value = "48.2K",
                    change = "+24.8%",
                    icon = Icons.Default.TrendingUp,
                    modifier = Modifier.weight(1f)
                )
                AnalyticsMetricCard(
                    title = "Engagement",
                    value = "8.4%",
                    change = "+3.2%",
                    icon = Icons.Default.ThumbUp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AnalyticsMetricCard(
                    title = "Video Views",
                    value = "128.5K",
                    change = "+42.1%",
                    icon = Icons.Default.Visibility,
                    modifier = Modifier.weight(1f)
                )
                AnalyticsMetricCard(
                    title = "Watch Time",
                    value = "3,410 hrs",
                    change = "+18.9%",
                    icon = Icons.Default.AccessTime,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 70/30 Virtual Gift Transactions Ledger
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Gift Revenue Ledger",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${earnings.size} transactions",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (earnings.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No gift transactions yet. Live streams and reels will record your 70% share here!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(earnings.take(6)) { earning ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = NiborjaTertiaryGold.copy(alpha = 0.15f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("⭐", fontSize = 18.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${earning.giftName} from ${earning.senderName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${earning.starAmount} Stars • 70% Split",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "+$${String.format("%.2f", earning.creatorShareUsd)}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981),
                                fontSize = 14.5.sp
                            )
                            Text(
                                text = "Admin: $${String.format("%.2f", earning.adminShareUsd)}",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Payout Requests History
        item {
            Text(
                text = "Payout Requests",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (payoutRequests.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No withdrawal requests yet. Minimum $25 local bank / $100 wire transfer.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(payoutRequests) { req ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$${String.format("%.2f", req.amountUsd)} USD",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "${req.method} • ${req.accountDetails.take(20)}...",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (req.status) {
                                "approved", "processed" -> Color(0xFF10B981).copy(alpha = 0.15f)
                                "rejected" -> Color(0xFFEF4444).copy(alpha = 0.15f)
                                else -> NiborjaTertiaryGold.copy(alpha = 0.15f)
                            }
                        ) {
                            Text(
                                text = req.status.uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = when (req.status) {
                                    "approved", "processed" -> Color(0xFF10B981)
                                    "rejected" -> Color(0xFFEF4444)
                                    else -> NiborjaTertiaryGold
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
private fun AnalyticsMetricCard(
    title: String,
    value: String,
    change: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(imageVector = icon, contentDescription = null, tint = NiborjaPrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = change, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF10B981))
        }
    }
}

@Composable
fun PayoutRequestDialog(
    availableBalanceUsd: Double,
    onDismiss: () -> Unit,
    onSubmit: (Double, String, String) -> Unit
) {
    var amountText by remember { mutableStateOf(if (availableBalanceUsd > 25.0) "25.00" else "") }
    var selectedMethod by remember { mutableStateOf("Local Bank Transfer") }
    var accountDetails by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Request Payout",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Minimum $25 Local Bank / $100 International Wire",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount in USD ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Payout Method", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Local Bank Transfer", "International Wire").forEach { m ->
                        val isSelected = selectedMethod == m
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedMethod = m },
                            label = { Text(m, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = accountDetails,
                    onValueChange = { accountDetails = it },
                    label = { Text("Bank Name, Account #, Routing / IFSC / IBAN") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2
                )

                errorMsg?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amount = amountText.toDoubleOrNull()
                            if (amount == null || amount <= 0) {
                                errorMsg = "Enter a valid payout amount"
                                return@Button
                            }
                            val minAmount = if (selectedMethod == "International Wire") 100.0 else 25.0
                            if (amount < minAmount) {
                                errorMsg = "Minimum payout for $selectedMethod is $$minAmount"
                                return@Button
                            }
                            if (accountDetails.isBlank()) {
                                errorMsg = "Please provide your beneficiary account details"
                                return@Button
                            }
                            onSubmit(amount, selectedMethod, accountDetails)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NiborjaPrimary)
                    ) {
                        Text("Submit Request")
                    }
                }
            }
        }
    }
}
