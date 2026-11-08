package com.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.example.data.model.*
import com.example.data.repository.NiborjaRepository
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppRootContent(activity = this)
            }
        }
    }
}

@Composable
fun AppRootContent(activity: ComponentActivity? = null) {
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (currentUser == null) {
        AuthScreen(
            activity = activity,
            onAuthSuccess = { currentUser = Firebase.auth.currentUser }
        )
    } else {
        val uid = currentUser!!.uid
        key(uid) {
            NiborjaApp(
                currentUserId = uid,
                onSignOutRequested = {
                    currentUser = null
                }
            )
        }
    }
}

@Composable
fun NiborjaApp(
    currentUserId: String,
    onSignOutRequested: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { NiborjaRepository(context) }
    val credentialManager = remember { CredentialManager.create(context) }

    var currentTab by remember { mutableStateOf(ScreenTab.FEED) }
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var stories by remember { mutableStateOf<List<Story>>(emptyList()) }
    var reels by remember { mutableStateOf<List<Reel>>(emptyList()) }
    var marketplaceItems by remember { mutableStateOf<List<MarketplaceItem>>(emptyList()) }
    var liveStreams by remember { mutableStateOf<List<LiveStream>>(emptyList()) }
    var earnings by remember { mutableStateOf<List<CreatorEarning>>(emptyList()) }
    var payoutRequests by remember { mutableStateOf<List<PayoutRequest>>(emptyList()) }
    var conversations by remember { mutableStateOf<List<Conversation>>(emptyList()) }
    var activeConversationId by remember { mutableStateOf<String?>(null) }
    var activeMessages by remember { mutableStateOf<List<Message>>(emptyList()) }

    var showStarShop by remember { mutableStateOf(false) }
    var showGiftSheetForCreator by remember { mutableStateOf<Pair<String, String>?>(null) } // creatorId, creatorName
    var showCreateDialog by remember { mutableStateOf(false) }
    var showSellProductDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Fetch or create user profile on launch
    LaunchedEffect(currentUserId) {
        try {
            val profile = repository.getOrCreateUserProfile()
            userProfile = profile
        } catch (e: Exception) {
            Log.e("NiborjaApp", "Failed to initialize user profile", e)
        }
    }

    // Real-time observation of user profile
    LaunchedEffect(currentUserId) {
        repository.observeUserProfile(currentUserId).collect { p ->
            if (p != null) userProfile = p
        }
    }

    // Real-time observation of posts
    LaunchedEffect(Unit) {
        repository.observePosts().collect { postList ->
            posts = postList
        }
    }

    // Real-time observation of stories
    LaunchedEffect(Unit) {
        repository.observeStories().collect { storyList ->
            stories = storyList
        }
    }

    // Real-time observation of reels
    LaunchedEffect(Unit) {
        repository.observeReels().collect { reelList ->
            reels = reelList
        }
    }

    // Real-time observation of marketplace
    LaunchedEffect(Unit) {
        repository.observeMarketplace().collect { itemList ->
            marketplaceItems = itemList
        }
    }

    // Real-time observation of live streams
    LaunchedEffect(Unit) {
        repository.observeLiveStreams().collect { streamList ->
            liveStreams = streamList
        }
    }

    // Real-time observation of creator earnings & payouts
    LaunchedEffect(currentUserId) {
        repository.observeCreatorEarnings(currentUserId).collect { earnList ->
            earnings = earnList
        }
    }
    LaunchedEffect(currentUserId) {
        repository.observePayoutRequests(currentUserId).collect { payoutList ->
            payoutRequests = payoutList
        }
    }

    // Real-time observation of conversations
    LaunchedEffect(Unit) {
        repository.observeConversations().collect { convList ->
            conversations = convList
        }
    }

    // Real-time observation of messages if conversation is open
    LaunchedEffect(activeConversationId) {
        activeConversationId?.let { convId ->
            repository.observeMessages(convId).collect { msgList ->
                activeMessages = msgList
            }
        }
    }

    // Star Shop Dialog
    if (showStarShop) {
        StarShopDialog(
            currentStarsBalance = userProfile?.starsBalance ?: 0L,
            onDismiss = { showStarShop = false },
            onBuyPackage = { pkg ->
                scope.launch {
                    try {
                        repository.buyStarPackage(pkg)
                        snackbarHostState.showSnackbar("Recharged ${pkg.stars + pkg.bonusStars} Stars successfully!")
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Purchase error: ${e.localizedMessage}")
                    }
                }
            }
        )
    }

    // Virtual Gift Bottom Sheet with 70/30 split
    showGiftSheetForCreator?.let { (creatorId, creatorName) ->
        GiftBottomSheet(
            creatorName = creatorName,
            currentStarsBalance = userProfile?.starsBalance ?: 0L,
            onDismiss = { showGiftSheetForCreator = null },
            onSendGift = { gift ->
                scope.launch {
                    try {
                        val earning = repository.sendVirtualGift(creatorId, gift)
                        showGiftSheetForCreator = null
                        snackbarHostState.showSnackbar(
                            "Sent ${gift.name}! Creator gets $${String.format("%.2f", earning.creatorShareUsd)} (70%)"
                        )
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Gift failed: ${e.localizedMessage}")
                    }
                }
            },
            onOpenStarShop = {
                showGiftSheetForCreator = null
                showStarShop = true
            }
        )
    }

    // Create Content Dialog (Post, Story, Reel, Live)
    if (showCreateDialog) {
        CreateContentDialog(
            onDismiss = { showCreateDialog = false },
            onCreatePost = { content, mediaUrl, audience ->
                scope.launch {
                    try {
                        val type = if (mediaUrl.isNotBlank()) "image" else "none"
                        repository.createPost(content, mediaUrl, type, audience)
                        snackbarHostState.showSnackbar("Post published!")
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Failed to post: ${e.localizedMessage}")
                    }
                }
            },
            onCreateStory = { mediaUrl, caption ->
                scope.launch {
                    try {
                        repository.createStory(mediaUrl, caption)
                        snackbarHostState.showSnackbar("Story uploaded (24h)!")
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Story upload failed")
                    }
                }
            },
            onCreateReel = { videoUrl, caption, audio ->
                scope.launch {
                    try {
                        repository.createReel(videoUrl, caption, audio)
                        snackbarHostState.showSnackbar("Reel published to Reels Studio!")
                        currentTab = ScreenTab.REELS
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Reel upload failed")
                    }
                }
            },
            onStartLive = { title ->
                scope.launch {
                    try {
                        repository.startLiveStream(title)
                        currentTab = ScreenTab.LIVE_ROOM
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Live failed to start")
                    }
                }
            }
        )
    }

    // Sell Product Dialog
    if (showSellProductDialog) {
        SellProductDialog(
            onDismiss = { showSellProductDialog = false },
            onSubmit = { title, desc, price, category, imageUrl ->
                scope.launch {
                    try {
                        repository.createMarketplaceItem(title, desc, price, category, imageUrl)
                        showSellProductDialog = false
                        snackbarHostState.showSnackbar("Listing published on MIVORZA Market!")
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Listing failed: ${e.localizedMessage}")
                    }
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentTab != ScreenTab.REELS &&
                currentTab != ScreenTab.LIVE_ROOM &&
                currentTab != ScreenTab.MESSENGER &&
                currentTab != ScreenTab.ADMIN
            ) {
                NiborjaTopBar(
                    starsBalance = userProfile?.starsBalance ?: 0L,
                    onOpenStarShop = { showStarShop = true },
                    onOpenMessenger = { currentTab = ScreenTab.MESSENGER },
                    onOpenLive = { currentTab = ScreenTab.LIVE_ROOM },
                    onOpenSearch = { /* search */ }
                )
            }
        },
        bottomBar = {
            if (currentTab != ScreenTab.LIVE_ROOM && currentTab != ScreenTab.MESSENGER && currentTab != ScreenTab.ADMIN) {
                NiborjaBottomNav(
                    currentTab = currentTab,
                    onTabSelected = { tab -> currentTab = tab },
                    onCreateClick = { showCreateDialog = true }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                ScreenTab.FEED -> {
                    FeedScreen(
                        userProfile = userProfile,
                        posts = posts,
                        stories = stories,
                        commentsMap = emptyMap(),
                        onLikePost = { post ->
                            scope.launch {
                                repository.toggleLikePost(post.id, post.likesCount)
                            }
                        },
                        onAddComment = { postId, text ->
                            scope.launch {
                                repository.addComment(postId, text)
                                snackbarHostState.showSnackbar("Comment added")
                            }
                        },
                        onOpenCreatePost = { showCreateDialog = true },
                        onOpenAddStory = { showCreateDialog = true },
                        onSendGift = { post ->
                            showGiftSheetForCreator = Pair(post.userId, post.authorName)
                        },
                        onDeletePost = { postId ->
                            scope.launch {
                                repository.deletePost(postId)
                                snackbarHostState.showSnackbar("Post deleted")
                            }
                        }
                    )
                }

                ScreenTab.REELS -> {
                    ReelsScreen(
                        reels = reels,
                        onLikeReel = { reel ->
                            scope.launch {
                                repository.toggleLikeReel(reel.id)
                            }
                        },
                        onSendGiftToCreator = { creatorId, creatorName ->
                            showGiftSheetForCreator = Pair(creatorId, creatorName)
                        },
                        onOpenCreateReel = { showCreateDialog = true }
                    )
                }

                ScreenTab.MARKETPLACE -> {
                    MarketplaceScreen(
                        items = marketplaceItems,
                        onContactSeller = { item ->
                            scope.launch {
                                val convId = repository.startConversation(item.sellerId, item.sellerName)
                                activeConversationId = convId
                                currentTab = ScreenTab.MESSENGER
                            }
                        },
                        onOpenSellDialog = { showSellProductDialog = true }
                    )
                }

                ScreenTab.STUDIO -> {
                    CreatorStudioScreen(
                        userProfile = userProfile,
                        earnings = earnings,
                        payoutRequests = payoutRequests,
                        onRequestPayout = { amount, method, details ->
                            scope.launch {
                                try {
                                    repository.requestPayout(amount, method, details)
                                    snackbarHostState.showSnackbar("Payout request of $$amount submitted for approval!")
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Payout request failed")
                                }
                            }
                        }
                    )
                }

                ScreenTab.PROFILE -> {
                    ProfileScreen(
                        userProfile = userProfile,
                        onUpdateProfile = { updated ->
                            scope.launch {
                                repository.updateUserProfile(updated)
                                userProfile = updated
                                snackbarHostState.showSnackbar("Profile updated!")
                            }
                        },
                        onOpenAdminDashboard = { currentTab = ScreenTab.ADMIN },
                        onSignOut = {
                            scope.launch {
                                withContext(NonCancellable) {
                                    try {
                                        credentialManager.clearCredentialState(ClearCredentialStateRequest())
                                    } catch (e: Exception) {
                                        Log.w("Auth", "Failed to clear credential state: ${e.message}")
                                    } finally {
                                        Firebase.auth.signOut()
                                        onSignOutRequested()
                                    }
                                }
                            }
                        }
                    )
                }

                ScreenTab.MESSENGER -> {
                    MessengerScreen(
                        currentUserId = currentUserId,
                        conversations = conversations,
                        activeMessages = activeMessages,
                        activeConversationId = activeConversationId,
                        onSelectConversation = { convId -> activeConversationId = convId },
                        onSendMessage = { convId, text ->
                            scope.launch {
                                repository.sendMessage(convId, text)
                            }
                        },
                        onBackToConversations = { activeConversationId = null },
                        onCloseMessenger = { currentTab = ScreenTab.FEED }
                    )
                }

                ScreenTab.LIVE_ROOM -> {
                    LiveStreamScreen(
                        liveStreams = liveStreams,
                        currentUserName = userProfile?.displayName ?: "Creator",
                        onStartStream = { title ->
                            scope.launch {
                                repository.startLiveStream(title)
                            }
                        },
                        onEndStream = { streamId ->
                            scope.launch {
                                repository.endLiveStream(streamId)
                            }
                        },
                        onSendGiftToHost = { hostId, hostName ->
                            showGiftSheetForCreator = Pair(hostId, hostName)
                        },
                        onCloseLive = { currentTab = ScreenTab.FEED }
                    )
                }

                ScreenTab.ADMIN -> {
                    AdminScreen(
                        earnings = earnings,
                        payoutRequests = payoutRequests,
                        posts = posts,
                        onApprovePayout = { req ->
                            scope.launch {
                                try {
                                    repository.db.collection("payout_requests").document(req.id)
                                        .update("status", "approved")
                                    snackbarHostState.showSnackbar("Payout of $${req.amountUsd} approved!")
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Approval failed")
                                }
                            }
                        },
                        onRejectPayout = { req ->
                            scope.launch {
                                try {
                                    repository.db.collection("payout_requests").document(req.id)
                                        .update("status", "rejected")
                                    snackbarHostState.showSnackbar("Payout rejected")
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Rejection failed")
                                }
                            }
                        },
                        onDeletePost = { postId ->
                            scope.launch {
                                repository.deletePost(postId)
                                snackbarHostState.showSnackbar("Post removed by Admin")
                            }
                        },
                        onBack = { currentTab = ScreenTab.PROFILE }
                    )
                }

                else -> {}
            }
        }
    }
}
