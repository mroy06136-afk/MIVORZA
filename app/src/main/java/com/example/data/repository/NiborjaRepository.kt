package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.*
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class NiborjaRepository(val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google to perform this action.")
    }

    // Default sample content when collections are fresh
    companion object {
        val DEFAULT_POSTS = listOf(
            Post(
                id = "default_post_1",
                userId = "creator_elena",
                authorName = "Elena Rostova",
                authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
                authorUsername = "elena_creative",
                content = "Excited to launch my new 3D motion design course exclusively on MIVORZA Studio! 🚀 Thank you to all my supporters for the virtual gifts and love. Let's build the future together! ✨ #CreateAndEarn #MivorzaCreators",
                mediaUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800",
                mediaType = "image",
                likesCount = 1420L,
                commentsCount = 88L,
                sharesCount = 45L,
                audience = "public"
            ),
            Post(
                id = "default_post_2",
                userId = "creator_marcus",
                authorName = "Marcus Vance",
                authorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200",
                authorUsername = "marcus_tech",
                content = "The 70/30 creator split on MIVORZA is genuinely game-changing for independent artists. Received my first instant payout today directly to my bank account. Transparency wins! 💰📊",
                mediaUrl = "",
                mediaType = "none",
                likesCount = 890L,
                commentsCount = 54L,
                sharesCount = 29L,
                audience = "public"
            ),
            Post(
                id = "default_post_3",
                userId = "creator_sarah",
                authorName = "Sarah Chen",
                authorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200",
                authorUsername = "sarah_travels",
                content = "Sunset over Tokyo skyline 🌆 Streaming live today at 8 PM JST with interactive Q&A and surprise gift drops! Tap the live tab to join the broadcast.",
                mediaUrl = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=800",
                mediaType = "image",
                likesCount = 2310L,
                commentsCount = 142L,
                sharesCount = 110L,
                audience = "public"
            )
        )

        val DEFAULT_STORIES = listOf(
            Story(
                id = "default_story_1",
                userId = "creator_elena",
                authorName = "Elena Rostova",
                authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
                mediaUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800",
                caption = "Behind the scenes in the design studio 🎨"
            ),
            Story(
                id = "default_story_2",
                userId = "creator_sarah",
                authorName = "Sarah Chen",
                authorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200",
                mediaUrl = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=800",
                caption = "Tokyo evening street lights 🌃"
            ),
            Story(
                id = "default_story_3",
                userId = "creator_marcus",
                authorName = "Marcus Vance",
                authorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200",
                mediaUrl = "https://images.unsplash.com/photo-1590602847861-f357a9332bbc?w=800",
                caption = "Testing new broadcast audio setup 🎙️"
            )
        )

        val DEFAULT_REELS = listOf(
            Reel(
                id = "default_reel_1",
                userId = "creator_elena",
                authorName = "Elena Rostova",
                authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
                videoUrl = "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=800",
                caption = "Digital art speedpaint session 🎨 Dragon gift animation preview! What should I draw next?",
                audioTitle = "Mivorza Beats - Chill LoFi Vol. 4",
                likesCount = 5430L,
                commentsCount = 312L,
                viewsCount = 14200L
            ),
            Reel(
                id = "default_reel_2",
                userId = "creator_sarah",
                authorName = "Sarah Chen",
                authorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200",
                videoUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800",
                caption = "Cyberpunk night market vibes in Shinjuku ✨ Neon reflections and rain. #TravelReel",
                audioTitle = "Tokyo Synthwave - Night Runner",
                likesCount = 8920L,
                commentsCount = 620L,
                viewsCount = 32400L
            )
        )

        val DEFAULT_MARKET_ITEMS = listOf(
            MarketplaceItem(
                id = "default_item_1",
                sellerId = "creator_marcus",
                sellerName = "Marcus Vance",
                sellerAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200",
                title = "Pro Studio Condenser Microphone Kit",
                description = "Pristine condition professional cardioid mic, shock mount, pop filter and XLR cable. Perfect for podcasting, streaming and voiceover.",
                priceUsd = 149.0,
                category = "Creator Audio",
                imageUrl = "https://images.unsplash.com/photo-1590602847861-f357a9332bbc?w=600",
                status = "active"
            ),
            MarketplaceItem(
                id = "default_item_2",
                sellerId = "creator_elena",
                sellerName = "Elena Rostova",
                sellerAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
                title = "Limited Edition Cyberpunk Neon Art Print (Signed)",
                description = "High-quality metallic foil print 18x24 inches, hand numbered and signed by artist Elena Rostova.",
                priceUsd = 75.0,
                category = "Art & Collectibles",
                imageUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=600",
                status = "active"
            )
        )

        val DEFAULT_LIVE = listOf(
            LiveStream(
                id = "default_live_1",
                hostId = "creator_sarah",
                hostName = "Sarah Chen",
                hostAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200",
                title = "Live Tokyo Shibuya Walk & Q&A 🔴 Special Star Gift Rewards!",
                viewerCount = 1438L,
                status = "live"
            )
        )
    }

    // --- USER PROFILE ---
    fun observeUserProfile(userId: String): Flow<UserProfile?> = callbackFlow {
        val listener = db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("NiborjaRepo", "Error observing user profile", error)
                    trySend(null)
                    return@addSnapshotListener
                }
                val profile = snapshot?.toObject(UserProfile::class.java)
                trySend(profile)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getOrCreateUserProfile(): UserProfile {
        val user = auth.currentUser ?: throw IllegalStateException("Not signed in")
        val docRef = db.collection("users").document(user.uid)
        try {
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                val existing = snapshot.toObject(UserProfile::class.java)
                if (existing != null) return existing
            }
        } catch (e: Exception) {
            Log.w("NiborjaRepo", "Profile read fallback: ${e.message}")
        }

        val name = user.displayName?.ifBlank { "Mivorza Creator" } ?: "Mivorza Creator"
        val rawHandle = (user.email?.substringBefore("@") ?: "creator_${user.uid.take(5)}")
            .replace("[^a-zA-Z0-9_]".toRegex(), "")
        val handle = if (rawHandle.length >= 2) rawHandle else "creator_${user.uid.take(5)}"
        val avatar = user.photoUrl?.toString() ?: ""

        val newProfile = UserProfile(
            userId = user.uid,
            displayName = name,
            username = handle,
            avatarUrl = avatar,
            coverUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800",
            bio = "Official creator on MIVORZA • Connect, Share, Create & Earn",
            creatorCategory = "Content Creator & Artist",
            isVerified = true,
            isCreatorMode = true,
            starsBalance = 250L
        )

        val payload = mapOf(
            "userId" to newProfile.userId,
            "displayName" to newProfile.displayName,
            "username" to newProfile.username,
            "avatarUrl" to newProfile.avatarUrl,
            "coverUrl" to newProfile.coverUrl,
            "bio" to newProfile.bio,
            "creatorCategory" to newProfile.creatorCategory,
            "isVerified" to newProfile.isVerified,
            "isCreatorMode" to newProfile.isCreatorMode,
            "followersCount" to 1240L,
            "followingCount" to 180L,
            "friendsCount" to 95L,
            "postsCount" to 12L,
            "starsBalance" to newProfile.starsBalance,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            docRef.set(payload).await()
        } catch (e: Exception) {
            Log.e("NiborjaRepo", "Failed to create user profile in Firestore: ${e.message}")
        }
        return newProfile
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val payload = mapOf(
            "displayName" to profile.displayName,
            "username" to profile.username,
            "bio" to profile.bio,
            "location" to profile.location,
            "website" to profile.website,
            "creatorCategory" to profile.creatorCategory,
            "isCreatorMode" to profile.isCreatorMode,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.update(payload).await()
    }

    // --- POSTS ---
    fun observePosts(): Flow<List<Post>> = db.collection("posts")
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .snapshots()
        .map { snapshot ->
            val remote = snapshot.toObjects(Post::class.java)
            if (remote.isEmpty()) DEFAULT_POSTS else remote
        }
        .catch { e ->
            Log.e("NiborjaRepo", "Error observing posts", e)
            emit(DEFAULT_POSTS)
        }

    suspend fun createPost(content: String, mediaUrl: String = "", mediaType: String = "none", audience: String = "public") {
        val uid = requireUserId()
        val user = auth.currentUser
        val authorName = user?.displayName ?: "Creator"
        val authorAvatar = user?.photoUrl?.toString() ?: ""
        val authorUsername = user?.email?.substringBefore("@") ?: "creator"

        val docRef = db.collection("posts").document()
        val payload = mapOf(
            "userId" to uid,
            "authorName" to authorName,
            "authorAvatar" to authorAvatar,
            "authorUsername" to authorUsername,
            "content" to content,
            "mediaUrl" to mediaUrl,
            "mediaType" to mediaType,
            "likesCount" to 0L,
            "commentsCount" to 0L,
            "sharesCount" to 0L,
            "audience" to audience,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()

        try {
            db.collection("users").document(uid).update("postsCount", FieldValue.increment(1)).await()
        } catch (e: Exception) {
            Log.w("NiborjaRepo", "Increment post count warning: ${e.message}")
        }
    }

    suspend fun toggleLikePost(postId: String, currentLikes: Long) {
        requireUserId()
        try {
            val postRef = db.collection("posts").document(postId)
            postRef.update("likesCount", FieldValue.increment(1)).await()
        } catch (e: Exception) {
            Log.w("NiborjaRepo", "Like post update: ${e.message}")
        }
    }

    suspend fun deletePost(postId: String) {
        val uid = requireUserId()
        val postRef = db.collection("posts").document(postId)
        val snap = postRef.get().await()
        if (snap.getString("userId") == uid) {
            postRef.delete().await()
            try {
                db.collection("users").document(uid).update("postsCount", FieldValue.increment(-1)).await()
            } catch (e: Exception) {
                Log.w("NiborjaRepo", "Decrement count: ${e.message}")
            }
        }
    }

    // --- COMMENTS ---
    fun observeComments(postId: String): Flow<List<Comment>> = db.collection("posts").document(postId)
        .collection("comments")
        .orderBy("createdAt", Query.Direction.ASCENDING)
        .snapshots()
        .map { it.toObjects(Comment::class.java) }
        .catch { e ->
            Log.e("NiborjaRepo", "Error observing comments", e)
            emit(emptyList())
        }

    suspend fun addComment(postId: String, text: String) {
        val uid = requireUserId()
        val user = auth.currentUser
        val name = user?.displayName ?: "Creator"
        val avatar = user?.photoUrl?.toString() ?: ""

        val commentRef = db.collection("posts").document(postId).collection("comments").document()
        val payload = mapOf(
            "postId" to postId,
            "userId" to uid,
            "authorName" to name,
            "authorAvatar" to avatar,
            "content" to text,
            "createdAt" to FieldValue.serverTimestamp()
        )
        commentRef.set(payload).await()
        try {
            db.collection("posts").document(postId).update("commentsCount", FieldValue.increment(1)).await()
        } catch (e: Exception) {
            Log.w("NiborjaRepo", "Increment comment: ${e.message}")
        }
    }

    // --- STORIES ---
    fun observeStories(): Flow<List<Story>> = db.collection("stories")
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .snapshots()
        .map { snapshot ->
            val remote = snapshot.toObjects(Story::class.java)
            if (remote.isEmpty()) DEFAULT_STORIES else remote
        }
        .catch { e ->
            Log.e("NiborjaRepo", "Error observing stories", e)
            emit(DEFAULT_STORIES)
        }

    suspend fun createStory(mediaUrl: String, caption: String = "") {
        val uid = requireUserId()
        val user = auth.currentUser
        val name = user?.displayName ?: "Creator"
        val avatar = user?.photoUrl?.toString() ?: ""

        val storyRef = db.collection("stories").document()
        val payload = mapOf(
            "userId" to uid,
            "authorName" to name,
            "authorAvatar" to avatar,
            "mediaUrl" to mediaUrl,
            "mediaType" to "image",
            "caption" to caption,
            "viewsCount" to 0L,
            "createdAt" to FieldValue.serverTimestamp()
        )
        storyRef.set(payload).await()
    }

    // --- REELS ---
    fun observeReels(): Flow<List<Reel>> = db.collection("reels")
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .snapshots()
        .map { snapshot ->
            val remote = snapshot.toObjects(Reel::class.java)
            if (remote.isEmpty()) DEFAULT_REELS else remote
        }
        .catch { e ->
            Log.e("NiborjaRepo", "Error observing reels", e)
            emit(DEFAULT_REELS)
        }

    suspend fun createReel(videoUrl: String, caption: String, audioTitle: String = "Original Sound - MIVORZA Studio") {
        val uid = requireUserId()
        val user = auth.currentUser
        val name = user?.displayName ?: "Creator"
        val avatar = user?.photoUrl?.toString() ?: ""

        val reelRef = db.collection("reels").document()
        val payload = mapOf(
            "userId" to uid,
            "authorName" to name,
            "authorAvatar" to avatar,
            "videoUrl" to videoUrl,
            "caption" to caption,
            "audioTitle" to audioTitle,
            "likesCount" to 0L,
            "commentsCount" to 0L,
            "viewsCount" to 1L,
            "createdAt" to FieldValue.serverTimestamp()
        )
        reelRef.set(payload).await()
    }

    suspend fun toggleLikeReel(reelId: String) {
        try {
            val reelRef = db.collection("reels").document(reelId)
            reelRef.update("likesCount", FieldValue.increment(1)).await()
        } catch (e: Exception) {
            Log.w("NiborjaRepo", "Like reel: ${e.message}")
        }
    }

    // --- CONVERSATIONS & MESSAGING ---
    fun observeConversations(): Flow<List<Conversation>> = db.collection("conversations")
        .orderBy("updatedAt", Query.Direction.DESCENDING)
        .snapshots()
        .map { it.toObjects(Conversation::class.java) }
        .catch { e ->
            Log.e("NiborjaRepo", "Error observing conversations", e)
            emit(emptyList())
        }

    fun observeMessages(conversationId: String): Flow<List<Message>> = db.collection("conversations")
        .document(conversationId)
        .collection("messages")
        .orderBy("createdAt", Query.Direction.ASCENDING)
        .snapshots()
        .map { it.toObjects(Message::class.java) }
        .catch { e ->
            Log.e("NiborjaRepo", "Error observing messages", e)
            emit(emptyList())
        }

    suspend fun sendMessage(conversationId: String, text: String) {
        val uid = requireUserId()
        val user = auth.currentUser
        val name = user?.displayName ?: "User"
        val avatar = user?.photoUrl?.toString() ?: ""

        val msgRef = db.collection("conversations").document(conversationId).collection("messages").document()
        val payload = mapOf(
            "conversationId" to conversationId,
            "senderId" to uid,
            "senderName" to name,
            "senderAvatar" to avatar,
            "text" to text,
            "mediaUrl" to "",
            "createdAt" to FieldValue.serverTimestamp()
        )
        msgRef.set(payload).await()

        db.collection("conversations").document(conversationId).update(
            mapOf(
                "lastMessage" to text,
                "lastMessageSenderId" to uid,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    suspend fun startConversation(targetUserId: String, targetName: String): String {
        val uid = requireUserId()
        val convRef = db.collection("conversations").document()
        val payload = mapOf(
            "participantIds" to listOf(uid, targetUserId),
            "lastMessage" to "Started a conversation with $targetName",
            "lastMessageSenderId" to uid,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        convRef.set(payload).await()
        return convRef.id
    }

    // --- CREATOR MONETIZATION & 70/30 SPLIT ---
    fun observeCreatorEarnings(creatorId: String): Flow<List<CreatorEarning>> = db.collection("creator_earnings")
        .whereEqualTo("creatorId", creatorId)
        .snapshots()
        .map { it.toObjects(CreatorEarning::class.java) }
        .catch { e ->
            Log.e("NiborjaRepo", "Error observing creator earnings", e)
            emit(emptyList())
        }

    suspend fun sendVirtualGift(
        creatorId: String,
        gift: VirtualGift
    ): CreatorEarning {
        val uid = requireUserId()
        val user = auth.currentUser
        val senderName = user?.displayName ?: "Supporter"

        // Baseline: 1 Star = $0.01 USD
        val totalUsd = gift.stars * 0.01
        // 70% Creator / 30% Admin Revenue Split
        val creatorShare = (totalUsd * 0.70 * 100.0).toLong() / 100.0
        val adminShare = (totalUsd * 0.30 * 100.0).toLong() / 100.0

        // Deduct stars from sender
        val senderDoc = db.collection("users").document(uid)
        senderDoc.update("starsBalance", FieldValue.increment(-gift.stars)).await()

        // Create transaction record in creator_earnings
        val earningRef = db.collection("creator_earnings").document()
        val earning = CreatorEarning(
            id = earningRef.id,
            creatorId = creatorId,
            senderId = uid,
            senderName = senderName,
            giftName = gift.name,
            starAmount = gift.stars,
            creatorShareUsd = creatorShare,
            adminShareUsd = adminShare,
            status = "completed"
        )

        val payload = mapOf(
            "creatorId" to earning.creatorId,
            "senderId" to earning.senderId,
            "senderName" to earning.senderName,
            "giftName" to earning.giftName,
            "starAmount" to earning.starAmount,
            "creatorShareUsd" to earning.creatorShareUsd,
            "adminShareUsd" to earning.adminShareUsd,
            "status" to earning.status,
            "createdAt" to FieldValue.serverTimestamp()
        )
        earningRef.set(payload).await()

        // Send notification to creator
        createNotification(
            targetUserId = creatorId,
            type = "gift",
            message = "$senderName sent you a ${gift.name} (${gift.stars} Stars • +$${creatorShare} USD)"
        )

        return earning
    }

    suspend fun buyStarPackage(pkg: StarPackage) {
        val uid = requireUserId()
        val totalAddedStars = pkg.stars + pkg.bonusStars
        db.collection("users").document(uid).update("starsBalance", FieldValue.increment(totalAddedStars)).await()
    }

    // --- PAYOUT REQUESTS ---
    fun observePayoutRequests(creatorId: String): Flow<List<PayoutRequest>> = db.collection("payout_requests")
        .whereEqualTo("creatorId", creatorId)
        .snapshots()
        .map { it.toObjects(PayoutRequest::class.java) }
        .catch { e ->
            Log.e("NiborjaRepo", "Error observing payout requests", e)
            emit(emptyList())
        }

    suspend fun requestPayout(amountUsd: Double, method: String, accountDetails: String) {
        val uid = requireUserId()
        val user = auth.currentUser
        val name = user?.displayName ?: "Creator"

        val payoutRef = db.collection("payout_requests").document()
        val payload = mapOf(
            "creatorId" to uid,
            "creatorName" to name,
            "amountUsd" to amountUsd,
            "method" to method,
            "accountDetails" to accountDetails,
            "status" to "pending",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        payoutRef.set(payload).await()

        createNotification(
            targetUserId = uid,
            type = "payout",
            message = "Your payout request of $$amountUsd via $method has been submitted and is pending review."
        )
    }

    // --- MARKETPLACE ---
    fun observeMarketplace(): Flow<List<MarketplaceItem>> = db.collection("marketplace")
        .whereEqualTo("status", "active")
        .snapshots()
        .map { snapshot ->
            val remote = snapshot.toObjects(MarketplaceItem::class.java)
            if (remote.isEmpty()) DEFAULT_MARKET_ITEMS else remote
        }
        .catch { e ->
            Log.e("NiborjaRepo", "Error observing marketplace", e)
            emit(DEFAULT_MARKET_ITEMS)
        }

    suspend fun createMarketplaceItem(title: String, description: String, priceUsd: Double, category: String, imageUrl: String) {
        val uid = requireUserId()
        val user = auth.currentUser
        val name = user?.displayName ?: "Seller"
        val avatar = user?.photoUrl?.toString() ?: ""

        val itemRef = db.collection("marketplace").document()
        val payload = mapOf(
            "sellerId" to uid,
            "sellerName" to name,
            "sellerAvatar" to avatar,
            "title" to title,
            "description" to description,
            "priceUsd" to priceUsd,
            "category" to category,
            "imageUrl" to imageUrl,
            "status" to "active",
            "createdAt" to FieldValue.serverTimestamp()
        )
        itemRef.set(payload).await()
    }

    // --- LIVE STREAMS ---
    fun observeLiveStreams(): Flow<List<LiveStream>> = db.collection("live_streams")
        .whereEqualTo("status", "live")
        .snapshots()
        .map { snapshot ->
            val remote = snapshot.toObjects(LiveStream::class.java)
            if (remote.isEmpty()) DEFAULT_LIVE else remote
        }
        .catch { e ->
            Log.e("NiborjaRepo", "Error observing live streams", e)
            emit(DEFAULT_LIVE)
        }

    suspend fun startLiveStream(title: String): String {
        val uid = requireUserId()
        val user = auth.currentUser
        val name = user?.displayName ?: "Broadcaster"
        val avatar = user?.photoUrl?.toString() ?: ""

        val streamRef = db.collection("live_streams").document()
        val payload = mapOf(
            "hostId" to uid,
            "hostName" to name,
            "hostAvatar" to avatar,
            "title" to title,
            "viewerCount" to 1L,
            "status" to "live",
            "createdAt" to FieldValue.serverTimestamp()
        )
        streamRef.set(payload).await()
        return streamRef.id
    }

    suspend fun endLiveStream(streamId: String) {
        val uid = requireUserId()
        val doc = db.collection("live_streams").document(streamId)
        val snap = doc.get().await()
        if (snap.getString("hostId") == uid) {
            doc.update("status", "ended").await()
        }
    }

    // --- NOTIFICATIONS ---
    fun observeNotifications(userId: String): Flow<List<NotificationItem>> = db.collection("users")
        .document(userId)
        .collection("notifications")
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .snapshots()
        .map { it.toObjects(NotificationItem::class.java) }
        .catch { e ->
            Log.e("NiborjaRepo", "Error observing notifications", e)
            emit(emptyList())
        }

    suspend fun createNotification(targetUserId: String, type: String, message: String) {
        val sender = auth.currentUser
        val notifRef = db.collection("users").document(targetUserId).collection("notifications").document()
        val payload = mapOf(
            "userId" to targetUserId,
            "fromUserId" to (sender?.uid ?: "system"),
            "fromUserName" to (sender?.displayName ?: "Niborja"),
            "fromUserAvatar" to (sender?.photoUrl?.toString() ?: ""),
            "type" to type,
            "message" to message,
            "targetId" to "",
            "isRead" to false,
            "createdAt" to FieldValue.serverTimestamp()
        )
        notifRef.set(payload).await()
    }
}
