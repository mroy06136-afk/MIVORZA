package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NiborjaPrimary
import com.example.ui.theme.NiborjaSecondary
import com.example.ui.theme.NiborjaTertiaryGold

enum class ScreenTab {
    FEED,
    REELS,
    CREATE,
    MARKETPLACE,
    STUDIO,
    PROFILE,
    MESSENGER,
    LIVE_ROOM,
    ADMIN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NiborjaTopBar(
    starsBalance: Long,
    onOpenStarShop: () -> Unit,
    onOpenMessenger: () -> Unit,
    onOpenLive: () -> Unit,
    onOpenSearch: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Gradient N brand badge
                Surface(
                    modifier = Modifier.size(34.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(listOf(NiborjaPrimary, NiborjaSecondary, NiborjaTertiaryGold))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "M",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "MIVORZA",
                        fontWeight = FontWeight.Black,
                        fontSize = 19.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "CONNECT • SHARE • CREATE • EARN",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        },
        actions = {
            // Star Wallet Balance Chip
            Surface(
                color = NiborjaTertiaryGold.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .clickable { onOpenStarShop() }
                    .testTag("topbar_star_chip")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⭐", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$starsBalance",
                        fontWeight = FontWeight.Bold,
                        color = NiborjaTertiaryGold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Live Broadcast Action
            IconButton(onClick = onOpenLive, modifier = Modifier.testTag("topbar_live_button")) {
                Icon(
                    imageVector = Icons.Default.LiveTv,
                    contentDescription = "Go Live",
                    tint = Color(0xFFEF4444)
                )
            }

            // Messenger Action
            IconButton(onClick = onOpenMessenger, modifier = Modifier.testTag("topbar_messenger_button")) {
                BadgedBox(
                    badge = {
                        Badge(containerColor = NiborjaPrimary) {
                            Text("3", fontSize = 10.sp, color = Color.White)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Chat,
                        contentDescription = "Messenger"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun NiborjaBottomNav(
    currentTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit,
    onCreateClick: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("bottom_nav_bar")
    ) {
        NavigationBarItem(
            selected = currentTab == ScreenTab.FEED,
            onClick = { onTabSelected(ScreenTab.FEED) },
            icon = {
                Icon(
                    imageVector = if (currentTab == ScreenTab.FEED) Icons.Default.Home else Icons.Outlined.Home,
                    contentDescription = "Feed"
                )
            },
            label = { Text("Feed", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NiborjaPrimary,
                selectedTextColor = NiborjaPrimary
            )
        )

        NavigationBarItem(
            selected = currentTab == ScreenTab.REELS,
            onClick = { onTabSelected(ScreenTab.REELS) },
            icon = {
                Icon(
                    imageVector = if (currentTab == ScreenTab.REELS) Icons.Default.SlowMotionVideo else Icons.Outlined.SlowMotionVideo,
                    contentDescription = "Reels"
                )
            },
            label = { Text("Reels", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NiborjaSecondary,
                selectedTextColor = NiborjaSecondary
            )
        )

        // Center Floating Create Button
        NavigationBarItem(
            selected = false,
            onClick = onCreateClick,
            icon = {
                Surface(
                    shape = CircleShape,
                    color = NiborjaPrimary,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            label = { Text("Create", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
        )

        NavigationBarItem(
            selected = currentTab == ScreenTab.MARKETPLACE,
            onClick = { onTabSelected(ScreenTab.MARKETPLACE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == ScreenTab.MARKETPLACE) Icons.Default.Storefront else Icons.Outlined.Storefront,
                    contentDescription = "Market"
                )
            },
            label = { Text("Market", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NiborjaPrimary,
                selectedTextColor = NiborjaPrimary
            )
        )

        NavigationBarItem(
            selected = currentTab == ScreenTab.STUDIO,
            onClick = { onTabSelected(ScreenTab.STUDIO) },
            icon = {
                Icon(
                    imageVector = if (currentTab == ScreenTab.STUDIO) Icons.Default.DashboardCustomize else Icons.Outlined.DashboardCustomize,
                    contentDescription = "Studio"
                )
            },
            label = { Text("Studio", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NiborjaTertiaryGold,
                selectedTextColor = NiborjaTertiaryGold
            )
        )

        NavigationBarItem(
            selected = currentTab == ScreenTab.PROFILE,
            onClick = { onTabSelected(ScreenTab.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == ScreenTab.PROFILE) Icons.Default.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NiborjaPrimary,
                selectedTextColor = NiborjaPrimary
            )
        )
    }
}
