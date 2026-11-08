package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Reel
import com.example.ui.theme.NiborjaPrimary
import com.example.ui.theme.NiborjaSecondary
import com.example.ui.theme.NiborjaTertiaryGold

@Composable
fun ReelsScreen(
    reels: List<Reel>,
    onLikeReel: (Reel) -> Unit,
    onSendGiftToCreator: (String, String) -> Unit,
    onOpenCreateReel: () -> Unit
) {
    if (reels.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.SlowMotionVideo,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Reels yet",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onOpenCreateReel,
                    colors = ButtonDefaults.buttonColors(containerColor = NiborjaSecondary)
                ) {
                    Text("Upload First Reel")
                }
            }
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { reels.size })

    VerticalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("reels_vertical_pager")
    ) { page ->
        val reel = reels[page]
        ReelPageItem(
            reel = reel,
            onLike = { onLikeReel(reel) },
            onGift = { onSendGiftToCreator(reel.userId, reel.authorName) }
        )
    }
}

@Composable
private fun ReelPageItem(
    reel: Reel,
    onLike: () -> Unit,
    onGift: () -> Unit
) {
    var isLiked by remember { mutableStateOf(false) }
    var likesCount by remember { mutableStateOf(reel.likesCount) }

    // Spinning disc animation for music
    val infiniteTransition = rememberInfiniteTransition(label = "music_disc")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "disc_rotation"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Video Visual / Poster
        AsyncImage(
            model = reel.videoUrl,
            contentDescription = reel.caption,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient shadows for text readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                    )
                )
        )

        // Top Tag
        Surface(
            color = Color.Black.copy(alpha = 0.4f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🔴", fontSize = 10.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "REELS STUDIO",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
            }
        }

        // Left Side Bottom: Creator info & Caption
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 80.dp, end = 80.dp)
        ) {
            // Creator info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(38.dp),
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                ) {
                    AsyncImage(
                        model = reel.authorAvatar,
                        contentDescription = reel.authorName,
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = reel.authorName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = NiborjaPrimary,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Follow",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Caption
            Text(
                text = reel.caption,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 19.sp,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Audio Tag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = NiborjaTertiaryGold,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = reel.audioTitle,
                    color = Color.White,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }

        // Right Floating Action Column
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Like
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        isLiked = !isLiked
                        if (isLiked) likesCount++ else likesCount--
                        onLike()
                    },
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                        .testTag("like_reel_button")
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color(0xFFEF4444) else Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = "$likesCount",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Comments
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { /* Open reel comments */ },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.35f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = "Comment",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Text(
                    text = "${reel.commentsCount}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Virtual Gift / Star Tip
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onGift,
                    modifier = Modifier
                        .background(NiborjaTertiaryGold.copy(alpha = 0.85f), CircleShape)
                        .testTag("gift_reel_button")
                ) {
                    Text("⭐", fontSize = 20.sp)
                }
                Text(
                    text = "Tip 70%",
                    color = NiborjaTertiaryGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Share
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { /* Share */ },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.35f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Share",
                    color = Color.White,
                    fontSize = 11.sp
                )
            }

            // Spinning Vinyl Disc
            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .rotate(rotation),
                shape = CircleShape,
                color = Color.DarkGray,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🎵", fontSize = 14.sp)
                }
            }
        }
    }
}
