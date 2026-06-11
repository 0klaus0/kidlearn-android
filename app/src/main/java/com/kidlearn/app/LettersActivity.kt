package com.kidlearn.app

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kidlearn.app.databinding.ActivityLearningBinding

/**
 * 字母学习 Activity - 展示 A-Z 英文字母
 * 点击字母时朗读英文单词并展示配图 emoji
 */
class LettersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLearningBinding
    private lateinit var prefs: AppPreferences
    private lateinit var audio: AudioManager
    private lateinit var letters: List<LetterItem>
    private var tasksCompleted: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences.getInstance(this)
        audio = AudioManager.getInstance(this)

        letters = LetterItem.getForAge(prefs.getAgeGroup())
        binding.tvTitle.text = "${getString(R.string.title_letters)}（${letters.size}个）"

        setupNavigation()
        renderLetters()
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

    private fun renderLetters() {
        binding.gridLayout.removeAllViews()
        val pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse)
        val colors = listOf(
            R.color.red, R.color.orange, R.color.yellow, R.color.green,
            R.color.cyan, R.color.blue, R.color.purple, R.color.pink
        )

        letters.forEachIndexed { index, item ->
            val btn = Button(this).apply {
                text = item.letter.toString()
                textSize = 40f
                textStyle = android.graphics.Typeface.BOLD
                setPadding(0, resources.getDimensionPixelSize(R.dimen.spacing_medium),
                    0, resources.getDimensionPixelSize(R.dimen.spacing_medium))
                minHeight = resources.getDimensionPixelSize(R.dimen.learn_card_size)
                minWidth = resources.getDimensionPixelSize(R.dimen.learn_card_size)
                setBackgroundResource(R.drawable.bg_learn_card)
                setTextColor(resources.getColor(R.color.text_primary, null))
                backgroundTintList = androidx.core.content.ContextCompat.getColorStateList(
                    context, colors[index % colors.size]
                )
                setOnClickListener {
                    it.startAnimation(pulseAnim)
                    onLetterClicked(item)
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

    private fun onLetterClicked(item: LetterItem) {
        // 先朗读字母，再朗读单词（英文）
        audio.speakEnglish("${item.letter}. ${item.word}")

        AlertDialog.Builder(this)
            .setTitle("字母 ${item.letter}")
            .setMessage("${item.emoji} ${item.word}")
            .setPositiveButton("我知道啦") { dialog, _ -> dialog.dismiss() }
            .show()

        prefs.incrementLettersCompleted()
        prefs.addStars(1)
        tasksCompleted++

        if (tasksCompleted % 3 == 0) {
            showCelebration()
        }
    }

    private fun showCelebration() {
        audio.playSuccessSound()
        AlertDialog.Builder(this)
            .setTitle("🎉 ${getString(R.string.great_job)}")
            .setMessage("你已经学会了很多字母啦！继续加油！\n⭐ +1")
            .setPositiveButton("继续学习") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
