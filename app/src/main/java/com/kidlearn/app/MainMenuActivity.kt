package com.kidlearn.app

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kidlearn.app.databinding.ActivityMainMenuBinding
import com.kidlearn.app.parent.ParentLoginActivity

/**
 * 主菜单 Activity - 儿童进入后看到的主界面
 *
 * 导航结构极简：用户从此页面单次点击即可到达任何功能模块
 *
 * 入口：
 * - 数字学习
 * - 字母学习
 * - 颜色学习
 * - 形状学习
 * - 游戏
 * - 奖励收藏
 * - 家长中心（受密码保护）
 */
class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding
    private lateinit var prefs: AppPreferences
    private lateinit var audio: AudioManager
    private var startTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences.getInstance(this)
        audio = AudioManager.getInstance(this)

        prefs.recordPlayDate()
        startTime = System.currentTimeMillis()

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        updateStarDisplay()
        checkDailyTime()
    }

    override fun onPause() {
        super.onPause()
        // 记录使用时长
        val elapsed = (System.currentTimeMillis() - startTime) / 1000
        if (elapsed > 0) {
            prefs.addPlayTime(elapsed.toInt())
        }
    }

    private fun setupUI() {
        updateStarDisplay()

        val pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse)

        // 六个大按钮菜单
        binding.btnNumbers.setOnClickListener {
            it.startAnimation(pulseAnim)
            audio.playClickSound()
            startActivity(Intent(this, NumbersActivity::class.java))
        }
        binding.btnLetters.setOnClickListener {
            it.startAnimation(pulseAnim)
            audio.playClickSound()
            startActivity(Intent(this, LettersActivity::class.java))
        }
        binding.btnColors.setOnClickListener {
            it.startAnimation(pulseAnim)
            audio.playClickSound()
            startActivity(Intent(this, ColorsActivity::class.java))
        }
        binding.btnShapes.setOnClickListener {
            it.startAnimation(pulseAnim)
            audio.playClickSound()
            startActivity(Intent(this, ShapesActivity::class.java))
        }
        binding.btnGames.setOnClickListener {
            it.startAnimation(pulseAnim)
            audio.playClickSound()
            startActivity(Intent(this, GamesActivity::class.java))
        }
        binding.btnRewards.setOnClickListener {
            it.startAnimation(pulseAnim)
            audio.playClickSound()
            startActivity(Intent(this, RewardsActivity::class.java))
        }
        binding.btnParent.setOnClickListener {
            it.startAnimation(pulseAnim)
            startActivity(Intent(this, ParentLoginActivity::class.java))
        }
        binding.btnSound.setOnClickListener {
            val enabled = audio.toggleSound()
            binding.btnSound.text = if (enabled) "🔊" else "🔇"
            val msg = if (enabled) R.string.sound_on else R.string.sound_off
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
        binding.btnChangeAge.setOnClickListener {
            prefs.resetAll()
            val intent = Intent(this, AgeSelectActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun updateStarDisplay() {
        val stars = prefs.getStars()
        binding.tvStars.text = "⭐ $stars"
        val age = prefs.getAgeGroup()
        val ageText = when (age) {
            AgeGroup.AGE_2_3 -> "2-3岁"
            AgeGroup.AGE_4_5 -> "4-5岁"
            AgeGroup.AGE_5_6 -> "5-6岁"
        }
        binding.tvAge.text = "年龄段：$ageText"
    }

    private fun checkDailyTime() {
        if (prefs.isDailyTimeUp()) {
            // 显示对话框提示今日使用时间结束
            android.app.AlertDialog.Builder(this)
                .setTitle("休息一下")
                .setMessage("今天已经学习了很长时间啦，明天再来玩吧！")
                .setPositiveButton("好的") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
        }
    }

    override fun onBackPressed() {
        // 不允许返回（防止小朋友随意退出）
        Toast.makeText(this, "点击右上角家长中心可以调整设置", Toast.LENGTH_SHORT).show()
    }
}
