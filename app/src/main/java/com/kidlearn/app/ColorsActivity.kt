package com.kidlearn.app

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kidlearn.app.databinding.ActivityLearningBinding

/**
 * 颜色学习 Activity
 * 展示8种基础颜色，让儿童点击认识
 */
class ColorsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLearningBinding
    private lateinit var prefs: AppPreferences
    private lateinit var audio: AudioManager
    private lateinit var colors: List<ColorItem>
    private var tasksCompleted: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences.getInstance(this)
        audio = AudioManager.getInstance(this)

        colors = ColorItem.getForAge(prefs.getAgeGroup())
        binding.tvTitle.text = "${getString(R.string.title_colors)}（${colors.size}个）"

        setupNavigation()
        renderColors()
    }

    private fun setupNavigation() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun renderColors() {
        binding.gridLayout.removeAllViews()
        val pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse)

        colors.forEach { item ->
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
                backgroundTintList = androidx.core.content.ContextCompat.getColorStateList(
                    context, item.colorResId
                )
                setOnClickListener {
                    it.startAnimation(pulseAnim)
                    onColorClicked(item)
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

    private fun onColorClicked(item: ColorItem) {
        val colorName = getString(item.nameResId)
        audio.speakChinese("这是$colorName")
        AlertDialog.Builder(this)
            .setTitle("${item.emoji} $colorName")
            .setMessage("这是美丽的$colorName")
            .setPositiveButton("我知道啦") { dialog, _ -> dialog.dismiss() }
            .show()

        prefs.incrementColorsCompleted()
        prefs.addStars(1)
        tasksCompleted++

        if (tasksCompleted % 3 == 0) {
            showCelebration()
        }
    }

    private fun showCelebration() {
        audio.playSuccessSound()
        AlertDialog.Builder(this)
            .setTitle("🎨 ${getString(R.string.excellent)}")
            .setMessage("你认识了好多颜色啦！\n⭐ +1")
            .setPositiveButton("继续学习") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
