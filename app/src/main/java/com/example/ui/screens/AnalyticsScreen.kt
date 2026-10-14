package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BlockEvent
import com.example.data.model.ScheduleCategory
import com.example.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val scheduleItems by viewModel.scheduleItems.collectAsState()
    val recentEvents by viewModel.recentBlockEvents.collectAsState()
    val todayFocusSec by viewModel.todayFocusSeconds.collectAsState()
    val todayBlockCount by viewModel.todayBlockCount.collectAsState()
    val todayShortsCount by viewModel.todayShortsCount.collectAsState()

    val actualMinutes = (todayFocusSec ?: 0) / 60
    val targetLevel = profile?.studyLevel

    val estimatedMinutesSaved = (todayShortsCount * 12) + (todayBlockCount * 8)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Productivity & Usage Analytics",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Personalized insights for ${targetLevel?.title ?: "College"} (Age ${profile?.age ?: 19})",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Top 3 Stat Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsStatCard(
                    title = "Deep Focus",
                    value = "${actualMinutes}m",
                    subtitle = "Logged today",
                    icon = Icons.Default.HourglassTop,
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                AnalyticsStatCard(
                    title = "Shorts Filtered",
                    value = "$todayShortsCount",
                    subtitle = "Feeds shielded",
                    icon = Icons.Default.VideoLibrary,
                    accentColor = Color(0xFF0284C7),
                    modifier = Modifier.weight(1f)
                )

                AnalyticsStatCard(
                    title = "Time Saved",
                    value = "~${estimatedMinutesSaved}m",
                    subtitle = "Preserved focus",
                    icon = Icons.Default.Shield,
                    accentColor = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Weekly Trend Bar Chart
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Weekly Focus Adherence (Hours)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 7-day mock bars + today's actual
                    val days = listOf("M", "T", "W", "T", "F", "S", "Today")
                    val goalHours = (profile?.dailyGoalMinutes ?: 240) / 60f
                    val hours = listOf(3.5f, 4.2f, 2.8f, 4.0f, 3.8f, 2.0f, actualMinutes / 60f)
                    val maxHour = (hours.maxOrNull() ?: 5f).coerceAtLeast(goalHours)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        days.forEachIndexed { index, day ->
                            val h = hours[index]
                            val barHeightFraction = (h / maxHour).coerceIn(0.1f, 1f)
                            val isToday = index == days.lastIndex

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = String.format(Locale.getDefault(), "%.1f", h),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .height((100 * barHeightFraction).dp)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(
                                            if (isToday) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                        )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = day,
                                    fontSize = 11.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Time Allocation Breakdown according to Age & Study Level
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Time Allocation vs Level Recommendation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Based on optimal circadian balance for ${targetLevel?.title ?: "College"}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    AllocationRow(
                        category = "Study & Academics",
                        recommendedHours = targetLevel?.targetStudyHoursPerDay ?: 5.0,
                        currentMinutes = scheduleItems.filter { it.category == ScheduleCategory.STUDY }.sumOf { it.durationMinutes() },
                        color = Color(0xFF0F766E)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    AllocationRow(
                        category = "Deep Work & Projects",
                        recommendedHours = targetLevel?.targetWorkHoursPerDay ?: 2.5,
                        currentMinutes = scheduleItems.filter { it.category == ScheduleCategory.WORK }.sumOf { it.durationMinutes() },
                        color = Color(0xFF1D4ED8)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    AllocationRow(
                        category = "Rest & Sleep",
                        recommendedHours = targetLevel?.targetRestHoursPerDay ?: 8.0,
                        currentMinutes = scheduleItems.filter { it.category == ScheduleCategory.REST }.sumOf { it.durationMinutes() },
                        color = Color(0xFFD97706)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    AllocationRow(
                        category = "Leisure & Entertainment",
                        recommendedHours = targetLevel?.targetLeisureHoursPerDay ?: 2.0,
                        currentMinutes = scheduleItems.filter { it.category == ScheduleCategory.ENTERTAINMENT }.sumOf { it.durationMinutes() },
                        color = Color(0xFF7C3AED)
                    )
                }
            }
        }

        // Age-Specific Productivity Advice
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Productivity Advice for ${targetLevel?.title ?: "You"}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = targetLevel?.advice ?: "Schedule alarms for your hardest tasks in your morning peak energy window.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // Recent Blocked Events Log
        item {
            Text(
                text = "Recent Distraction Shield Log",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (recentEvents.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No distractions intercepted yet. Start a focus session to arm the shields!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(recentEvents.take(15), key = { it.id }) { event ->
                BlockEventRow(event = event)
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun AllocationRow(
    category: String,
    recommendedHours: Double,
    currentMinutes: Int,
    color: Color
) {
    val currentHours = currentMinutes / 60.0
    val progress = (currentHours / recommendedHours.coerceAtLeast(0.1)).toFloat().coerceIn(0f, 1f)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = category, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(
                text = String.format(Locale.getDefault(), "%.1fh / %.1fh rec", currentHours, recommendedHours),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun BlockEventRow(event: BlockEvent) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(event.timestamp))

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (event.wasShorts) Color(0xFF0284C7).copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (event.wasShorts) Icons.Default.VideoLibrary else Icons.Default.Security,
                        contentDescription = null,
                        tint = if (event.wasShorts) Color(0xFF0284C7) else Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.appName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = event.reason,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = formattedTime,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AnalyticsStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
