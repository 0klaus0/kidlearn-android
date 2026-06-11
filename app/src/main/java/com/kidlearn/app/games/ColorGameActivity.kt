package com.kidlearn.app.games

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kidlearn.app.AppPreferences
import com.kidlearn.app.AudioManager
import com.kidlearn.app.MainMenuActivity
import com.kidlearn.app.R
import com.kidlearn.app.databinding.ActivityGameColorBinding
import kotlin.random.Random

/**
 * 涂色游戏（简化版）：
 * 让儿童从8种颜色中选择一种，然后为目标图形上色（通过点击切换颜色）
 * 每次完成一次"涂色"后得星，并可以开始新的图形
 */
class ColorGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameColorBinding
    private lateinit var prefs: AppPreferences
    private lateinit var audio: AudioManager
    private var currentColor: Int = Color.RED
    private var score: Int = 0
    private var colorRound: Int = 0
    private var selectedShapeIndex: Int = 0
    private val shapes = listOf("⭐", "❤️", "🔷", "⚪", "🎈", "🌸")

    private val androidColors = listOf(
        Color.parseColor("#E57373"), // red
        Color.parseColor("#FFB74D"), // orange
        Color.parseColor("#FFF176"), // yellow
        Color.parseColor("#81C784"), // green
        Color.parseColor("#4DD0E1"), // cyan
        Color.parseColor("#64B5F6"), // blue
        Color.parseColor("#BA68C8"), // purple
        Color.parseColor("#F48FB1")  // pink
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameColorBinding.inflate(layoutInflater)
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

        setupColorPalette()
        setupShapeButtons()
        newRound()
    }

    private fun setupColorPalette() {
        val pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse)
        binding.colorPalette.removeAllViews()
        androidColors.forEachIndexed { index, color ->
            val btn = Button(this).apply {
                text = "🎨"
                textSize = 24f
                setBackgroundColor(color)
                setPadding(0, 0, 0, 0)
                minHeight = 0
                minWidth = 0
                layoutParams = LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.button_min_width),
                    resources.getDimensionPixelSize(R.dimen.button_min_height)
                ).apply {
                    setMargins(
                        resources.getDimensionPixelSize(R.dimen.spacing_small),
                        0,
                        resources.getDimensionPixelSize(R.dimen.spacing_small),
                        0
                    )
                }
                setOnClickListener {
                    it.startAnimation(pulseAnim)
                    currentColor = color
                    binding.tvCurrentColor.setBackgroundColor(color)
                    audio.speakChinese("选择了颜色")
                }
            }
            binding.colorPalette.addView(btn)
        }
        // 设置默认颜色
        currentColor = androidColors[0]
        binding.tvCurrentColor.setBackgroundColor(currentColor)
    }

    private fun setupShapeButtons() {
        binding.shapeContainer.removeAllViews()
        val pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse)

        shapes.forEachIndexed { index, shape ->
            val shapeLayout = FrameLayout(this).apply {
                layoutParams = FrameLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.menu_card_size),
                    resources.getDimensionPixelSize(R.dimen.menu_card_size)
                ).apply {
                    setMargins(
                        resources.getDimensionPixelSize(R.dimen.spacing_small),
                        resources.getDimensionPixelSize(R.dimen.spacing_small),
                        resources.getDimensionPixelSize(R.dimen.spacing_small),
                        resources.getDimensionPixelSize(R.dimen.spacing_small)
                    )
                }
                setBackgroundResource(R.drawable.bg_card)
                tag = 0 // 用 tag 存储当前颜色索引：0 = 未涂色
            }

            val textView = TextView(this).apply {
                text = shape
                textSize = 72f
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                gravity = Gravity.CENTER
            }

            shapeLayout.addView(textView)
            shapeLayout.setOnClickListener {
                it.startAnimation(pulseAnim)
                val currentTag = it.tag as Int
                // 第1次点击：用当前颜色为图形"上色"
                if (currentTag == 0) {
                    it.setBackgroundColor(currentColor)
                    it.tag = 1
                    audio.playSuccessSound()
                    score++
                    prefs.addStars(2)
                    colorRound++
                    binding.tvScore.text = "得分：$score  已涂色：$colorRound"

                    if (score % 4 == 0) {
                        AlertDialog.Builder(this)
                            .setTitle("🎨 太棒啦！")
                            .setMessage("你已经涂了 $score 个颜色啦！\n⭐ +2")
                            .setPositiveButton("继续涂") { d, _ -> d.dismiss() }
                            .show()
                    }
                } else {
                    // 再次点击则清除颜色重涂
                    it.setBackgroundResource(R.drawable.bg_card)
                    it.tag = 0
                }
            }
            binding.shapeContainer.addView(shapeLayout)
        }
    }

    private fun newRound() {
        // 每一轮显示新图形提示
        binding.tvInstruction.text = "选择一种颜色，然后点击图形为它上色吧！"
        audio.speakChinese("选择颜色，然后点图形上色")
    }
}
