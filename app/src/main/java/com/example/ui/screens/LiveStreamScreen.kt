package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.LiveStream
import com.example.ui.theme.NiborjaPrimary
import com.example.ui.theme.NiborjaTertiaryGold

@Composable
fun LiveStreamScreen(
    liveStreams: List<LiveStream>,
    currentUserName: String,
    onStartStream: (String) -> Unit,
    onEndStream: (String) -> Unit,
    onSendGiftToHost: (String, String) -> Unit,
    onCloseLive: () -> Unit
) {
    BackHandler {
        onCloseLive()
    }

    var isBroadcasting by remember { mutableStateOf(false) }
    var activeStreamTitle by remember { mutableStateOf("Live Studio Q&A • Connect & Earn") }
    var viewerCount by remember { mutableStateOf(1420L) }
    var liveCommentInput by remember { mutableStateOf("") }
    val liveComments = remember {
        mutableStateListOf(
            Pair("Sarah Chen", "Watching from Tokyo! 🇯🇵 Excited for this stream!"),
            Pair("Marcus Vance", "That 70/30 split feature is incredible 🔥"),
            Pair("Alex Rivera", "Just sent a Dragon gift! 🐉"),
            Pair("David Kim", "Can you explain how payout verification works?")
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("live_stream_screen")
    ) {
        // Broadcaster Camera Video stream simulation
        AsyncImage(
            model = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1000",
            contentDescription = "Live Video Stream",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Top Stream Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEF4444)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("●", color = Color.White, fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("LIVE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "$viewerCount", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            IconButton(
                onClick = onCloseLive,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Exit Live", tint = Color.White)
            }
        }

        // Host Info
        Surface(
            color = Color.Black.copy(alpha = 0.45f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 56.dp, start = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(28.dp),
                    shape = CircleShape
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Elena Rostova", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(activeStreamTitle, color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, maxLines = 1)
                }
            }
        }

        // Live Comments Stream (Bottom Left)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 80.dp, end = 70.dp)
                .heightIn(max = 240.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(liveComments) { (user, comment) ->
                    Surface(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "$user: ", color = NiborjaTertiaryGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(text = comment, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Floating Right Action Buttons (Gift, Like Heart, Share)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Send Gift Button
            IconButton(
                onClick = {
                    onSendGiftToHost("elena_host", "Elena Rostova")
                    liveComments.add(Pair(currentUserName, "Sent a Tiger Gift! 🐯 (70% split)"))
                },
                modifier = Modifier
                    .background(NiborjaTertiaryGold, CircleShape)
                    .size(48.dp)
                    .testTag("live_send_gift_button")
            ) {
                Text("⭐", fontSize = 22.sp)
            }

            // Floating Heart Reaction
            IconButton(
                onClick = {
                    viewerCount++
                    liveComments.add(Pair(currentUserName, "Sent ❤️ reaction"))
                },
                modifier = Modifier
                    .background(Color(0xFFEF4444), CircleShape)
                    .size(44.dp)
            ) {
                Icon(imageVector = Icons.Default.Favorite, contentDescription = "Heart", tint = Color.White)
            }
        }

        // Bottom Live Comment Input
        Surface(
            color = Color.Black.copy(alpha = 0.75f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = liveCommentInput,
                    onValueChange = { liveCommentInput = it },
                    placeholder = { Text("Comment live...", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("live_comment_input"),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NiborjaPrimary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (liveCommentInput.isNotBlank()) {
                            liveComments.add(Pair(currentUserName, liveCommentInput))
                            liveCommentInput = ""
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = NiborjaPrimary)
                }
            }
        }
    }
}
