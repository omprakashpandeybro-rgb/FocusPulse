package com.example.data.schedule

import com.example.data.model.ScheduleCategory
import com.example.data.model.ScheduleItem
import com.example.data.model.StudyLevel

object AgeScheduleTemplates {

    fun generateScheduleForLevel(level: StudyLevel, age: Int): List<ScheduleItem> {
        return when (level) {
            StudyLevel.MIDDLE_SCHOOL -> listOf(
                ScheduleItem(
                    title = "Morning Routine & School Prep",
                    description = "Breakfast, pack backpack, review day's subjects",
                    category = ScheduleCategory.ROUTINE,
                    startHour = 7,
                    startMinute = 0,
                    endHour = 8,
                    endMinute = 0,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "School & Class Time",
                    description = "Core classes and classroom engagement",
                    category = ScheduleCategory.STUDY,
                    startHour = 8,
                    startMinute = 15,
                    endHour = 14,
                    endMinute = 45,
                    isAlarmEnabled = false,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Afternoon Snack & Outdoor Play",
                    description = "Rest and outdoor sports / physical activity",
                    category = ScheduleCategory.REST,
                    startHour = 15,
                    startMinute = 0,
                    endHour = 16,
                    endMinute = 0,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Homework & Study Sprint 1",
                    description = "Focused homework (Shorts blocked, educational search on)",
                    category = ScheduleCategory.STUDY,
                    startHour = 16,
                    startMinute = 15,
                    endHour = 17,
                    endMinute = 15,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = true
                ),
                ScheduleItem(
                    title = "Reading & Creative Project",
                    description = "Reading books, science project, or coding basics",
                    category = ScheduleCategory.WORK,
                    startHour = 17,
                    startMinute = 30,
                    endHour = 18,
                    endMinute = 30,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = true
                ),
                ScheduleItem(
                    title = "Dinner & Family Time",
                    description = "Healthy dinner and conversation",
                    category = ScheduleCategory.REST,
                    startHour = 18,
                    startMinute = 30,
                    endHour = 19,
                    endMinute = 30,
                    isAlarmEnabled = false,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Gaming & Entertainment",
                    description = "Allowed games or shows before bedtime",
                    category = ScheduleCategory.ENTERTAINMENT,
                    startHour = 19,
                    startMinute = 30,
                    endHour = 20,
                    endMinute = 30,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Sleep & Restorative Rest",
                    description = "Phone away, full 9 hours sleep",
                    category = ScheduleCategory.REST,
                    startHour = 21,
                    startMinute = 0,
                    endHour = 7,
                    endMinute = 0,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                )
            )

            StudyLevel.HIGH_SCHOOL -> listOf(
                ScheduleItem(
                    title = "Morning Focus Awakening",
                    description = "Hydration, quick stretch, breakfast and mental prep",
                    category = ScheduleCategory.ROUTINE,
                    startHour = 6,
                    startMinute = 45,
                    endHour = 7,
                    endMinute = 45,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "School Academic Block",
                    description = "Classes, notes, and academic lectures",
                    category = ScheduleCategory.STUDY,
                    startHour = 8,
                    startMinute = 0,
                    endHour = 14,
                    endMinute = 30,
                    isAlarmEnabled = false,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Decompression & Sports / Walk",
                    description = "Physical movement, recharge mind after school",
                    category = ScheduleCategory.REST,
                    startHour = 15,
                    startMinute = 0,
                    endHour = 16,
                    endMinute = 0,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Deep Study Block: STEM & Assignments",
                    description = "Math, Science & test prep (YouTube tutorials allowed, Shorts shielded)",
                    category = ScheduleCategory.STUDY,
                    startHour = 16,
                    startMinute = 15,
                    endHour = 18,
                    endMinute = 15,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = true
                ),
                ScheduleItem(
                    title = "Dinner & Break",
                    description = "Healthy nutrition & disconnect",
                    category = ScheduleCategory.REST,
                    startHour = 18,
                    startMinute = 30,
                    endHour = 19,
                    endMinute = 15,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Evening Deep Work: Projects & Essay Writing",
                    description = "Writing, lab reports, college prep tasks",
                    category = ScheduleCategory.WORK,
                    startHour = 19,
                    startMinute = 30,
                    endHour = 21,
                    endMinute = 0,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = true
                ),
                ScheduleItem(
                    title = "Entertainment & Friends",
                    description = "Protected social media and gaming window",
                    category = ScheduleCategory.ENTERTAINMENT,
                    startHour = 21,
                    startMinute = 0,
                    endHour = 22,
                    endMinute = 0,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Wind Down & Sleep",
                    description = "Blue light filter, reading, restorative sleep",
                    category = ScheduleCategory.REST,
                    startHour = 22,
                    startMinute = 15,
                    endHour = 6,
                    endMinute = 45,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                )
            )

            StudyLevel.COLLEGE_UNDERGRAD -> listOf(
                ScheduleItem(
                    title = "Morning Routine & Goal Setting",
                    description = "Morning hydration, planning daily target modules",
                    category = ScheduleCategory.ROUTINE,
                    startHour = 7,
                    startMinute = 30,
                    endHour = 8,
                    endMinute = 30,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Deep Study Sprint: Hardest Academic Topic",
                    description = "High-concentration conceptual revision (Distraction Shield Active)",
                    category = ScheduleCategory.STUDY,
                    startHour = 9,
                    startMinute = 0,
                    endHour = 11,
                    endMinute = 30,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = true
                ),
                ScheduleItem(
                    title = "Lunch & Campus Walk",
                    description = "Nutritious meal and fresh air reset",
                    category = ScheduleCategory.REST,
                    startHour = 12,
                    startMinute = 0,
                    endHour = 13,
                    endMinute = 0,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Project Deep Work & Coding",
                    description = "Major coursework, term papers, lab analysis, software builds",
                    category = ScheduleCategory.WORK,
                    startHour = 13,
                    startMinute = 30,
                    endHour = 16,
                    endMinute = 0,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = true
                ),
                ScheduleItem(
                    title = "Workout / Fitness Recharge",
                    description = "Gym workout, cycling, cardiovascular reset",
                    category = ScheduleCategory.REST,
                    startHour = 16,
                    startMinute = 30,
                    endHour = 17,
                    endMinute = 45,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Evening Revision & Practice Sets",
                    description = "Practice problem sets and spaced repetition cards",
                    category = ScheduleCategory.STUDY,
                    startHour = 19,
                    startMinute = 0,
                    endHour = 20,
                    endMinute = 30,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = true
                ),
                ScheduleItem(
                    title = "Social Leisure & Entertainment",
                    description = "Gaming, streaming shows, unwinding with peers",
                    category = ScheduleCategory.ENTERTAINMENT,
                    startHour = 20,
                    startMinute = 45,
                    endHour = 22,
                    endMinute = 30,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Wind Down & Sleep",
                    description = "Sleep optimization for memory consolidation",
                    category = ScheduleCategory.REST,
                    startHour = 23,
                    startMinute = 0,
                    endHour = 7,
                    endMinute = 30,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                )
            )

            StudyLevel.GRADUATE_EXAMS -> listOf(
                ScheduleItem(
                    title = "Pre-Dawn Priming & Cold Shower",
                    description = "Caffeine, hydration, agenda review for competitive syllabus",
                    category = ScheduleCategory.ROUTINE,
                    startHour = 6,
                    startMinute = 0,
                    endHour = 6,
                    endMinute = 45,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Ultra-Study Marathon Block 1",
                    description = "High-yield topics & question bank simulation (All Feeds Blocked)",
                    category = ScheduleCategory.STUDY,
                    startHour = 7,
                    startMinute = 0,
                    endHour = 10,
                    endMinute = 0,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = true
                ),
                ScheduleItem(
                    title = "Mid-Day Walk & Nutrition Break",
                    description = "Brain food, light sunlight exposure, mental silence",
                    category = ScheduleCategory.REST,
                    startHour = 10,
                    startMinute = 30,
                    endHour = 11,
                    endMinute = 30,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Ultra-Study Marathon Block 2: Weak Areas",
                    description = "Targeting difficult mistakes & deep theoretical review",
                    category = ScheduleCategory.STUDY,
                    startHour = 12,
                    startMinute = 0,
                    endHour = 15,
                    endMinute = 0,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = true
                ),
                ScheduleItem(
                    title = "Active Physical Movement",
                    description = "Jogging or brisk walk to lower cortisol",
                    category = ScheduleCategory.REST,
                    startHour = 15,
                    startMinute = 30,
                    endHour = 16,
                    endMinute = 30,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Mock Exam / Timed Section Practice",
                    description = "Full test simulation under strict exam condition timing",
                    category = ScheduleCategory.WORK,
                    startHour = 17,
                    startMinute = 0,
                    endHour = 19,
                    endMinute = 30,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = true
                ),
                ScheduleItem(
                    title = "Dinner & Mindful Relaxation",
                    description = "Casual reading or podcast, light conversation",
                    category = ScheduleCategory.ENTERTAINMENT,
                    startHour = 20,
                    startMinute = 0,
                    endHour = 21,
                    endMinute = 15,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Deep Sleep for Memory Retention",
                    description = "Uninterrupted rest for neural synthesis",
                    category = ScheduleCategory.REST,
                    startHour = 21,
                    startMinute = 45,
                    endHour = 6,
                    endMinute = 0,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                )
            )

            StudyLevel.WORKING_PROFESSIONAL -> listOf(
                ScheduleItem(
                    title = "Morning Prime & Exercise",
                    description = "Cardio, hydration, setting top 3 priority outcomes",
                    category = ScheduleCategory.ROUTINE,
                    startHour = 6,
                    startMinute = 30,
                    endHour = 7,
                    endMinute = 30,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Protected Deep Work: High ROI Output",
                    description = "Architecture, core deliverables, strategic analysis (No Slack/Social)",
                    category = ScheduleCategory.WORK,
                    startHour = 8,
                    startMinute = 30,
                    endHour = 11,
                    endMinute = 30,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = true
                ),
                ScheduleItem(
                    title = "Lunch & Mental Reset",
                    description = "Step away from desk, eye relief, fresh air",
                    category = ScheduleCategory.REST,
                    startHour = 12,
                    startMinute = 0,
                    endHour = 13,
                    endMinute = 0,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Collaborative Execution & Meetings",
                    description = "Syncs, communication, review requests, tickets",
                    category = ScheduleCategory.WORK,
                    startHour = 13,
                    startMinute = 30,
                    endHour = 16,
                    endMinute = 30,
                    isAlarmEnabled = false,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Skill Upskilling & Professional Study",
                    description = "Technical courses, certifications, industry papers",
                    category = ScheduleCategory.STUDY,
                    startHour = 17,
                    startMinute = 0,
                    endHour = 18,
                    endMinute = 15,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = true
                ),
                ScheduleItem(
                    title = "Dinner & Family Time",
                    description = "Quality connection & recharging",
                    category = ScheduleCategory.REST,
                    startHour = 18,
                    startMinute = 45,
                    endHour = 20,
                    endMinute = 0,
                    isAlarmEnabled = false,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Leisure, Streaming & Hobbies",
                    description = "Shows, games, casual reading",
                    category = ScheduleCategory.ENTERTAINMENT,
                    startHour = 20,
                    startMinute = 15,
                    endHour = 22,
                    endMinute = 0,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                ),
                ScheduleItem(
                    title = "Sleep & Recovery",
                    description = "Complete night recovery",
                    category = ScheduleCategory.REST,
                    startHour = 22,
                    startMinute = 30,
                    endHour = 6,
                    endMinute = 30,
                    isAlarmEnabled = true,
                    isFocusShieldEnabled = false
                )
            )
        }
    }
}
