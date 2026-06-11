package com.kidlearn.app.games

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kidlearn.app.AppPreferences
import com.kidlearn.app.AudioManager
import com.kidlearn.app.MainMenuActivity
import com.kidlearn.app.NumberItem
import com.kidlearn.app.databinding.ActivityGameMatchingBinding
import kotlin.random.Random

/**
 * 配对游戏：显示数字和对应数量的物品，让儿童点击相同数量的数字配对
 * 简化实现：显示左侧数字，右侧3个候选数字/数量，让儿童选择正确的
 */
class MatchingGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameMatchingBinding
    private lateinit var prefs: AppPreferences
    private lateinit var audio: AudioManager
    private var correctAnswer: Int = 0
    private var score: Int = 0
    private var round: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameMatchingBinding.inflate(layoutInflater)
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
        val numbers = NumberItem.getForAge(prefs.getAgeGroup())
        val target = numbers[Random.nextInt(numbers.size)]
        correctAnswer = target.number
        binding.tvScore.text = "得分：$score  第 $round 题"

        // 题目：显示 emoji 数量示例作为题干
        val countEmoji = when (target.number) {
            0 -> "❌（空）"
            1 -> "🍎"
            2 -> "🍎🍎"
            3 -> "🍎🍎🍎"
            4 -> "🍎🍎🍎🍎"
            5 -> "🍎🍎🍎🍎🍎"
            6 -> "🍎🍎🍎🍎🍎🍎"
            7 -> "🍎🍎🍎🍎🍎🍎🍎"
            8 -> "🍎🍎🍎🍎🍎🍎🍎🍎"
            9 -> "🍎🍎🍎🍎🍎🍎🍎🍎🍎"
            10 -> "🍎🍎🍎🍎🍎🍎🍎🍎🍎🍎"
            else -> "❓"
        }
        binding.tvQuestion.text = "数一数：这里有几个苹果？\n\n$countEmoji"
        audio.speakChinese("数一数，这里有几个苹果？")

        // 生成选项（包含正确答案和2个干扰项）
        val options = mutableSetOf<Int>()
        options.add(correctAnswer)
        while (options.size < 3) {
            val candidate = numbers[Random.nextInt(numbers.size)].number
            options.add(candidate)
        }
        val shuffled = options.toList().shuffled()

        binding.optionContainer.removeAllViews()
        val pulseAnim = AnimationUtils.loadAnimation(
            this, com.kidlearn.app.R.anim.pulse
        )
        val colors = listOf(
            com.kidlearn.app.R.color.red,
            com.kidlearn.app.R.color.yellow,
            com.kidlearn.app.R.color.green
        )

        shuffled.forEachIndexed { index, value ->
            val btn = Button(this).apply {
                text = value.toString()
                textSize = 48f
                textStyle = android.graphics.Typeface.BOLD
                minHeight = resources.getDimensionPixelSize(com.kidlearn.app.R.dimen.button_large_height)
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    0,
                    resources.getDimensionPixelSize(com.kidlearn.app.R.dimen.button_large_height),
                    1f
                ).apply {
                    setMargins(
                        resources.getDimensionPixelSize(com.kidlearn.app.R.dimen.spacing_small),
                        resources.getDimensionPixelSize(com.kidlearn.app.R.dimen.spacing_small),
                        resources.getDimensionPixelSize(com.kidlearn.app.R.dimen.spacing_small),
                        resources.getDimensionPixelSize(com.kidlearn.app.R.dimen.spacing_small)
                    )
                }
                setBackgroundResource(com.kidlearn.app.R.drawable.bg_learn_card)
                setTextColor(resources.getColor(com.kidlearn.app.R.color.text_primary, null))
                backgroundTintList = androidx.core.content.ContextCompat.getColorStateList(
                    context, colors[index % colors.size]
                )
                setOnClickListener {
                    it.startAnimation(pulseAnim)
                    onOptionSelected(value)
                }
            }
            binding.optionContainer.addView(btn)
        }
    }

    private fun onOptionSelected(value: Int) {
        if (value == correctAnswer) {
            audio.playSuccessSound()
            score++
            prefs.addStars(2)

            if (score % 3 == 0) {
                // 每答对3题给予一次大鼓励
                AlertDialog.Builder(this)
                    .setTitle("🎉 太棒啦！")
                    .setMessage("你已经答对 $score 题啦！继续加油！\n⭐ +2")
                    .setPositiveButton("继续") { dialog, _ ->
                        dialog.dismiss()
                        newRound()
                    }
                    .show()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("✨ 答对啦！")
                    .setMessage("这是 $correctAnswer 个苹果！\n⭐ +2")
                    .setPositiveButton("下一题") { dialog, _ ->
                        dialog.dismiss()
                        newRound()
                    }
                    .show()
            }
        } else {
            audio.playTryAgainSound()
            AlertDialog.Builder(this)
                .setTitle("🤔 再想想")
                .setMessage("答案不是 $value，再试试看吧！")
                .setPositiveButton("好的") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }
}
