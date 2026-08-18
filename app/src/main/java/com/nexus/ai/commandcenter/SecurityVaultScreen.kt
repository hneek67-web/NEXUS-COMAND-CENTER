package com.nexus.ai.commandcenter

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SecurityVaultScreen(onExit: () -> Unit) {
    var enteredPin by remember { mutableStateOf("") }
    var vaultState by remember { mutableStateOf("LOCKED") }
    
    val masterPin = "1234"
    val duressPin = "9999"
    val hiddenApps = listOf("Secure Browser", "Crypto Wallet", "Private Gallery")
    val decoyApps = listOf("Calculator", "System Notes")

    Column(modifier = Modifier.fillMaxSize().background(CyberBlack).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("NEXUS // SECURE VAULT", color = AlertRed, fontFamily = FontFamily.Monospace, fontSize = 22.sp, modifier = Modifier.padding(top = 24.dp))
        Spacer(modifier = Modifier.height(32.dp))

        if (vaultState == "LOCKED") {
            Text("ENTER CLEARANCE CODE", color = NeonCyan, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = enteredPin.padEnd(4, '_').replace(Regex("[0-9]"), "*"), color = Color.White, fontSize = 32.sp, letterSpacing = 12.sp, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(48.dp))
            
            val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "DEL", "0", "ENT")
            LazyColumn {
                items(keys.chunked(3)) { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        row.forEach { key ->
                            Box(modifier = Modifier.size(75.dp).padding(6.dp).clip(RoundedCornerShape(8.dp)).background(HologramGlass).border(1.dp, ElectricBlue, RoundedCornerShape(8.dp)).clickable {
                                when (key) {
                                    "DEL" -> if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                                    "ENT" -> {
                                        if (enteredPin == masterPin) vaultState = "MASTER"
                                        else if (enteredPin == duressPin) vaultState = "DURESS"
                                        else enteredPin = ""
                                    }
                                    else -> if (enteredPin.length < 4) enteredPin += key
                                }
                            }, contentAlignment = Alignment.Center) { Text(key, color = NeonCyan, fontFamily = FontFamily.Monospace, fontSize = 20.sp) }
                        }
                    }
                }
            }
        } else {
            Text(text = if (vaultState == "MASTER") "MASTER CLEARANCE ACCEPTED" else "GUEST CLEARANCE ACCEPTED", color = SuccessGreen, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(24.dp))
            val displayApps = if (vaultState == "MASTER") hiddenApps else decoyApps
            displayApps.forEach { app ->
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).background(HologramGlass).border(1.dp, SuccessGreen, RoundedCornerShape(4.dp)).padding(16.dp)) {
                    Text(app, color = Color.White, fontFamily = FontFamily.Monospace)
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Text("< LOCK & EXIT", color = AlertRed, fontFamily = FontFamily.Monospace, modifier = Modifier.clickable { onExit() }.padding(bottom = 32.dp))
    }
}
