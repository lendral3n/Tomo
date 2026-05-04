package com.tomosensei.feature.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tomosensei.core.designsystem.components.Overline
import com.tomosensei.core.designsystem.components.WashiBackground
import com.tomosensei.core.designsystem.theme.HankoRed
import com.tomosensei.core.designsystem.theme.Manrope
import com.tomosensei.core.designsystem.theme.ShipporiMincho
import com.tomosensei.core.designsystem.theme.SumiBlack
import com.tomosensei.core.designsystem.theme.SumiDark
import com.tomosensei.core.designsystem.theme.SumiLight
import com.tomosensei.core.designsystem.theme.SumiMid
import com.tomosensei.core.designsystem.theme.WashiCreamLight
import com.tomosensei.feature.chat.ChatMessageUi
import com.tomosensei.feature.chat.ChatRole
import com.tomosensei.feature.chat.ChatViewModel

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    WashiBackground(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            Header()
            if (state.messages.isEmpty()) {
                EmptyState(modifier = Modifier.weight(1f), onPick = viewModel::send)
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
                ) {
                    items(state.messages, key = { it.id }) { msg ->
                        MessageBubble(msg)
                    }
                }
            }
            InputBar(
                draft = state.draft,
                streaming = state.isStreaming,
                onChange = viewModel::onDraftChange,
                onSend = { viewModel.send(state.draft) },
            )
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 52.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Sensei Chat",
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W700,
                fontSize = 16.sp,
                color = SumiBlack,
            ),
        )
        Text(
            text = "on-device · 智",
            style = TextStyle(
                fontFamily = Manrope,
                fontWeight = FontWeight.W500,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = SumiLight,
            ),
        )
    }
}

@Composable
private fun MessageBubble(msg: ChatMessageUi) {
    val isUser = msg.role == ChatRole.User
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(if (isUser) HankoRed else WashiCreamLight)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(
                text = msg.text + if (msg.streaming) " ▍" else "",
                style = TextStyle(
                    fontFamily = Manrope,
                    fontSize = 14.sp,
                    color = if (isUser) WashiCreamLight else SumiDark,
                ),
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier, onPick: (String) -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Overline(text = "Mulai percakapan")
        Text(
            text = "Tanya Sensei apa saja.",
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W600,
                fontSize = 24.sp,
                color = SumiBlack,
            ),
        )
        Text(
            text = "Beberapa kartu cepat:",
            style = TextStyle(fontFamily = Manrope, fontSize = 13.sp, color = SumiMid),
        )
        listOf(
            "Halo Sensei",
            "Apa arti 食べる?",
            "Kapan pakai は vs が?",
        ).forEach { prompt ->
            QuickPromptTile(text = prompt, onClick = { onPick(prompt) })
        }
    }
}

@Composable
private fun QuickPromptTile(text: String, onClick: () -> Unit) {
    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = Manrope,
                fontSize = 13.sp,
                color = SumiDark,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        )
    }
}

@Composable
private fun InputBar(
    draft: String,
    streaming: Boolean,
    onChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = draft,
            onValueChange = onChange,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            placeholder = {
                Text(
                    text = if (streaming) "Sensei sedang menulis…" else "Tanya Sensei…",
                    color = SumiLight,
                )
            },
            enabled = !streaming,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HankoRed,
                unfocusedBorderColor = SumiLight.copy(alpha = 0.4f),
            ),
            textStyle = TextStyle(fontFamily = Manrope, fontSize = 14.sp, color = SumiDark),
        )
        Button(
            onClick = onSend,
            shape = CircleShape,
            enabled = !streaming && draft.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = HankoRed,
                contentColor = WashiCreamLight,
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            modifier = Modifier.height(48.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Kirim",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

