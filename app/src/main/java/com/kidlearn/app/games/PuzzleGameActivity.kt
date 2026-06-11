package com.kidlearn.app.games

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.GridLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kidlearn.app.AppPreferences
import com.kidlearn.app.AudioManager
import com.kidlearn.app.MainMenuActivity
import com.kidlearn.app.R
import com.kidlearn.app.databinding.ActivityGamePuzzleBinding
import kotlin.random.Random

/**
 * 拼图游戏（简化版：数字顺序排列）：
 * 显示 1,2,3,4,5 打乱的数字，让儿童按从小到大的顺序点击
 * 每次点击正确则锁定该格子，全部完成后胜利
 */
class PuzzleGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGamePuzzleBinding
    private lateinit var prefs: AppPreferences
    private lateinit var audio: AudioManager
    private var score: Int = 0
    private var puzzleRound: Int = 0
    private var nextExpected: Int = 1
    private var maxNum: Int = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamePuzzleBinding.inflate(layoutInflater)
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
        puzzleRound++
        nextExpected = 1
        // 根据年龄调整难度
        maxNum = when (prefs.getAgeGroup()) {
            com.kidlearn.app.AgeGroup.AGE_2_3 -> 4
            com.kidlearn.app.AgeGroup.AGE_4_5 -> 5
            com.kidlearn.app.AgeGroup.AGE_5_6 -> 6
        }

        binding.tvScore.text = "得分：$score  第 $puzzleRound 关"
        binding.tvInstruction.text = "请按从小到大的顺序点击数字 1 到 $maxNum"
        audio.speakChinese("请按从小到大的顺序点击数字 1 到 $maxNum")

        // 生成打乱的数字
        val numbers = (1..maxNum).toList().shuffled()

        binding.puzzleGrid.removeAllViews()
        binding.puzzleGrid.columnCount = 3
        val pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse)

        val colors = listOf(
            R.color.red, R.color.orange, R.color.yellow, R.color.green,
            R.color.cyan, R.color.blue, R.color.purple, R.color.pink
        )

        numbers.forEachIndexed { index, num ->
            val btn = Button(this).apply {
                text = num.toString()
                textSize = 36f
                textStyle = android.graphics.Typeface.BOLD
                tag = num  // 用 tag 存储数字
                setPadding(0, 0, 0, 0)
                minHeight = resources.getDimensionPixelSize(R.dimen.button_large_height)
                val size = resources.getDimensionPixelSize(R.dimen.button_large_height)
                layoutParams = GridLayout.LayoutParams().apply {
                    width = size
                    height = size
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
                    context, colors[index % colors.size]
                )
                setOnClickListener { view ->
                    view.startAnimation(pulseAnim)
                    onNumberClicked(view as Button, num)
                }
            }
            binding.puzzleGrid.addView(btn)
        }
    }

    private fun onNumberClicked(btn: Button, num: Int) {
        if (num == nextExpected) {
            // 正确：锁定该按钮
            audio.speakChinese("$num")
            btn.isEnabled = false
            btn.alpha = 0.4f
            btn.text = "✓$num"
            nextExpected++

            if (nextExpected > maxNum) {
                // 完成本轮
                audio.playSuccessSound()
                score++
                prefs.addStars(3)

                AlertDialog.Builder(this)
                    .setTitle("🎉 完成啦！")
                    .setMessage("你成功完成了数字拼图！\n⭐ +3")
                    .setPositiveButton("下一关") { d, _ ->
                        d.dismiss()
                        newRound()
                    }
                    .show()
            }
        } else {
            audio.playTryAgainSound()
            AlertDialog.Builder(this)
                .setTitle("🤔 不是这个")
                .setMessage("请先点击数字 $nextExpected 哦！")
                .setPositiveButton("好的") { d, _ -> d.dismiss() }
                .show()
        }
    }
}
