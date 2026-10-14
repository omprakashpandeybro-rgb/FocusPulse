package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class ScheduleCategory(
    val displayName: String,
    val lightColor: Color,
    val darkColor: Color,
    val description: String
) {
    WORK(
        displayName = "Deep Work",
        lightColor = Color(0xFF1D4ED8),
        darkColor = Color(0xFF60A5FA),
        description = "Core high-value tasks, projects, coding & writing"
    ),
    STUDY(
        displayName = "Study & Learn",
        lightColor = Color(0xFF0F766E),
        darkColor = Color(0xFF2DD4BF),
        description = "Lectures, reading, revision & conceptual prep"
    ),
    REST(
        displayName = "Rest & Recharge",
        lightColor = Color(0xFFD97706),
        darkColor = Color(0xFFFBBF24),
        description = "Mindful breaks, walks, hydration, meals & sleep"
    ),
    ENTERTAINMENT(
        displayName = "Leisure & Play",
        lightColor = Color(0xFF7C3AED),
        darkColor = Color(0xFFA78BFA),
        description = "Gaming, media, connecting with friends"
    ),
    ROUTINE(
        displayName = "Daily Wellness",
        lightColor = Color(0xFF0369A1),
        darkColor = Color(0xFF38BDF8),
        description = "Morning prep, workout, commuting & chores"
    );

    fun getColor(isDark: Boolean): Color = if (isDark) darkColor else lightColor
}

enum class StudyLevel(
    val title: String,
    val ageRange: String,
    val recommendedFocusBlockMinutes: Int,
    val recommendedBreakMinutes: Int,
    val targetStudyHoursPerDay: Double,
    val targetWorkHoursPerDay: Double,
    val targetRestHoursPerDay: Double,
    val targetLeisureHoursPerDay: Double,
    val advice: String
) {
    MIDDLE_SCHOOL(
        title = "Middle School",
        ageRange = "10–14 yrs",
        recommendedFocusBlockMinutes = 25,
        recommendedBreakMinutes = 5,
        targetStudyHoursPerDay = 2.5,
        targetWorkHoursPerDay = 1.0,
        targetRestHoursPerDay = 9.5,
        targetLeisureHoursPerDay = 2.0,
        advice = "Short 25m bursts build lifelong discipline. Ample sleep & active play are vital."
    ),
    HIGH_SCHOOL(
        title = "High School",
        ageRange = "14–18 yrs",
        recommendedFocusBlockMinutes = 35,
        recommendedBreakMinutes = 7,
        targetStudyHoursPerDay = 4.0,
        targetWorkHoursPerDay = 1.5,
        targetRestHoursPerDay = 8.5,
        targetLeisureHoursPerDay = 2.0,
        advice = "Prepare for exams by eliminating social media rabbit holes and YouTube Shorts."
    ),
    COLLEGE_UNDERGRAD(
        title = "College / University",
        ageRange = "18–24 yrs",
        recommendedFocusBlockMinutes = 50,
        recommendedBreakMinutes = 10,
        targetStudyHoursPerDay = 5.5,
        targetWorkHoursPerDay = 2.5,
        targetRestHoursPerDay = 8.0,
        targetLeisureHoursPerDay = 2.0,
        advice = "Master deep asynchronous study sprints with scheduled social & fitness recharge."
    ),
    GRADUATE_EXAMS(
        title = "Graduate / Competitive Exams",
        ageRange = "21+ yrs",
        recommendedFocusBlockMinutes = 60,
        recommendedBreakMinutes = 10,
        targetStudyHoursPerDay = 7.0,
        targetWorkHoursPerDay = 2.0,
        targetRestHoursPerDay = 7.5,
        targetLeisureHoursPerDay = 1.5,
        advice = "Ultra-disciplined marathon sessions. Absolute distraction blackout on short-form feeds."
    ),
    WORKING_PROFESSIONAL(
        title = "Working Professional",
        ageRange = "22+ yrs",
        recommendedFocusBlockMinutes = 50,
        recommendedBreakMinutes = 10,
        targetStudyHoursPerDay = 1.5,
        targetWorkHoursPerDay = 6.5,
        targetRestHoursPerDay = 8.0,
        targetLeisureHoursPerDay = 2.0,
        advice = "Carve out protected morning deep work blocks before email and meetings start."
    )
}

enum class ThemeMode(val label: String) {
    SYSTEM("System Default"),
    LIGHT("Always Light"),
    DARK("Always Dark"),
    SCHEDULE_ALIGNED("Schedule Aligned (Day Light / Night Dark)")
}
