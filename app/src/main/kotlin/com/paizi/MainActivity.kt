package com.paizi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paizi.ui.theme.PAIZITheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * PAIZI Main Activity
 * 
 * Provides:
 * - Chat interface
 * - Conversation display
 * - Input handling
 * - Voice support
 * - Quick actions
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PAIZITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }

        Timber.i("MainActivity Created")
    }
}

/**
 * Main Screen Composition
 */
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val inputValue = remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PAIZI") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Show menu */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            ChatInputBar(
                value = inputValue.value,
                onValueChange = { inputValue.value = it },
                onSend = {
                    if (inputValue.value.text.isNotEmpty()) {
                        viewModel.sendMessage(inputValue.value.text)
                        inputValue.value = TextFieldValue("")
                    }
                },
                onVoice = { viewModel.startVoiceInput() },
                onAttach = { viewModel.attachFile() }
            )
        }
    ) { paddingValues ->
        ConversationScreen(
            messages = state.messages,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

/**
 * Conversation Display
 */
@Composable
fun ConversationScreen(
    messages: List<ChatMessage>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        reverseLayout = true
    ) {
        items(messages.reversed()) { message ->
            ChatBubble(message = message)
        }
    }
}

/**
 * Chat Message Bubble
 */
@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor = if (isUser) 
        MaterialTheme.colorScheme.primary 
    else 
        MaterialTheme.colorScheme.tertiaryContainer

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(alignment),
        contentAlignment = alignment
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp),
                color = if (isUser) Color.White else Color.Black
            )
        }
    }
}

/**
 * Chat Input Bar
 */
@Composable
fun ChatInputBar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onSend: () -> Unit,
    onVoice: () -> Unit,
    onAttach: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onAttach) {
            Icon(Icons.Default.AttachFile, contentDescription = "Attach File")
        }

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            placeholder = { Text("Type your message...") },
            singleLine = true,
            shape = RoundedCornerShape(20.dp)
        )

        IconButton(onClick = onVoice) {
            Icon(Icons.Default.Mic, contentDescription = "Voice Input")
        }

        IconButton(onClick = onSend) {
            Icon(Icons.Default.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

/**
 * Chat Message Model
 */
data class ChatMessage(
    val id: String,
    val content: String,
    val isUser: Boolean,
    val timestamp: Long
)

/**
 * Main Screen UI State
 */
data class MainScreenState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentProject: String? = null
)

/**
 * Main Screen View Model
 */
@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MainScreenState())
    val uiState: StateFlow<MainScreenState> = _uiState.asStateFlow()

    fun sendMessage(message: String) {
        Timber.d("Message sent: $message")
        viewModelScope.launch {
            // TODO: Send message to AI system
            // This will be integrated with AIRouter and Agent system
            
            val userMessage = ChatMessage(
                id = System.currentTimeMillis().toString(),
                content = message,
                isUser = true,
                timestamp = System.currentTimeMillis()
            )

            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + userMessage,
                isLoading = true
            )

            // Simulate AI response delay
            kotlinx.coroutines.delay(1000)

            val aiMessage = ChatMessage(
                id = (System.currentTimeMillis() + 1).toString(),
                content = "I received your message: \"$message\". Full AI integration coming soon...",
                isUser = false,
                timestamp = System.currentTimeMillis()
            )

            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + aiMessage,
                isLoading = false
            )
        }
    }

    fun startVoiceInput() {
        Timber.d("Voice input started")
        // TODO: Implement voice-to-text
    }

    fun attachFile() {
        Timber.d("File attachment initiated")
        // TODO: Implement file attachment
    }
}
