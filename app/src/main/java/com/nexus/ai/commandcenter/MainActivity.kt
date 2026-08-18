package com.nexus.ai.commandcenter

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

val CyberBlack = Color(0xFF05080F)
val NeonCyan = Color(0xFF00E5FF)
val ElectricBlue = Color(0xFF0055FF)
val AlertRed = Color(0xFFFF0055)
val SuccessGreen = Color(0xFF00FFAA)
val HologramGlass = Color(0x1A00E5FF)

class MainActivity : ComponentActivity() {
    private lateinit var aiAssistant: AIAssistant

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aiAssistant = AIAssistant(this)
        setContent { MaterialTheme(colorScheme = darkColorScheme(background = CyberBlack)) { NexusApp() } }
    }
    override fun onDestroy() { aiAssistant.shutdown(); super.onDestroy() }
}

enum class Screen { HOME, LAUNCHER, BOOST_SEQUENCE, VAULT }

@Composable
fun NexusApp() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedApp by remember { mutableStateOf<String?>(null) }
    var selectedPackage by remember { mutableStateOf<String?>(null) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
            when (screen) {
                Screen.HOME -> NexusHomeScreen(
                    onOpenLauncher = { currentScreen = Screen.LAUNCHER },
                    onOpenVault = { currentScreen = Screen.VAULT }
                )
                Screen.LAUNCHER -> AppLauncherScreen(
                    onBack = { currentScreen = Screen.HOME },
                    onAppSelected = { name, pkg -> selectedApp = name; selectedPackage = pkg; currentScreen = Screen.BOOST_SEQUENCE }
                )
                Screen.BOOST_SEQUENCE -> BoostSequenceScreen(
                    appName = selectedApp ?: "UNKNOWN", pkgName = selectedPackage ?: "", onComplete = { currentScreen = Screen.HOME }
                )
                Screen.VAULT -> SecurityVaultScreen(onExit = { currentScreen = Screen.HOME })
            }
        }
    }
}

@Composable
fun NexusHomeScreen(onOpenLauncher: () -> Unit, onOpenVault: () -> Unit) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("NEXUS AI // CORE", color = NeonCyan, fontFamily = FontFamily.Monospace, fontSize = 22.sp, modifier = Modifier.padding(top = 24.dp, bottom = 24.dp))
        AICorePulse()
        Spacer(modifier = Modifier.height(24.dp))
        
        // VPN Toggle
        SecureTunnelModule(context)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Modules
        Box(modifier = Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(8.dp)).background(HologramGlass).border(1.dp, ElectricBlue, RoundedCornerShape(8.dp)).clickable { onOpenLauncher() }.padding(12.dp), contentAlignment = Alignment.Center) {
            Text("INITIALIZE NEXUS BOOST // LAUNCHER", color = NeonCyan, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(8.dp)).background(HologramGlass).border(1.dp, AlertRed, RoundedCornerShape(8.dp)).clickable { onOpenVault() }.padding(12.dp), contentAlignment = Alignment.Center) {
            Text("ACCESS SECURE VAULT", color = AlertRed, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.weight(1f))
        Text("SYSTEM OVERRIDE // EXIT LAUNCHER", color = AlertRed, fontFamily = FontFamily.Monospace, modifier = Modifier.clickable {
            val intent = Intent(android.provider.Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            context.startActivity(intent)
        }.padding(bottom = 32.dp))
    }
}

@Composable
fun SecureTunnelModule(context: Context) {
    var vpnStatus by remember { mutableStateOf("STANDBY") }
    var isVpnActive by remember { mutableStateOf(false) }

    val vpnPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            context.startService(Intent(context, NexusVpnService::class.java))
            isVpnActive = true
            vpnStatus = "ENCRYPTED"
        } else {
            vpnStatus = "ACCESS DENIED"
        }
    }

    Box(modifier = Modifier.fillMaxWidth().height(75.dp).clip(RoundedCornerShape(8.dp)).background(HologramGlass).border(1.dp, if (isVpnActive) SuccessGreen else NeonCyan, RoundedCornerShape(8.dp)).clickable {
        if (isVpnActive) {
            context.stopService(Intent(context, NexusVpnService::class.java))
            isVpnActive = false; vpnStatus = "STANDBY"
        } else {
            val intent = VpnService.prepare(context)
            if (intent != null) vpnPermissionLauncher.launch(intent)
            else { context.startService(Intent(context, NexusVpnService::class.java)); isVpnActive = true; vpnStatus = "ENCRYPTED" }
        }
    }.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("NETWORK // SECURE TUNNEL", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Text("STATUS: $vpnStatus", color = if (isVpnActive) SuccessGreen else NeonCyan, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
            }
            Canvas(modifier = Modifier.size(16.dp)) { drawCircle(color = if (isVpnActive) SuccessGreen else Color.Gray, radius = size.width / 2) }
        }
    }
}

@Composable
fun AICorePulse() {
    val transition = rememberInfiniteTransition(label = "pulse")
    val rotation by transition.animateFloat(initialValue = 0f, targetValue = 360f, animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)), label = "rot")

    Canvas(modifier = Modifier.size(200.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        drawCircle(color = NeonCyan.copy(alpha = 0.2f), radius = size.width / 3, center = center)
        rotate(rotation, center) {
            drawArc(color = ElectricBlue, startAngle = 0f, sweepAngle = 280f, useCenter = false, style = Stroke(width = 8f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 20f))), topLeft = Offset(20f, 20f), size = Size(size.width - 40f, size.height - 40f))
        }
    }
}

@Composable
fun AppLauncherScreen(onBack: () -> Unit, onAppSelected: (String, String) -> Unit) {
    val context = LocalContext.current
    val pm = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
    val apps = remember { pm.queryIntentActivities(intent, 0).map { it.loadLabel(pm).toString() to it.activityInfo.packageName }.sortedBy { it.first } }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("< BACK TO CORE", color = NeonCyan, fontFamily = FontFamily.Monospace, modifier = Modifier.clickable { onBack() }.padding(vertical = 16.dp))
        LazyColumn {
            items(apps) { app ->
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).background(HologramGlass).border(1.dp, ElectricBlue, RoundedCornerShape(4.dp)).clickable { onAppSelected(app.first, app.second) }.padding(16.dp)) {
                    Text(app.first, color = Color.White, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
fun BoostSequenceScreen(appName: String, pkgName: String, onComplete: () -> Unit) {
    val context = LocalContext.current
    var phase by remember { mutableIntStateOf(0) }
    val phases = listOf("TARGET DETECTED: $appName", "CLEARING BACKGROUND RAM", "CONFIGURING AXIS CALIBRATION", "HEADSHOT SENSITIVITY OPTIMIZED", "LAUNCH SEQUENCE 100%", "⚡ BOOST READY")

    LaunchedEffect(Unit) {
        for (i in phases.indices) { phase = i; delay(800) }
        val launchIntent = context.packageManager.getLaunchIntentForPackage(pkgName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
        }
        onComplete()
    }

    Box(modifier = Modifier.fillMaxSize().background(CyberBlack), contentAlignment = Alignment.Center) {
        AICorePulse()
        Text(phases[phase], color = if (phase == phases.lastIndex) SuccessGreen else NeonCyan, fontFamily = FontFamily.Monospace, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}
