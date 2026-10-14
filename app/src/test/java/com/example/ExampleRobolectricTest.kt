package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ScheduleCategory
import com.example.data.model.ScheduleItem
import com.example.data.model.StudyLevel
import com.example.data.schedule.AgeScheduleTemplates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FocusPulse", appName)
  }

  @Test
  fun `age schedule templates generate balanced blocks`() {
    val collegeSchedule = AgeScheduleTemplates.generateScheduleForLevel(StudyLevel.COLLEGE_UNDERGRAD, 20)
    assertTrue("College schedule should contain multiple scheduled blocks", collegeSchedule.size >= 5)

    val hasStudy = collegeSchedule.any { it.category == ScheduleCategory.STUDY }
    val hasWork = collegeSchedule.any { it.category == ScheduleCategory.WORK }
    val hasRest = collegeSchedule.any { it.category == ScheduleCategory.REST }
    val hasLeisure = collegeSchedule.any { it.category == ScheduleCategory.ENTERTAINMENT }

    assertTrue("Should include Study blocks", hasStudy)
    assertTrue("Should include Work blocks", hasWork)
    assertTrue("Should include Rest blocks", hasRest)
    assertTrue("Should include Leisure blocks", hasLeisure)
  }

  @Test
  fun `schedule item time formatting and duration calculation`() {
    val item = ScheduleItem(
      title = "Physics Lab",
      category = ScheduleCategory.STUDY,
      startHour = 14,
      startMinute = 30,
      endHour = 16,
      endMinute = 0
    )
    assertEquals("2:30 PM - 4:00 PM", item.formatTimeRange())
    assertEquals(90, item.durationMinutes())
  }
}
