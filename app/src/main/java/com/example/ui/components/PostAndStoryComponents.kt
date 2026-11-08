package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.Comment
import com.example.data.model.Post
import com.example.data.model.Story
import com.example.ui.theme.NiborjaPrimary
import com.example.ui.theme.NiborjaSecondary
import com.example.ui.theme.NiborjaTertiaryGold

@Composable
fun StoryRow(
    stories: List<Story>,
    currentUserName: String,
    currentUserAvatar: String,
    onAddStoryClick: () -> Unit,
    onStoryClick: (Story) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Add to Story Item
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onAddStoryClick() }
                        .testTag("add_story_button")
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Surface(
                            modifier = Modifier.size(62.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            if (currentUserAvatar.isNotBlank()) {
                                AsyncImage(
                                    model = currentUserAvatar,
                                    contentDescription = "My Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.size(22.dp),
                            shape = CircleShape,
                            color = NiborjaPrimary,
                            border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.surface)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Story",
                                tint = Color.White,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your Story",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Other User Stories
            items(stories) { story ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onStoryClick(story) }
                        .testTag("story_item_${story.id}")
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .border(
                                width = 2.5.dp,
                                brush = Brush.linearGradient(listOf(NiborjaPrimary, NiborjaSecondary, NiborjaTertiaryGold)),
                                shape = CircleShape
                            )
                            .padding(3.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            AsyncImage(
                                model = story.authorAvatar.ifBlank { story.mediaUrl },
                                contentDescription = story.authorName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = story.authorName.split(" ").firstOrNull() ?: "Creator",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun StoryViewerDialog(
    story: Story,
    onDismiss: () -> Unit,
    onSendReaction: (String) -> Unit
) {
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(story) {
        val totalMs = 5000L
        val intervalMs = 50L
        var elapsed = 0L
        while (elapsed < totalMs) {
            kotlinx.coroutines.delay(intervalMs)
            elapsed += intervalMs
            progress = elapsed.toFloat() / totalMs
        }
        onDismiss()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("story_viewer_dialog")
        ) {
            // Story Media
            AsyncImage(
                model = story.mediaUrl,
                contentDescription = story.caption,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dark Top Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                        )
                    )
            )

            // Top Header: Progress Bar & Creator info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f),
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape
                        ) {
                            AsyncImage(
                                model = story.authorAvatar,
                                contentDescription = story.authorName,
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = story.authorName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "24h Story",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
            }

            // Caption & Quick Reactions at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
                    .padding(16.dp)
            ) {
                if (story.caption.isNotBlank()) {
                    Text(
                        text = story.caption,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val emojis = listOf("❤️", "🔥", "👏", "😍", "⭐")
                    emojis.forEach { emoji ->
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier
                                .size(44.dp)
                                .clickable {
                                    onSendReaction(emoji)
                                    onDismiss()
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = emoji, fontSize = 22.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    comments: List<Comment>,
    currentUserId: String,
    onLikeClick: () -> Unit,
    onAddComment: (String) -> Unit,
    onSendGiftClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null
) {
    var isLiked by remember { mutableStateOf(false) }
    var likesCount by remember { mutableStateOf(post.likesCount) }
    var showCommentBox by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    var showReactionsMenu by remember { mutableStateOf(false) }
    var selectedReaction by remember { mutableStateOf("👍") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("post_card_${post.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        if (post.authorAvatar.isNotBlank()) {
                            AsyncImage(
                                model = post.authorAvatar,
                                contentDescription = post.authorName,
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = NiborjaPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "@${post.authorUsername.ifBlank { "creator" }}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = " • Just now • ",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Icon(
                                imageVector = when (post.audience) {
                                    "friends" -> Icons.Default.Group
                                    "private" -> Icons.Default.Lock
                                    else -> Icons.Default.Public
                                },
                                contentDescription = post.audience,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (post.userId == currentUserId && onDeleteClick != null) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Post",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Content text
            Text(
                text = post.content,
                fontSize = 14.5.sp,
                lineHeight = 21.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Media attachment if present
            if (post.mediaUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = post.mediaUrl,
                    contentDescription = "Post Media",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 260.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(selectedReaction, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$likesCount",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${comments.size} comments • ${post.sharesCount} shares",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
            )

            // Reaction bar with animated menu
            AnimatedVisibility(visible = showReactionsMenu) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val reactions = listOf("👍", "❤️", "🤗", "😂", "😮", "😢", "😡")
                        reactions.forEach { r ->
                            Text(
                                text = r,
                                fontSize = 22.sp,
                                modifier = Modifier
                                    .clickable {
                                        selectedReaction = r
                                        if (!isLiked) {
                                            isLiked = true
                                            likesCount++
                                            onLikeClick()
                                        }
                                        showReactionsMenu = false
                                    }
                                    .padding(2.dp)
                            )
                        }
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like Button
                TextButton(
                    onClick = {
                        if (!isLiked) {
                            isLiked = true
                            likesCount++
                            onLikeClick()
                        } else {
                            showReactionsMenu = !showReactionsMenu
                        }
                    },
                    modifier = Modifier.testTag("like_post_${post.id}")
                ) {
                    Text(selectedReaction, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isLiked) "Reacted" else "Like",
                        fontWeight = if (isLiked) FontWeight.Bold else FontWeight.Medium,
                        color = if (isLiked) NiborjaPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Comment Button
                TextButton(
                    onClick = { showCommentBox = !showCommentBox },
                    modifier = Modifier.testTag("comment_post_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Comment",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Tip / Send Gift Button
                FilledTonalButton(
                    onClick = onSendGiftClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = NiborjaTertiaryGold.copy(alpha = 0.15f),
                        contentColor = NiborjaTertiaryGold
                    ),
                    modifier = Modifier.testTag("tip_creator_${post.id}")
                ) {
                    Text("⭐ Gift", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Inline Comments Section
            AnimatedVisibility(visible = showCommentBox) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    // Input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = { Text("Write a comment...", fontSize = 13.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("comment_input_${post.id}"),
                            shape = RoundedCornerShape(20.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (commentText.isNotBlank()) {
                                    onAddComment(commentText)
                                    commentText = ""
                                }
                            },
                            modifier = Modifier.testTag("submit_comment_${post.id}")
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = NiborjaPrimary)
                        }
                    }

                    // Existing comments
                    if (comments.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            comments.take(3).forEach { c ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(28.dp),
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            if (c.authorAvatar.isNotBlank()) {
                                                AsyncImage(
                                                    model = c.authorAvatar,
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Crop
                                                )
                                            } else {
                                                Icon(imageVector = Icons.Default.Person, contentDescription = null)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = c.authorName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = c.content,
                                                fontSize = 13.sp
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
    }
}
