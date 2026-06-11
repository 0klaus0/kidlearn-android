package com.kidlearn.app.parent

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.kidlearn.app.AppPreferences
import com.kidlearn.app.R
import com.kidlearn.app.databinding.ActivityParentDashboardBinding

/**
 * 家长控制主界面：显示4个功能入口
 * 1. 使用时间限制
 * 2. 学习进度追踪
 * 3. 应用内容管理（隐藏某年龄段内容）
 * 4. 修改密码
 */
class ParentDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParentDashboardBinding
    private lateinit var prefs: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences.getInstance(this)

        // 显示统计信息
        updateSummary()

        val pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnTimeLimit.setOnClickListener {
            it.startAnimation(pulseAnim)
            startActivity(Intent(this, TimeLimitActivity::class.java))
        }
        binding.btnProgress.setOnClickListener {
            it.startAnimation(pulseAnim)
            startActivity(Intent(this, ProgressActivity::class.java))
        }
        binding.btnContent.setOnClickListener {
            it.startAnimation(pulseAnim)
            startActivity(Intent(this, ContentManageActivity::class.java))
        }
        binding.btnChangePwd.setOnClickListener {
            it.startAnimation(pulseAnim)
            showPasswordDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        updateSummary()
    }

    private fun updateSummary() {
        val totalStars = prefs.getTotalStarsEarned()
        val todayMinutes = prefs.getTodayPlaySeconds() / 60
        val streak = prefs.getStreakDays()
        val dailyLimit = prefs.getDailyTimeLimit()

        binding.tvSummaryStars.text = "⭐ 累计获得星星：$totalStars"
        binding.tvSummaryTime.text = "⏱ 今日学习：$todayMinutes 分钟 / $dailyLimit 分钟"
        binding.tvSummaryStreak.text = "🔥 连续学习：$streak 天"
    }

    private fun showPasswordDialog() {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
        dialog.setTitle("修改家长密码")

        val input = android.widget.EditText(this).apply {
            hint = "请输入4-8位数字新密码"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
            setPadding(
                resources.getDimensionPixelSize(R.dimen.spacing_medium),
                resources.getDimensionPixelSize(R.dimen.spacing_medium),
                resources.getDimensionPixelSize(R.dimen.spacing_medium),
                resources.getDimensionPixelSize(R.dimen.spacing_medium)
            )
        }

        dialog.setView(input)
        dialog.setPositiveButton("确定") { d, _ ->
            val newPwd = input.text.toString()
            if (newPwd.length in 4..8 && newPwd.all { it.isDigit() }) {
                prefs.setPassword(newPwd)
                android.widget.Toast.makeText(this, "密码修改成功", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                android.widget.Toast.makeText(this, "密码必须为4-8位数字", android.widget.Toast.LENGTH_SHORT).show()
            }
            d.dismiss()
        }
        dialog.setNegativeButton("取消") { d, _ -> d.dismiss() }
        dialog.show()
    }
}
