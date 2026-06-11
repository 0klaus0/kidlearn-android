package com.kidlearn.app.parent

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kidlearn.app.AppPreferences
import com.kidlearn.app.R
import com.kidlearn.app.databinding.ActivityTimeLimitBinding

/**
 * 时间限制设置页
 * 设置每日/每周可用时长，并显示今日已用时长
 */
class TimeLimitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimeLimitBinding
    private lateinit var prefs: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeLimitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences.getInstance(this)

        binding.btnBack.setOnClickListener { finish() }
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        updateCurrentUsage()
    }

    private fun setupUI() {
        updateCurrentUsage()

        // 每日时长选项（10, 20, 30, 45, 60, 90, 120 分钟）
        val dailyOptions = intArrayOf(10, 20, 30, 45, 60, 90, 120)
        val currentDaily = prefs.getDailyTimeLimit()
        val currentWeekly = prefs.getWeeklyTimeLimit()

        dailyOptions.forEach { minutes ->
            val radio = RadioButton(this).apply {
                text = "$minutes 分钟 / 天"
                textSize = 20f
                isChecked = (minutes == currentDaily)
                id = minutes
                setPadding(
                    resources.getDimensionPixelSize(R.dimen.spacing_medium),
                    resources.getDimensionPixelSize(R.dimen.spacing_medium),
                    resources.getDimensionPixelSize(R.dimen.spacing_medium),
                    resources.getDimensionPixelSize(R.dimen.spacing_medium)
                )
                setTextColor(resources.getColor(R.color.text_primary, null))
            }
            binding.dailyGroup.addView(radio)
        }

        // 每周时长选项
        val weeklyOptions = intArrayOf(60, 120, 180, 240, 300, 420, 600)
        weeklyOptions.forEach { minutes ->
            val radio = RadioButton(this).apply {
                val hours = minutes / 60
                text = if (hours >= 1) "$hours 小时 / 周" else "$minutes 分钟 / 周"
                textSize = 20f
                isChecked = (minutes == currentWeekly)
                id = 10000 + minutes
                setPadding(
                    resources.getDimensionPixelSize(R.dimen.spacing_medium),
                    resources.getDimensionPixelSize(R.dimen.spacing_medium),
                    resources.getDimensionPixelSize(R.dimen.spacing_medium),
                    resources.getDimensionPixelSize(R.dimen.spacing_medium)
                )
                setTextColor(resources.getColor(R.color.text_primary, null))
            }
            binding.weeklyGroup.addView(radio)
        }

        binding.btnSave.setOnClickListener {
            val selectedDaily = binding.dailyGroup.checkedRadioButtonId
            if (selectedDaily > 0 && selectedDaily < 10000) {
                prefs.setDailyTimeLimit(selectedDaily)
            }
            val selectedWeekly = binding.weeklyGroup.checkedRadioButtonId
            if (selectedWeekly >= 10000) {
                prefs.setWeeklyTimeLimit(selectedWeekly - 10000)
            }
            android.widget.Toast.makeText(this, "已保存设置", android.widget.Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateCurrentUsage() {
        val todaySeconds = prefs.getTodayPlaySeconds()
        val todayMinutes = todaySeconds / 60
        val dailyLimit = prefs.getDailyTimeLimit()
        val remaining = maxOf(0, dailyLimit - todayMinutes)

        binding.tvCurrentUsage.text = "今日已学习：$todayMinutes 分钟\n每日限制：$dailyLimit 分钟\n剩余：$remaining 分钟"
    }
}
