package com.tomosensei.app.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.tomosensei.core.designsystem.components.Overline
import com.tomosensei.core.designsystem.components.WashiBackground
import com.tomosensei.core.designsystem.components.WashiCard
import com.tomosensei.core.designsystem.theme.HankoRed
import com.tomosensei.core.designsystem.theme.Manrope
import com.tomosensei.core.designsystem.theme.ShipporiMincho
import com.tomosensei.core.designsystem.theme.SuccessMoss
import com.tomosensei.core.designsystem.theme.SumiBlack
import com.tomosensei.core.designsystem.theme.SumiDark
import com.tomosensei.core.designsystem.theme.SumiLight
import com.tomosensei.core.designsystem.theme.SumiMid
import com.tomosensei.core.designsystem.theme.WashiCreamLight
import com.tomosensei.service.gateengine.GateForegroundService
import com.tomosensei.service.overlay.GateOverlayService

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var overlayGranted by remember { mutableStateOf(Settings.canDrawOverlays(context)) }
    var batteryWhitelisted by remember { mutableStateOf(isIgnoringBatteryOptimizations(context)) }
    var gateEnabled by remember { mutableStateOf(false) }

    // Refresh permission state when user returns from system settings.
    DisposableLifecycleEffect(lifecycleOwner) { event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            overlayGranted = Settings.canDrawOverlays(context)
            batteryWhitelisted = isIgnoringBatteryOptimizations(context)
        }
    }

    WashiBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 52.dp, start = 24.dp, end = 24.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = "Atur",
                style = TextStyle(
                    fontFamily = ShipporiMincho,
                    fontWeight = FontWeight.W600,
                    fontSize = 22.sp,
                    color = SumiBlack,
                ),
            )
            // Gate engine
            WashiCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Overline(text = "Gate Engine")
                    Text(
                        text = "Aktifkan agar setiap unlock HP memunculkan kartu untuk dijawab.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = Manrope,
                            color = SumiMid,
                            fontSize = 13.sp,
                        ),
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = if (gateEnabled) "Aktif" else "Nonaktif",
                            style = TextStyle(
                                fontFamily = Manrope,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W600,
                                color = if (gateEnabled) SuccessMoss else SumiLight,
                            ),
                        )
                        Switch(
                            checked = gateEnabled,
                            onCheckedChange = { wantOn ->
                                if (wantOn) {
                                    if (!overlayGranted || !batteryWhitelisted) return@Switch
                                    GateForegroundService.start(context)
                                    GateOverlayService.start(context)
                                    gateEnabled = true
                                } else {
                                    GateForegroundService.stop(context)
                                    gateEnabled = false
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = WashiCreamLight,
                                checkedTrackColor = SuccessMoss,
                                uncheckedThumbColor = WashiCreamLight,
                                uncheckedTrackColor = SumiLight.copy(alpha = 0.45f),
                            ),
                        )
                    }
                    if (!overlayGranted || !batteryWhitelisted) {
                        Text(
                            text = "Beri izin di bawah dulu sebelum aktifkan.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = Manrope,
                                color = HankoRed,
                            ),
                        )
                    }
                }
            }

            // Permission requests
            PermissionTile(
                title = "Tampilkan di atas aplikasi lain",
                subtitle = "Diperlukan agar gate muncul saat HP unlocked.",
                granted = overlayGranted,
                onRequest = { requestOverlayPermission(context) },
            )
            PermissionTile(
                title = "Bebas dari battery optimization",
                subtitle = "Tanpa ini Xiaomi akan membunuh service di background.",
                granted = batteryWhitelisted,
                onRequest = { requestBatteryWhitelist(context) },
            )

            // Footer info
            Text(
                text = "v0 — hanya level 3 (Answer to Pass) untuk trigger Unlock.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = Manrope,
                    color = SumiLight,
                ),
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun PermissionTile(
    title: String,
    subtitle: String,
    granted: Boolean,
    onRequest: () -> Unit,
) {
    WashiCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = Manrope,
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp,
                        color = SumiDark,
                    ),
                    modifier = Modifier.weight(1f),
                )
                StatusBadge(granted = granted)
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = Manrope,
                    color = SumiMid,
                ),
            )
            if (!granted) {
                Spacer(Modifier.height(2.dp))
                Button(
                    onClick = onRequest,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HankoRed,
                        contentColor = WashiCreamLight,
                    ),
                ) {
                    Text(
                        text = "Buka Settings",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = Manrope,
                            fontWeight = FontWeight.W600,
                        ),
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onRequest,
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SumiMid),
                ) {
                    Text(
                        text = "Cek lagi",
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = Manrope),
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(granted: Boolean) {
    val text = if (granted) "✓ ok" else "✗ belum"
    Text(
        text = text,
        style = TextStyle(
            fontFamily = Manrope,
            fontWeight = FontWeight.W700,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            color = if (granted) SuccessMoss else HankoRed,
        ),
    )
}

@Composable
private fun DisposableLifecycleEffect(
    owner: androidx.lifecycle.LifecycleOwner,
    onEvent: (Lifecycle.Event) -> Unit,
) {
    val callback = remember(onEvent) { LifecycleEventObserver { _, event -> onEvent(event) } }
    androidx.compose.runtime.DisposableEffect(owner) {
        owner.lifecycle.addObserver(callback)
        onDispose { owner.lifecycle.removeObserver(callback) }
    }
}

private fun requestOverlayPermission(context: Context) {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:${context.packageName}"),
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }
}

private fun requestBatteryWhitelist(context: Context) {
    @Suppress("BatteryLife")
    val intent = Intent(
        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
        Uri.parse("package:${context.packageName}"),
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }
}

private fun isIgnoringBatteryOptimizations(context: Context): Boolean {
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return pm.isIgnoringBatteryOptimizations(context.packageName)
}
