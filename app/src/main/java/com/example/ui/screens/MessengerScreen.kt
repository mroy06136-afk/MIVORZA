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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.Conversation
import com.example.data.model.Message
import com.example.ui.theme.NiborjaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessengerScreen(
    currentUserId: String,
    conversations: List<Conversation>,
    activeMessages: List<Message>,
    activeConversationId: String?,
    onSelectConversation: (String) -> Unit,
    onSendMessage: (String, String) -> Unit,
    onBackToConversations: () -> Unit,
    onCloseMessenger: () -> Unit
) {
    var messageInput by remember { mutableStateOf("") }
    var showCallingDialog by remember { mutableStateOf(false) }
    var callType by remember { mutableStateOf("Voice") }

    if (showCallingDialog) {
        CallSimulationDialog(
            callType = callType,
            contactName = "Creator Partner",
            onEndCall = { showCallingDialog = false }
        )
    }

    // Handle back button when inside a conversation
    if (activeConversationId != null) {
        BackHandler {
            onBackToConversations()
        }
    } else {
        BackHandler {
            onCloseMessenger()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("messenger_screen")
    ) {
        if (activeConversationId == null) {
            // CONVERSATIONS LIST
            TopAppBar(
                title = { Text("Messages", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onCloseMessenger) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* New conversation */ }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "New Message")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )

            // Online Creators Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                listOf(
                    Pair("Elena Rostova", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200"),
                    Pair("Marcus Vance", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200"),
                    Pair("Sarah Chen", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200")
                ).forEach { (name, avatar) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            onSelectConversation("conv_demo")
                        }
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Surface(
                                modifier = Modifier.size(50.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                AsyncImage(
                                    model = avatar,
                                    contentDescription = name,
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Surface(
                                modifier = Modifier.size(14.dp),
                                shape = CircleShape,
                                color = Color(0xFF10B981),
                                border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.surface)
                            ) {}
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = name.split(" ").first(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (conversations.isEmpty()) {
                    item {
                        // Demo conversation items for instantaneous preview
                        DemoConversationItem(
                            name = "Elena Rostova",
                            avatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
                            lastMessage = "Thank you so much for the Luxury Castle gift in the stream! 🏰✨",
                            time = "2m ago",
                            unread = true,
                            onClick = { onSelectConversation("demo_elena") }
                        )
                        DemoConversationItem(
                            name = "Marcus Vance",
                            avatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200",
                            lastMessage = "Hey! Is your condenser microphone still available on Market?",
                            time = "1h ago",
                            unread = false,
                            onClick = { onSelectConversation("demo_marcus") }
                        )
                        DemoConversationItem(
                            name = "Sarah Chen",
                            avatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200",
                            lastMessage = "Streaming live Tokyo nightlife soon! Join the audio lounge.",
                            time = "Yesterday",
                            unread = false,
                            onClick = { onSelectConversation("demo_sarah") }
                        )
                    }
                } else {
                    items(conversations) { conv ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectConversation(conv.id) }
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(46.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(10.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Conversation",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = conv.lastMessage.ifBlank { "No messages yet" },
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // ACTIVE 1-ON-1 CHAT ROOM
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape
                        ) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Elena Rostova", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Active now", fontSize = 11.sp, color = Color(0xFF10B981))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackToConversations) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        callType = "Voice"
                        showCallingDialog = true
                    }) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "Voice Call")
                    }
                    IconButton(onClick = {
                        callType = "Video"
                        showCallingDialog = true
                    }) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = "Video Call")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )

            // Chat Messages Stream
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                reverseLayout = true
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (activeMessages.isEmpty()) {
                    item {
                        // Sample chat bubbles
                        MessageBubble(text = "Hello! Loved your latest reel!", isOutgoing = true)
                        MessageBubble(text = "Thank you so much! Really appreciate your support on MIVORZA Studio 💖", isOutgoing = false)
                        MessageBubble(text = "Sent you a Rose gift! Keep up the amazing work! 🌹", isOutgoing = true)
                    }
                } else {
                    items(activeMessages.reversed()) { msg ->
                        MessageBubble(
                            text = msg.text,
                            isOutgoing = msg.senderId == currentUserId
                        )
                    }
                }
            }

            // Message Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth().navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* attachments */ }) {
                        Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = "Attach Photo", tint = NiborjaPrimary)
                    }

                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = { Text("Message...", fontSize = 14.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = {
                            if (messageInput.isNotBlank()) {
                                onSendMessage(activeConversationId, messageInput)
                                messageInput = ""
                            }
                        },
                        modifier = Modifier.testTag("send_message_button")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = NiborjaPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(text: String, isOutgoing: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isOutgoing) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isOutgoing) NiborjaPrimary else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isOutgoing) 16.dp else 2.dp,
                bottomEnd = if (isOutgoing) 2.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = text,
                color = if (isOutgoing) Color.White else MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
            )
        }
    }
}

@Composable
private fun DemoConversationItem(
    name: String,
    avatar: String,
    lastMessage: String,
    time: String,
    unread: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape
            ) {
                AsyncImage(model = avatar, contentDescription = name, contentScale = ContentScale.Crop)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = time, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = lastMessage,
                    fontSize = 13.sp,
                    color = if (unread) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (unread) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun CallSimulationDialog(
    callType: String,
    contactName: String,
    onEndCall: () -> Unit
) {
    Dialog(onDismissRequest = onEndCall) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F172A),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "MIVORZA $callType Call",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(3.dp, NiborjaPrimary)
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
                        contentDescription = contactName,
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = contactName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Ringing...", color = Color(0xFF10B981), fontSize = 13.sp)

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = { /* mute */ },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Mic, contentDescription = "Mute", tint = Color.White)
                    }

                    IconButton(
                        onClick = onEndCall,
                        modifier = Modifier
                            .background(Color(0xFFEF4444), CircleShape)
                            .size(54.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CallEnd, contentDescription = "End Call", tint = Color.White)
                    }

                    IconButton(
                        onClick = { /* speaker */ },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Speaker", tint = Color.White)
                    }
                }
            }
        }
    }
}
