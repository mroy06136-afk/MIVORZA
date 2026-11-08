package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.NiborjaPrimary
import com.example.ui.theme.NiborjaSecondary
import com.example.ui.theme.NiborjaTertiaryGold

@Composable
fun CreateContentDialog(
    onDismiss: () -> Unit,
    onCreatePost: (content: String, mediaUrl: String, audience: String) -> Unit,
    onCreateStory: (mediaUrl: String, caption: String) -> Unit,
    onCreateReel: (videoUrl: String, caption: String, audio: String) -> Unit,
    onStartLive: (title: String) -> Unit
) {
    var selectedType by remember { mutableStateOf("Post") } // Post, Story, Reel, Live
    var textInput by remember { mutableStateOf("") }
    var mediaUrlInput by remember { mutableStateOf("") }
    var audience by remember { mutableStateOf("public") }
    var audioTitle by remember { mutableStateOf("Original Sound - MIVORZA Studio") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().padding(14.dp).testTag("create_content_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Create on MIVORZA",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Type Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Post", "Story", "Reel", "Live").forEach { type ->
                        val isSelected = selectedType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedType = type },
                            label = { Text(type, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (type) {
                                    "Story" -> Color(0xFF10B981)
                                    "Reel" -> NiborjaSecondary
                                    "Live" -> Color(0xFFEF4444)
                                    else -> NiborjaPrimary
                                },
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                when (selectedType) {
                    "Post" -> {
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = { Text("What's on your mind? Share thoughts, links, updates...") },
                            modifier = Modifier.fillMaxWidth().testTag("post_text_input"),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 3
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = mediaUrlInput,
                            onValueChange = { mediaUrlInput = it },
                            placeholder = { Text("Photo or Media URL (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Audience: ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            listOf("public", "friends", "private").forEach { aud ->
                                val isAudSelected = audience == aud
                                FilterChip(
                                    selected = isAudSelected,
                                    onClick = { audience = aud },
                                    label = { Text(aud.capitalize(), fontSize = 11.sp) },
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }
                    }

                    "Story" -> {
                        OutlinedTextField(
                            value = mediaUrlInput,
                            onValueChange = { mediaUrlInput = it },
                            placeholder = { Text("Story Photo URL (e.g. Unsplash URL)") },
                            modifier = Modifier.fillMaxWidth().testTag("story_url_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = { Text("Caption or Sticker text (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    "Reel" -> {
                        OutlinedTextField(
                            value = mediaUrlInput,
                            onValueChange = { mediaUrlInput = it },
                            placeholder = { Text("Short Video URL or Thumbnail Poster") },
                            modifier = Modifier.fillMaxWidth().testTag("reel_url_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = { Text("Reel Caption & Hashtags (#Viral #Reel)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = audioTitle,
                            onValueChange = { audioTitle = it },
                            placeholder = { Text("Audio Track Title") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    "Live" -> {
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = { Text("Live Broadcast Title & Topic...") },
                            modifier = Modifier.fillMaxWidth().testTag("live_title_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Going live activates the 70/30 gift shower engine. Real-time viewers can send stars and virtual gifts.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
                            when (selectedType) {
                                "Post" -> {
                                    if (textInput.isNotBlank()) {
                                        onCreatePost(textInput, mediaUrlInput, audience)
                                    }
                                }
                                "Story" -> {
                                    val url = mediaUrlInput.ifBlank { "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800" }
                                    onCreateStory(url, textInput)
                                }
                                "Reel" -> {
                                    val url = mediaUrlInput.ifBlank { "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=800" }
                                    onCreateReel(url, textInput.ifBlank { "New MIVORZA Reel #Creator" }, audioTitle)
                                }
                                "Live" -> {
                                    onStartLive(textInput.ifBlank { "Live Broadcasting on MIVORZA" })
                                }
                            }
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NiborjaPrimary),
                        modifier = Modifier.testTag("publish_content_button")
                    ) {
                        Text("Publish")
                    }
                }
            }
        }
    }
}
