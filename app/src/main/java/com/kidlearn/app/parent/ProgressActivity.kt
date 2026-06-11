package com.kidlearn.app.parent

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kidlearn.app.AppPreferences
import com.kidlearn.app.R
import com.kidlearn.app.databinding.ActivityProgressBinding

/**
 * 学习进度报表页：按知识点和时间维度统计儿童学习情况
 */
class ProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressBinding
    private lateinit var prefs: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences.getInstance(this)
        binding.btnBack.setOnClickListener { finish() }

        generateReport()
    }

    private fun generateReport() {
        val totalStars = prefs.getTotalStarsEarned()
        val currentStars = prefs.getStars()
        val todayMinutes = prefs.getTodayPlaySeconds() / 60
        val dailyLimit = prefs.getDailyTimeLimit()
        val streak = prefs.getStreakDays()

        val numbersDone = prefs.getNumbersCompleted()
        val lettersDone = prefs.getLettersCompleted()
        val colorsDone = prefs.getColorsCompleted()
        val shapesDone = prefs.getShapesCompleted()
        val gamesPlayed = prefs.getGamesPlayed()
        val totalTasks = numbersDone + lettersDone + colorsDone + shapesDone + gamesPlayed

        // 今日概况
        binding.tvReportToday.text = """
            📅 今日学习报告：
            ・学习时长：$todayMinutes 分钟 / $dailyLimit 分钟
            ・完成任务数：$totalTasks 个
            ・获得星星：$currentStars ⭐
            ・连续学习天数：$streak 天
        """.trimIndent()

        // 各模块进度（使用简单的百分比表示）
        val numbersPercent = minOf(100, numbersDone * 10)
        val lettersPercent = minOf(100, lettersDone * 4)
        val colorsPercent = minOf(100, colorsDone * 12)
        val shapesPercent = minOf(100, shapesDone * 17)
        val gamesPercent = minOf(100, gamesPlayed * 10)

        addProgressRow("🔢 数字学习", numbersDone, numbersPercent)
        addProgressRow("🔤 字母学习", lettersDone, lettersPercent)
        addProgressRow("🎨 颜色学习", colorsDone, colorsPercent)
        addProgressRow("🔷 形状学习", shapesDone, shapesPercent)
        addProgressRow("🎮 游戏互动", gamesPlayed, gamesPercent)

        // 总体评价
        val totalPercent = (numbersPercent + lettersPercent + colorsPercent + shapesPercent + gamesPercent) / 5
        val evaluation = when {
            totalPercent >= 80 -> "非常棒！小朋友学习很认真，继续加油！🌟"
            totalPercent >= 50 -> "不错哦！小朋友在学习中取得了进步！👍"
            totalPercent >= 20 -> "还在学习中，多陪伴小朋友一起学习吧！💪"
            else -> "刚开始学习，加油！多引导小朋友探索各个功能模块！🌈"
        }
        binding.tvEvaluation.text = "总体评价：\n$evaluation"
    }

    private fun addProgressRow(label: String, count: Int, percent: Int) {
        val rowView = layoutInflater.inflate(R.layout.row_progress, null)
        val tvLabel = rowView.findViewById<TextView>(R.id.tvLabel)
        val tvCount = rowView.findViewById<TextView>(R.id.tvCount)
        val progressBar = rowView.findViewById<android.widget.ProgressBar>(R.id.progressBar)

        tvLabel.text = label
        tvCount.text = "$count 次 ($percent%)"
        progressBar.progress = percent

        binding.progressContainer.addView(rowView)
    }
}
