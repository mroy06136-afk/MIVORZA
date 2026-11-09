package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.data.model.ReportItem
import com.example.data.model.UserProfile
import com.example.ui.theme.NiborjaPrimary
import com.example.ui.theme.NiborjaSecondary
import com.example.ui.theme.NiborjaTertiaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    earnings: List<CreatorEarning>,
    payoutRequests: List<PayoutRequest>,
    posts: List<Post>,
    users: List<UserProfile> = emptyList(),
    reports: List<ReportItem> = emptyList(),
    onApprovePayout: (PayoutRequest) -> Unit,
    onRejectPayout: (PayoutRequest) -> Unit,
    onDeletePost: (String) -> Unit,
    onToggleVerification: (String, Boolean) -> Unit = { _, _ -> },
    onToggleCreatorMode: (String, Boolean) -> Unit = { _, _ -> },
    onUpdateUserStatus: (String, String) -> Unit = { _, _ -> },
    onResolveReport: (String, String) -> Unit = { _, _ -> },
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
                        Text("Admin Control Panel", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                Spacer(modifier = Modifier.height(4.dp))
                // Horizontal scrollable tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tabs = listOf("Financials", "Payout Approvals", "User Management", "Moderation", "Reports & Complaints", "Analytics")
                    tabs.forEach { tab ->
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
                                HorizontalDivider()
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
                            MetricCard(title = "Gifts Sent", value = "${earnings.size}", modifier = Modifier.weight(1f))
                            MetricCard(title = "Pending Payouts", value = "${payoutRequests.count { it.status == "pending" }}", modifier = Modifier.weight(1f))
                        }
                    }

                    item {
                        Text("Recent Gift & Monetization Transactions", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    if (earnings.isEmpty()) {
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("No transactions recorded yet.", fontSize = 13.sp, modifier = Modifier.padding(16.dp))
                            }
                        }
                    } else {
                        items(earnings.take(10)) { earning ->
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
                                    Column {
                                        Text("${earning.giftName} (${earning.starAmount} ⭐)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("From: ${earning.senderName} • To: ${earning.creatorId.take(8)}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Creator (70%): +$${String.format("%.2f", earning.creatorShareUsd)}", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("Admin (30%): +$${String.format("%.2f", earning.adminShareUsd)}", color = NiborjaSecondary, fontSize = 11.sp)
                                    }
                                }
                            }
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

                "User Management" -> {
                    item {
                        Text("Platform Users & Creator Status", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Manage verification badges, creator studio privileges and user status", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    if (users.isEmpty()) {
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("No registered users found in directory.", fontSize = 13.sp, modifier = Modifier.padding(16.dp))
                            }
                        }
                    } else {
                        items(users) { user ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(user.displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                if (user.isVerified) {
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = NiborjaPrimary, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                            Text("@${user.username} • ${user.creatorCategory}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }

                                        Surface(
                                            color = if (user.status == "banned") MaterialTheme.colorScheme.error.copy(alpha = 0.15f) else Color(0xFF10B981).copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = user.status.uppercase(),
                                                color = if (user.status == "banned") MaterialTheme.colorScheme.error else Color(0xFF10B981),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { onToggleVerification(user.userId, !user.isVerified) },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(if (user.isVerified) "Unverify" else "Verify", fontSize = 11.sp)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                val nextStatus = if (user.status == "active") "suspended" else "active"
                                                onUpdateUserStatus(user.userId, nextStatus)
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = if (user.status == "active") MaterialTheme.colorScheme.error else Color(0xFF10B981)
                                            )
                                        ) {
                                            Text(if (user.status == "active") "Suspend" else "Activate", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Moderation" -> {
                    item {
                        Text("Content Moderation", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Enforce community guidelines and remove prohibited content", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    items(posts.take(15)) { post ->
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
                                    Text(post.content, fontSize = 12.sp, maxLines = 2, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("Likes: ${post.likesCount} • Comments: ${post.commentsCount}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { onDeletePost(post.id) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove Post", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }

                "Reports & Complaints" -> {
                    item {
                        Text("User Complaints & Reports", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Resolve abuse reports submitted by community members", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    if (reports.isEmpty()) {
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("No pending user complaints or reports. Clean record!", fontSize = 13.sp, modifier = Modifier.padding(16.dp))
                            }
                        }
                    } else {
                        items(reports) { rep ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Target: ${rep.targetType.uppercase()}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                                        Text("By: ${rep.reporterName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Reason: ${rep.reason}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    if (rep.targetContent.isNotBlank()) {
                                        Text("Content: \"${rep.targetContent}\"", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                        TextButton(onClick = { onResolveReport(rep.id, "dismissed") }) {
                                            Text("Dismiss")
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Button(
                                            onClick = { onResolveReport(rep.id, "resolved") },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                        ) {
                                            Text("Resolve")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Analytics" -> {
                    item {
                        Text("Platform Analytics & Creator Economy", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Platform Growth Summary", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                MetricRow(label = "Platform Creators Mode", value = "${users.count { it.isCreatorMode }} active")
                                MetricRow(label = "Verified Creators", value = "${users.count { it.isVerified }} verified")
                                MetricRow(label = "Published Posts & Content", value = "${posts.size} items")
                                MetricRow(label = "Total Gifts Sent", value = "${earnings.size} transactions")
                                MetricRow(label = "Creator Revenue Share", value = "70.0%")
                                MetricRow(label = "Platform Revenue Share", value = "30.0%")
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

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
