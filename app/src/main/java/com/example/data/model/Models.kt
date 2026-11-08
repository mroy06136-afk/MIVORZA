package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val username: String = "",
    val bio: String = "",
    val avatarUrl: String = "",
    val coverUrl: String = "",
    val location: String = "",
    val website: String = "",
    val creatorCategory: String = "Digital Creator",
    val isVerified: Boolean = false,
    val isCreatorMode: Boolean = true,
    val followersCount: Long = 0L,
    val followingCount: Long = 0L,
    val friendsCount: Long = 0L,
    val postsCount: Long = 0L,
    val starsBalance: Long = 200L, // Starter balance for demonstration & gifting
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Post(
    @DocumentId val id: String = "",
    val userId: String = "",
    val authorName: String = "",
    val authorAvatar: String = "",
    val authorUsername: String = "",
    val content: String = "",
    val mediaUrl: String = "",
    val mediaType: String = "none", // none, image, video
    val likesCount: Long = 0L,
    val commentsCount: Long = 0L,
    val sharesCount: Long = 0L,
    val audience: String = "public", // public, friends, private
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Comment(
    @DocumentId val id: String = "",
    val postId: String = "",
    val userId: String = "",
    val authorName: String = "",
    val authorAvatar: String = "",
    val content: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Story(
    @DocumentId val id: String = "",
    val userId: String = "",
    val authorName: String = "",
    val authorAvatar: String = "",
    val mediaUrl: String = "",
    val mediaType: String = "image",
    val caption: String = "",
    val viewsCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    val expiresAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Reel(
    @DocumentId val id: String = "",
    val userId: String = "",
    val authorName: String = "",
    val authorAvatar: String = "",
    val videoUrl: String = "",
    val caption: String = "",
    val audioTitle: String = "Original Sound - MIVORZA Studio",
    val likesCount: Long = 0L,
    val commentsCount: Long = 0L,
    val viewsCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Conversation(
    @DocumentId val id: String = "",
    val participantIds: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageSenderId: String = "",
    @ServerTimestamp val updatedAt: Timestamp? = null,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Message(
    @DocumentId val id: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatar: String = "",
    val text: String = "",
    val mediaUrl: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class NotificationItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val fromUserId: String = "",
    val fromUserName: String = "",
    val fromUserAvatar: String = "",
    val type: String = "like", // like, comment, friend, gift, payout
    val message: String = "",
    val targetId: String = "",
    val isRead: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CreatorEarning(
    @DocumentId val id: String = "",
    val creatorId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val giftName: String = "",
    val starAmount: Long = 0L,
    val creatorShareUsd: Double = 0.0, // 70% share
    val adminShareUsd: Double = 0.0,   // 30% share
    val status: String = "completed",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PayoutRequest(
    @DocumentId val id: String = "",
    val creatorId: String = "",
    val creatorName: String = "",
    val amountUsd: Double = 0.0,
    val method: String = "Bank Transfer",
    val accountDetails: String = "",
    val status: String = "pending", // pending, approved, rejected, processed
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class MarketplaceItem(
    @DocumentId val id: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val sellerAvatar: String = "",
    val title: String = "",
    val description: String = "",
    val priceUsd: Double = 0.0,
    val category: String = "Electronics",
    val imageUrl: String = "",
    val status: String = "active", // active, sold
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class LiveStream(
    @DocumentId val id: String = "",
    val hostId: String = "",
    val hostName: String = "",
    val hostAvatar: String = "",
    val title: String = "",
    val viewerCount: Long = 1L,
    val status: String = "live", // live, ended
    @ServerTimestamp val createdAt: Timestamp? = null
)

data class StarPackage(
    val stars: Long,
    val priceUsd: Double,
    val bonusStars: Long = 0L,
    val isPopular: Boolean = false
)

data class VirtualGift(
    val id: String,
    val name: String,
    val stars: Long,
    val emoji: String,
    val tier: String // small, medium, high, premium
)
