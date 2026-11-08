package com.example.ui.screens

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
import coil.compose.AsyncImage
import com.example.data.model.Comment
import com.example.data.model.Post
import com.example.data.model.Story
import com.example.data.model.UserProfile
import com.example.data.model.VirtualGift
import com.example.ui.components.PostCard
import com.example.ui.components.StoryRow
import com.example.ui.components.StoryViewerDialog
import com.example.ui.theme.NiborjaPrimary
import com.example.ui.theme.NiborjaSecondary
import com.example.ui.theme.NiborjaTertiaryGold

@Composable
fun FeedScreen(
    userProfile: UserProfile?,
    posts: List<Post>,
    stories: List<Story>,
    commentsMap: Map<String, List<Comment>>,
    onLikePost: (Post) -> Unit,
    onAddComment: (String, String) -> Unit,
    onOpenCreatePost: () -> Unit,
    onOpenAddStory: () -> Unit,
    onSendGift: (Post) -> Unit,
    onDeletePost: (String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("For You") }
    var activeStoryToView by remember { mutableStateOf<Story?>(null) }

    activeStoryToView?.let { story ->
        StoryViewerDialog(
            story = story,
            onDismiss = { activeStoryToView = null },
            onSendReaction = { emoji ->
                // Handle story reaction
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("feed_screen")
    ) {
        // Feed Filter Tabs: For You, Following, Trending
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("For You", "Following", "Trending").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                text = filter,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NiborjaPrimary,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }

        // Stories Row at top
        item {
            StoryRow(
                stories = stories,
                currentUserName = userProfile?.displayName ?: "Creator",
                currentUserAvatar = userProfile?.avatarUrl ?: "",
                onAddStoryClick = onOpenAddStory,
                onStoryClick = { activeStoryToView = it }
            )
        }

        // Create Post Quick Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clickable { onOpenCreatePost() }
                    .testTag("create_post_trigger_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            if (!userProfile?.avatarUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = userProfile!!.avatarUrl,
                                    contentDescription = "My Avatar",
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "What's on your mind, ${userProfile?.displayName?.split(" ")?.firstOrNull() ?: "Creator"}?",
                                fontSize = 13.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        QuickActionItem(icon = Icons.Default.PhotoLibrary, text = "Photo", color = Color(0xFF10B981))
                        QuickActionItem(icon = Icons.Default.Videocam, text = "Video", color = NiborjaSecondary)
                        QuickActionItem(icon = Icons.Default.MonetizationOn, text = "Monetize", color = NiborjaTertiaryGold)
                    }
                }
            }
        }

        // Post List
        val filteredPosts = when (selectedFilter) {
            "Trending" -> posts.sortedByDescending { it.likesCount }
            else -> posts
        }

        if (filteredPosts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.DynamicFeed,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No posts yet. Be the first to post!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(filteredPosts, key = { it.id.ifBlank { it.content.hashCode().toString() } }) { post ->
                val postComments = commentsMap[post.id] ?: emptyList()
                PostCard(
                    post = post,
                    comments = postComments,
                    currentUserId = userProfile?.userId ?: "",
                    onLikeClick = { onLikePost(post) },
                    onAddComment = { text -> onAddComment(post.id, text) },
                    onSendGiftClick = { onSendGift(post) },
                    onDeleteClick = if (post.userId == userProfile?.userId) {
                        { onDeletePost(post.id) }
                    } else null
                )
            }
        }

        // Bottom spacer for scroll padding
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
