package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CreatorEarning
import com.example.data.model.PayoutRequest
import com.example.data.model.Post
import com.example.ui.theme.NiborjaPrimary
import com.example.ui.theme.NiborjaSecondary
import com.example.ui.theme.NiborjaTertiaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    earnings: List<CreatorEarning>,
    payoutRequests: List<PayoutRequest>,
    posts: List<Post>,
    onApprovePayout: (PayoutRequest) -> Unit,
    onRejectPayout: (PayoutRequest) -> Unit,
    onDeletePost: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    var selectedAdminTab by remember { mutableStateOf("Financials") }

    val totalCreatorDisbursed = earnings.sumOf { it.creatorShareUsd }
    val totalAdminRevenue = earnings.sumOf { it.adminShareUsd }
    val grossVolume = totalCreatorDisbursed + totalAdminRevenue

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = NiborjaPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Admin Control Panel", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("admin_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                // Admin Segmented Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Financials", "Payout Approvals", "Moderation").forEach { tab ->
                        val isSelected = selectedAdminTab == tab
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedAdminTab = tab },
                            label = { Text(tab, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NiborjaPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            when (selectedAdminTab) {
                "Financials" -> {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text("70/30 Platform Financial Overview", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Admin Revenue (30%)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("$${String.format("%.2f", totalAdminRevenue)} USD", fontSize = 20.sp, fontWeight = FontWeight.Black, color = NiborjaSecondary)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Creator Disbursed (70%)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("$${String.format("%.2f", totalCreatorDisbursed)} USD", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Divider()
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Gross Star Gift Volume:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Text("$${String.format("%.2f", grossVolume)} USD", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }

                    item {
                        Text("Active Platform Metrics", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricCard(title = "Total Posts", value = "${posts.size}", modifier = Modifier.weight(1f))
                            MetricCard(title = "Total Gifts Sent", value = "${earnings.size}", modifier = Modifier.weight(1f))
                            MetricCard(title = "Payout Requests", value = "${payoutRequests.size}", modifier = Modifier.weight(1f))
                        }
                    }
                }

                "Payout Approvals" -> {
                    item {
                        Text("Pending Creator Withdrawals", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            text = "Review and authorize creator fund disbursements per anti-fraud policy",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (payoutRequests.isEmpty()) {
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "No pending withdrawal requests.",
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    } else {
                        items(payoutRequests) { req ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(req.creatorName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text("$${String.format("%.2f", req.amountUsd)} USD • ${req.method}", color = Color(0xFF10B981), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        }

                                        Surface(
                                            color = when (req.status) {
                                                "approved", "processed" -> Color(0xFF10B981).copy(alpha = 0.15f)
                                                else -> NiborjaTertiaryGold.copy(alpha = 0.15f)
                                            },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = req.status.uppercase(),
                                                color = when (req.status) {
                                                    "approved", "processed" -> Color(0xFF10B981)
                                                    else -> NiborjaTertiaryGold
                                                },
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Details: ${req.accountDetails}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (req.status == "pending") {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            OutlinedButton(
                                                onClick = { onRejectPayout(req) },
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                            ) {
                                                Text("Reject")
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                onClick = { onApprovePayout(req) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                            ) {
                                                Text("Approve Payout")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Moderation" -> {
                    item {
                        Text("Content & User Moderation", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Enforce community guidelines and remove prohibited content", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    items(posts.take(8)) { post ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(post.authorName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(post.content, fontSize = 12.sp, maxLines = 1, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { onDeletePost(post.id) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove Post", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
