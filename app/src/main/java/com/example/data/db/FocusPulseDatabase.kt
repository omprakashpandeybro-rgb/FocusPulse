package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BlockEvent
import com.example.data.model.BlockedAppConfig
import com.example.data.model.FocusSession
import com.example.data.model.ScheduleCategory
import com.example.data.model.ScheduleItem
import com.example.data.model.StudyLevel
import com.example.data.model.ThemeMode
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Converters {
    @TypeConverter
    fun fromScheduleCategory(category: ScheduleCategory): String = category.name

    @TypeConverter
    fun toScheduleCategory(value: String): ScheduleCategory = runCatching {
        ScheduleCategory.valueOf(value)
    }.getOrDefault(ScheduleCategory.WORK)

    @TypeConverter
    fun fromStudyLevel(level: StudyLevel): String = level.name

    @TypeConverter
    fun toStudyLevel(value: String): StudyLevel = runCatching {
        StudyLevel.valueOf(value)
    }.getOrDefault(StudyLevel.COLLEGE_UNDERGRAD)

    @TypeConverter
    fun fromThemeMode(mode: ThemeMode): String = mode.name

    @TypeConverter
    fun toThemeMode(value: String): ThemeMode = runCatching {
        ThemeMode.valueOf(value)
    }.getOrDefault(ThemeMode.SYSTEM)
}

@Database(
    entities = [
        ScheduleItem::class,
        FocusSession::class,
        BlockedAppConfig::class,
        BlockEvent::class,
        UserProfile::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FocusPulseDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun blockedAppDao(): BlockedAppDao
    abstract fun blockEventDao(): BlockEventDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: FocusPulseDatabase? = null

        fun getDatabase(context: Context): FocusPulseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FocusPulseDatabase::class.java,
                    "focus_pulse_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(db: FocusPulseDatabase) {
                // Initial User Profile
                db.userProfileDao().insertProfile(
                    UserProfile(
                        id = 1,
                        name = "Alex",
                        age = 19,
                        studyLevel = StudyLevel.COLLEGE_UNDERGRAD,
                        dailyGoalMinutes = 240,
                        themeMode = ThemeMode.SYSTEM,
                        autoBlockShorts = true
                    )
                )

                // Initial Preconfigured Blocked Apps & Content
                val defaultBlocked = listOf(
                    BlockedAppConfig(
                        packageName = "com.google.android.youtube",
                        appName = "YouTube",
                        isBlocked = false,
                        blockShortsOnly = true,
                        category = "Video"
                    ),
                    BlockedAppConfig(
                        packageName = "com.instagram.android",
                        appName = "Instagram",
                        isBlocked = true,
                        blockShortsOnly = false,
                        category = "Social"
                    ),
                    BlockedAppConfig(
                        packageName = "com.zhiliaoapp.musically",
                        appName = "TikTok",
                        isBlocked = true,
                        blockShortsOnly = false,
                        category = "Social"
                    ),
                    BlockedAppConfig(
                        packageName = "com.twitter.android",
                        appName = "X (Twitter)",
                        isBlocked = true,
                        blockShortsOnly = false,
                        category = "Social"
                    ),
                    BlockedAppConfig(
                        packageName = "com.snapchat.android",
                        appName = "Snapchat",
                        isBlocked = true,
                        blockShortsOnly = false,
                        category = "Social"
                    ),
                    BlockedAppConfig(
                        packageName = "com.reddit.frontpage",
                        appName = "Reddit",
                        isBlocked = true,
                        blockShortsOnly = false,
                        category = "Social"
                    ),
                    BlockedAppConfig(
                        packageName = "com.facebook.katana",
                        appName = "Facebook",
                        isBlocked = true,
                        blockShortsOnly = false,
                        category = "Social"
                    ),
                    BlockedAppConfig(
                        packageName = "com.supercell.brawlstars",
                        appName = "Brawl Stars",
                        isBlocked = true,
                        blockShortsOnly = false,
                        category = "Gaming"
                    ),
                    BlockedAppConfig(
                        packageName = "com.roblox.client",
                        appName = "Roblox",
                        isBlocked = true,
                        blockShortsOnly = false,
                        category = "Gaming"
                    )
                )
                db.blockedAppDao().insertAll(defaultBlocked)

                // Initial College Default Schedule Template
                val defaultSchedule = listOf(
                    ScheduleItem(
                        title = "Morning Routine & Hydration",
                        description = "Light stretches, healthy breakfast, plan the daily priorities",
                        category = ScheduleCategory.ROUTINE,
                        startHour = 7,
                        startMinute = 30,
                        endHour = 8,
                        endMinute = 30,
                        isAlarmEnabled = true,
                        isFocusShieldEnabled = false
                    ),
                    ScheduleItem(
                        title = "Deep Study Block 1: Core Concepts",
                        description = "Lecture revision & practice problems (YouTube Study permitted, Shorts blocked)",
                        category = ScheduleCategory.STUDY,
                        startHour = 9,
                        startMinute = 0,
                        endHour = 11,
                        endMinute = 30,
                        isAlarmEnabled = true,
                        isFocusShieldEnabled = true
                    ),
                    ScheduleItem(
                        title = "Lunch & Walk Break",
                        description = "Mindful lunch, outdoor walk, disconnect from screens",
                        category = ScheduleCategory.REST,
                        startHour = 12,
                        startMinute = 0,
                        endHour = 13,
                        endMinute = 0,
                        isAlarmEnabled = true,
                        isFocusShieldEnabled = false
                    ),
                    ScheduleItem(
                        title = "Focused Work: Assignments & Code",
                        description = "Major coursework, lab prep, essay or coding projects",
                        category = ScheduleCategory.WORK,
                        startHour = 14,
                        startMinute = 0,
                        endHour = 16,
                        endMinute = 30,
                        isAlarmEnabled = true,
                        isFocusShieldEnabled = true
                    ),
                    ScheduleItem(
                        title = "Fitness & Physical Reset",
                        description = "Gym workout, jogging or bodyweight training",
                        category = ScheduleCategory.REST,
                        startHour = 17,
                        startMinute = 0,
                        endHour = 18,
                        endMinute = 15,
                        isAlarmEnabled = true,
                        isFocusShieldEnabled = false
                    ),
                    ScheduleItem(
                        title = "Evening Review & Flashcards",
                        description = "Spaced repetition, recap what was learned today",
                        category = ScheduleCategory.STUDY,
                        startHour = 19,
                        startMinute = 30,
                        endHour = 20,
                        endMinute = 45,
                        isAlarmEnabled = true,
                        isFocusShieldEnabled = true
                    ),
                    ScheduleItem(
                        title = "Free Leisure & Gaming",
                        description = "Social time, casual games, entertainment guilt-free",
                        category = ScheduleCategory.ENTERTAINMENT,
                        startHour = 21,
                        startMinute = 0,
                        endHour = 22,
                        endMinute = 30,
                        isAlarmEnabled = true,
                        isFocusShieldEnabled = false
                    )
                )
                db.scheduleDao().insertAll(defaultSchedule)
            }
        }
    }
}
