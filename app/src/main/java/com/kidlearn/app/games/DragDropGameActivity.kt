package com.kidlearn.app.games

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kidlearn.app.AppPreferences
import com.kidlearn.app.AudioManager
import com.kidlearn.app.ColorItem
import com.kidlearn.app.MainMenuActivity
import com.kidlearn.app.R
import com.kidlearn.app.databinding.ActivityGameDragBinding
import kotlin.random.Random

/**
 * 拖拽游戏（简化版：点击匹配）：
 * 显示一个目标区域（如"红色"），让儿童点击下方对应颜色的按钮
 * 操作方式使用点击（更稳定，更适合儿童），但仍保留"拖拽到目标区"的视觉隐喻
 */
class DragDropGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameDragBinding
    private lateinit var prefs: AppPreferences
    private lateinit var audio: AudioManager
    private lateinit var targetColor: ColorItem
    private var score: Int = 0
    private var round: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameDragBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences.getInstance(this)
        audio = AudioManager.getInstance(this)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        newRound()
    }

    private fun newRound() {
        round++
        val colors = ColorItem.getForAge(prefs.getAgeGroup())
        targetColor = colors[Random.nextInt(colors.size)]

        binding.tvScore.text = "得分：$score  第 $round 题"
        binding.tvTargetName.text = getString(targetColor.nameResId)
        binding.tvTargetEmoji.text = targetColor.emoji
        binding.targetArea.setBackgroundColor(
            androidx.core.content.ContextCompat.getColor(this, targetColor.colorResId)
        )

        audio.speakChinese("请点击下面的${getString(targetColor.nameResId)}")

        // 选项：显示4个颜色按钮
        val options = mutableSetOf<ColorItem>()
        options.add(targetColor)
        while (options.size < 4) {
            options.add(colors[Random.nextInt(colors.size)])
        }
        val shuffled = options.toList().shuffled()

        binding.optionContainer.removeAllViews()
        val pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse)

        shuffled.forEach { color ->
            val btn = Button(this).apply {
                text = color.emoji
                textSize = 48f
                textStyle = android.graphics.Typeface.BOLD
                minHeight = resources.getDimensionPixelSize(R.dimen.button_large_height)
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    0,
                    resources.getDimensionPixelSize(R.dimen.button_large_height),
                    1f
                ).apply {
                    setMargins(
                        resources.getDimensionPixelSize(R.dimen.spacing_small),
                        resources.getDimensionPixelSize(R.dimen.spacing_small),
                        resources.getDimensionPixelSize(R.dimen.spacing_small),
                        resources.getDimensionPixelSize(R.dimen.spacing_small)
                    )
                }
                setBackgroundResource(R.drawable.bg_learn_card)
                setTextColor(resources.getColor(R.color.text_primary, null))
                backgroundTintList = androidx.core.content.ContextCompat.getColorStateList(
                    context, color.colorResId
                )
                setOnClickListener {
                    it.startAnimation(pulseAnim)
                    onColorSelected(color)
                }
            }
            binding.optionContainer.addView(btn)
        }
    }

    private fun onColorSelected(color: ColorItem) {
        if (color.colorResId == targetColor.colorResId) {
            audio.playSuccessSound()
            score++
            prefs.addStars(2)
            if (score % 3 == 0) {
                AlertDialog.Builder(this)
                    .setTitle("🎉 真棒！")
                    .setMessage("已经答对 $score 题啦！\n⭐ +2")
                    .setPositiveButton("继续") { d, _ ->
                        d.dismiss()
                        newRound()
                    }
                    .show()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("✨ 答对啦！")
                    .setMessage("这就是${getString(targetColor.nameResId)}！\n⭐ +2")
                    .setPositiveButton("下一题") { d, _ ->
                        d.dismiss()
                        newRound()
                    }
                    .show()
            }
        } else {
            audio.playTryAgainSound()
            AlertDialog.Builder(this)
                .setTitle("🤔 再想想")
                .setMessage("这不是${getString(targetColor.nameResId)}，再试试看吧！")
                .setPositiveButton("好的") { d, _ -> d.dismiss() }
                .show()
        }
    }
}
