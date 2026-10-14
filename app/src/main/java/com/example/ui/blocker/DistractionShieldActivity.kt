package com.example.ui.blocker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainActivity
import com.example.service.FocusSessionManager
import com.example.ui.theme.MyApplicationTheme

class DistractionShieldActivity : ComponentActivity() {

    companion object {
        const val EXTRA_APP_NAME = "EXTRA_APP_NAME"
        const val EXTRA_REASON = "EXTRA_REASON"
        const val EXTRA_WAS_SHORTS = "EXTRA_WAS_SHORTS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appName = intent.getStringExtra(EXTRA_APP_NAME) ?: "Distracting App"
        val reason = intent.getStringExtra(EXTRA_REASON) ?: "This content is blocked during your focus session."
        val wasShorts = intent.getBooleanExtra(EXTRA_WAS_SHORTS, false)

        setContent {
            MyApplicationTheme(darkTheme = true) {
                DistractionShieldScreen(
                    appName = appName,
                    reason = reason,
                    wasShorts = wasShorts,
                    onReturnToFocus = {
                        val mainIntent = Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(mainIntent)
                        finish()
                    },
                    onEmergencyBreak = {
                        FocusSessionManager.grantEmergencyBreak(2)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun DistractionShieldScreen(
    appName: String,
    reason: String,
    wasShorts: Boolean,
    onReturnToFocus: () -> Unit,
    onEmergencyBreak: () -> Unit
) {
    val remainingSec by FocusSessionManager.remainingSeconds.collectAsState()
    val sessionTitle by FocusSessionManager.sessionTitle.collectAsState()
    val sessionCategory by FocusSessionManager.sessionCategory.collectAsState()

    val minutes = remainingSec / 60
    val seconds = remainingSec % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1E38),
                        Color(0xFF0F172A)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Shield Icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFFEF4444).copy(alpha = 0.15f), CircleShape)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color(0xFFEF4444).copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (wasShorts) Icons.Default.School else Icons.Default.Shield,
                        contentDescription = "Shield Active",
                        tint = if (wasShorts) Color(0xFF38BDF8) else Color(0xFFF87171),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (wasShorts) Color(0xFF0369A1).copy(alpha = 0.3f) else Color(0xFFDC2626).copy(alpha = 0.25f),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = if (wasShorts) "SMART SHORTS FILTER" else "DISTRACTION INTERCEPTED",
                    color = if (wasShorts) Color(0xFF38BDF8) else Color(0xFFFCA5A5),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Text(
                text = "$appName Shielded",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = reason,
                fontSize = 14.sp,
                color = Color(0xFFCBD5E1),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Current Session Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassBottom,
                            contentDescription = null,
                            tint = sessionCategory.darkColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ACTIVE SESSION",
                            color = sessionCategory.darkColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = sessionTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = formattedTime,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = sessionCategory.darkColor,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "remaining until distractions unlock",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Buttons
            Button(
                onClick = onReturnToFocus,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = sessionCategory.darkColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("return_to_focus_button")
            ) {
                Text(
                    text = "Return to Focus",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onEmergencyBreak,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("emergency_break_button")
            ) {
                Text(
                    text = "Emergency 2-Min Break (+2m)",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp
                )
            }
        }
    }
}
