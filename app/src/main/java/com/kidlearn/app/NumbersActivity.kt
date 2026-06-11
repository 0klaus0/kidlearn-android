package com.kidlearn.app

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kidlearn.app.databinding.ActivityLearningBinding

/**
 * 数字学习 Activity
 *
 * 显示 0-10 的数字列表，点击数字会：
 * 1. 播放标准中文发音
 * 2. 显示物品数量示例（用 emoji 表示）
 * 3. 给予动画反馈并奖励星星
 */
class NumbersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLearningBinding
    private lateinit var prefs: AppPreferences
    private lateinit var audio: AudioManager
    private lateinit var numbers: List<NumberItem>
    private var currentIndex: Int = 0
    private var tasksCompleted: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences.getInstance(this)
        audio = AudioManager.getInstance(this)

        numbers = NumberItem.getForAge(prefs.getAgeGroup())
        binding.tvTitle.text = getString(R.string.title_numbers) + "（${numbers.size}个）"

        setupNavigation()
        renderCards()
    }

    private fun setupNavigation() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun renderCards() {
        binding.gridLayout.removeAllViews()
        val pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse)

        numbers.forEach { item ->
            val btn = Button(this).apply {
                text = item.emoji
                textSize = 48f
                textStyle = android.graphics.Typeface.BOLD
                setPadding(0, resources.getDimensionPixelSize(R.dimen.spacing_medium),
                    0, resources.getDimensionPixelSize(R.dimen.spacing_medium))
                minHeight = resources.getDimensionPixelSize(R.dimen.learn_card_size)
                minWidth = resources.getDimensionPixelSize(R.dimen.learn_card_size)
                setBackgroundResource(R.drawable.bg_learn_card)
                setTextColor(resources.getColor(R.color.text_primary, null))

                val colors = listOf(
                    R.color.red, R.color.orange, R.color.yellow, R.color.green,
                    R.color.cyan, R.color.blue, R.color.purple, R.color.pink
                )
                val colorRes = colors[item.number % colors.size]
                backgroundTintList = androidx.core.content.ContextCompat.getColorStateList(
                    context, colorRes
                )

                setOnClickListener {
                    it.startAnimation(pulseAnim)
                    onNumberClicked(item)
                }
            }
            val params = android.widget.GridLayout.LayoutParams().apply {
                width = resources.getDimensionPixelSize(R.dimen.learn_card_size)
                height = resources.getDimensionPixelSize(R.dimen.learn_card_size)
                setMargins(
                    resources.getDimensionPixelSize(R.dimen.spacing_small),
                    resources.getDimensionPixelSize(R.dimen.spacing_small),
                    resources.getDimensionPixelSize(R.dimen.spacing_small),
                    resources.getDimensionPixelSize(R.dimen.spacing_small)
                )
            }
            binding.gridLayout.addView(btn, params)
        }
    }

    private fun onNumberClicked(item: NumberItem) {
        // 1. 发音
        audio.speakChinese("${item.number}")
        // 2. 显示弹窗：大数字 + 数量示例
        val message = "${item.emoji} ${item.countExample}"
        AlertDialog.Builder(this)
            .setTitle("数字 ${item.number}")
            .setMessage(message)
            .setPositiveButton("我知道啦") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
        // 3. 奖励
        prefs.incrementNumbersCompleted()
        prefs.addStars(1)
        tasksCompleted++

        // 4. 每完成3-5个任务触发鼓励动画
        if (tasksCompleted % 3 == 0) {
            showCelebration()
        }
    }

    private fun showCelebration() {
        audio.playSuccessSound()
        AlertDialog.Builder(this)
            .setTitle("🎉 " + getString(R.string.great_job))
            .setMessage("你真棒！继续加油哦！\n⭐ +1")
            .setPositiveButton("继续学习") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
