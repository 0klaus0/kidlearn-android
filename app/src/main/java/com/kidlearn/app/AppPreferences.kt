package com.kidlearn.app

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * 应用全局偏好设置管理
 * 管理：年龄分组、学习进度、星星数量、贴纸解锁、时间限制等
 */
class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "kidlearn_prefs"

        // 年龄设置
        private const val KEY_AGE_GROUP = "age_group"
        private const val KEY_AGE_SET = "age_set"

        // 星星和奖励
        private const val KEY_STARS = "stars"
        private const val KEY_UNLOCKED_STICKERS = "unlocked_stickers"
        private const val KEY_UNLOCKED_ACHIEVEMENTS = "unlocked_achievements"

        // 学习进度（按天统计）
        private const val KEY_LAST_PLAY_DATE = "last_play_date"
        private const val KEY_TOTAL_STAR_EARNED = "total_stars_earned"

        // 每日学习时长（秒）
        private const val KEY_TODAY_PLAY_TIME = "today_play_seconds"
        private const val KEY_TODAY_DATE = "today_date"

        // 各类学习任务完成数
        private const val KEY_NUMBERS_COMPLETED = "numbers_completed"
        private const val KEY_LETTERS_COMPLETED = "letters_completed"
        private const val KEY_COLORS_COMPLETED = "colors_completed"
        private const val KEY_SHAPES_COMPLETED = "shapes_completed"
        private const val KEY_GAMES_PLAYED = "games_played"

        // 连续学习天数
        private const val KEY_STREAK_DAYS = "streak_days"

        // 家长控制
        private const val KEY_PARENT_PASSWORD = "parent_password"
        private const val KEY_DAILY_TIME_LIMIT = "daily_time_limit"   // 分钟
        private const val KEY_WEEKLY_TIME_LIMIT = "weekly_time_limit" // 分钟
        private const val KEY_WEEKLY_USAGE = "weekly_usage"           // 分钟

        // 音量/静音
        private const val KEY_SOUND_ENABLED = "sound_enabled"

        @Volatile
        private var instance: AppPreferences? = null

        fun getInstance(context: Context): AppPreferences {
            return instance ?: synchronized(this) {
                instance ?: AppPreferences(context.applicationContext).also { instance = it }
            }
        }

        private fun todayStr(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }

        private fun weekKey(): String {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val week = cal.get(Calendar.WEEK_OF_YEAR)
            return "${year}_W$week"
        }
    }

    // ======= 年龄设置 =======
    fun getAgeGroup(): AgeGroup {
        val value = prefs.getString(KEY_AGE_GROUP, AgeGroup.AGE_2_3.name)!!
        return AgeGroup.fromString(value)
    }

    fun setAgeGroup(age: AgeGroup) {
        prefs.edit().putString(KEY_AGE_GROUP, age.name).apply()
        prefs.edit().putBoolean(KEY_AGE_SET, true).apply()
    }

    fun isAgeSet(): Boolean = prefs.getBoolean(KEY_AGE_SET, false)

    // ======= 星星 =======
    fun getStars(): Int = prefs.getInt(KEY_STARS, 0)

    fun addStars(count: Int) {
        val current = getStars()
        val new = current + count
        prefs.edit().putInt(KEY_STARS, new).apply()
        prefs.edit().putInt(KEY_TOTAL_STAR_EARNED,
            prefs.getInt(KEY_TOTAL_STAR_EARNED, 0) + count).apply()
        checkStickerUnlock(new)
        checkAchievementUnlock(new)
    }

    fun spendStars(count: Int) {
        val current = getStars()
        if (current >= count) {
            prefs.edit().putInt(KEY_STARS, current - count).apply()
        }
    }

    fun getTotalStarsEarned(): Int = prefs.getInt(KEY_TOTAL_STAR_EARNED, 0)

    // ======= 贴纸 =======
    private fun checkStickerUnlock(total: Int) {
        val unlocked = getUnlockedStickers().toMutableSet()
        StickerItem.getAll().forEach { sticker ->
            if (total >= sticker.requiredStars && !unlocked.contains(sticker.id)) {
                unlocked.add(sticker.id)
            }
        }
        prefs.edit().putStringSet(KEY_UNLOCKED_STICKERS, unlocked).apply()
    }

    fun getUnlockedStickers(): Set<String> {
        return prefs.getStringSet(KEY_UNLOCKED_STICKERS, emptySet())!!
    }

    // ======= 成就 =======
    private fun checkAchievementUnlock(totalStars: Int) {
        val unlocked = getUnlockedAchievements().toMutableSet()
        val checkAndUnlock = { id: String, condition: Boolean ->
            if (condition && !unlocked.contains(id)) {
                unlocked.add(id)
            }
        }

        checkAndUnlock("ach_first_star", totalStars >= 1)
        checkAndUnlock("ach_ten_stars", totalStars >= 10)
        checkAndUnlock("ach_fifty_stars", totalStars >= 50)
        checkAndUnlock("ach_number_master", getNumbersCompleted() >= 11)
        checkAndUnlock("ach_letter_master", getLettersCompleted() >= 26)
        checkAndUnlock("ach_color_master", getColorsCompleted() >= 8)
        checkAndUnlock("ach_shape_master", getShapesCompleted() >= 6)
        checkAndUnlock("ach_game_master", getGamesPlayed() >= 10)
        checkAndUnlock("ach_first_day", getStreakDays() >= 1)
        checkAndUnlock("ach_seven_days", getStreakDays() >= 7)

        prefs.edit().putStringSet(KEY_UNLOCKED_ACHIEVEMENTS, unlocked).apply()
    }

    fun getUnlockedAchievements(): Set<String> {
        return prefs.getStringSet(KEY_UNLOCKED_ACHIEVEMENTS, emptySet())!!
    }

    // ======= 学习任务计数 =======
    fun incrementNumbersCompleted() {
        val n = getNumbersCompleted() + 1
        prefs.edit().putInt(KEY_NUMBERS_COMPLETED, n).apply()
        checkAchievementUnlock(getTotalStarsEarned())
    }

    fun getNumbersCompleted(): Int = prefs.getInt(KEY_NUMBERS_COMPLETED, 0)

    fun incrementLettersCompleted() {
        val n = getLettersCompleted() + 1
        prefs.edit().putInt(KEY_LETTERS_COMPLETED, n).apply()
    }

    fun getLettersCompleted(): Int = prefs.getInt(KEY_LETTERS_COMPLETED, 0)

    fun incrementColorsCompleted() {
        val n = getColorsCompleted() + 1
        prefs.edit().putInt(KEY_COLORS_COMPLETED, n).apply()
    }

    fun getColorsCompleted(): Int = prefs.getInt(KEY_COLORS_COMPLETED, 0)

    fun incrementShapesCompleted() {
        val n = getShapesCompleted() + 1
        prefs.edit().putInt(KEY_SHAPES_COMPLETED, n).apply()
    }

    fun getShapesCompleted(): Int = prefs.getInt(KEY_SHAPES_COMPLETED, 0)

    fun incrementGamesPlayed() {
        val n = getGamesPlayed() + 1
        prefs.edit().putInt(KEY_GAMES_PLAYED, n).apply()
    }

    fun getGamesPlayed(): Int = prefs.getInt(KEY_GAMES_PLAYED, 0)

    // ======= 学习时长 =======
    fun addPlayTime(seconds: Int) {
        val today = todayStr()
        val saved = prefs.getString(KEY_TODAY_DATE, "")
        var current = if (saved == today) {
            prefs.getInt(KEY_TODAY_PLAY_TIME, 0)
        } else {
            0
        }
        current += seconds
        prefs.edit().putString(KEY_TODAY_DATE, today).apply()
        prefs.edit().putInt(KEY_TODAY_PLAY_TIME, current).apply()
    }

    fun getTodayPlaySeconds(): Int {
        val today = todayStr()
        return if (prefs.getString(KEY_TODAY_DATE, "") == today) {
            prefs.getInt(KEY_TODAY_PLAY_TIME, 0)
        } else 0
    }

    fun getTodayPlayMinutes(): Int = getTodayPlaySeconds() / 60

    // ======= 连续学习天数 =======
    fun recordPlayDate() {
        val today = todayStr()
        val last = prefs.getString(KEY_LAST_PLAY_DATE, "")
        if (last == today) return

        val lastDate = if (last.isNotEmpty()) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.parse(last)
        } else null

        val cal1 = Calendar.getInstance()
        cal1.time = lastDate ?: Date()
        val cal2 = Calendar.getInstance()
        cal2.time = Date()

        val diffDays = if (lastDate != null) {
            val diff = cal2.timeInMillis - cal1.timeInMillis
            (diff / (1000 * 60 * 60 * 24)).toInt()
        } else 1

        val currentStreak = prefs.getInt(KEY_STREAK_DAYS, 0)
        val newStreak = when {
            diffDays == 1 -> currentStreak + 1
            diffDays == 0 -> currentStreak
            else -> 1
        }
        prefs.edit().putInt(KEY_STREAK_DAYS, newStreak).apply()
        prefs.edit().putString(KEY_LAST_PLAY_DATE, today).apply()
        checkAchievementUnlock(getTotalStarsEarned())
    }

    fun getStreakDays(): Int = prefs.getInt(KEY_STREAK_DAYS, 0)

    // ======= 家长控制：时间限制 =======
    fun getDailyTimeLimit(): Int = prefs.getInt(KEY_DAILY_TIME_LIMIT, 30)  // 默认30分钟

    fun setDailyTimeLimit(minutes: Int) {
        prefs.edit().putInt(KEY_DAILY_TIME_LIMIT, minutes).apply()
    }

    fun getWeeklyTimeLimit(): Int = prefs.getInt(KEY_WEEKLY_TIME_LIMIT, 210) // 默认3.5小时

    fun setWeeklyTimeLimit(minutes: Int) {
        prefs.edit().putInt(KEY_WEEKLY_TIME_LIMIT, minutes).apply()
    }

    fun isDailyTimeUp(): Boolean {
        return getTodayPlayMinutes() >= getDailyTimeLimit()
    }

    // ======= 家长密码 =======
    fun checkPassword(input: String): Boolean {
        val saved = prefs.getString(KEY_PARENT_PASSWORD, "1234")!!
        return input == saved
    }

    fun setPassword(newPassword: String) {
        prefs.edit().putString(KEY_PARENT_PASSWORD, newPassword).apply()
    }

    fun hasPassword(): Boolean = prefs.contains(KEY_PARENT_PASSWORD)

    // ======= 音量 =======
    fun isSoundEnabled(): Boolean = prefs.getBoolean(KEY_SOUND_ENABLED, true)

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }

    // ======= 重置数据（测试用） =======
    fun resetAll() {
        prefs.edit().clear().apply()
    }
}
