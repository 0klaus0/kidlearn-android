package com.kidlearn.app.games

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kidlearn.app.AppPreferences
import com.kidlearn.app.AudioManager
import com.kidlearn.app.LetterItem
import com.kidlearn.app.MainMenuActivity
import com.kidlearn.app.R
import com.kidlearn.app.databinding.ActivityGameSoundBinding
import kotlin.random.Random

/**
 * 声音识别游戏：
 * 点击喇叭播放字母发音，然后让儿童在3个选项中选择正确的字母
 */
class SoundGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameSoundBinding
    private lateinit var prefs: AppPreferences
    private lateinit var audio: AudioManager
    private lateinit var targetLetter: LetterItem
    private var score: Int = 0
    private var soundRound: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameSoundBinding.inflate(layoutInflater)
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

        binding.btnPlaySound.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse))
            playTargetSound()
        }

        newRound()
    }

    private fun newRound() {
        soundRound++
        val letters = LetterItem.getForAge(prefs.getAgeGroup())
        targetLetter = letters[Random.nextInt(letters.size)]

        binding.tvScore.text = "得分：$score  第 $soundRound 题"
        audio.speakEnglish("${targetLetter.letter}")

        // 生成选项
        val options = mutableSetOf<LetterItem>()
        options.add(targetLetter)
        while (options.size < 3) {
            options.add(letters[Random.nextInt(letters.size)])
        }
        val shuffled = options.toList().shuffled()

        binding.optionContainer.removeAllViews()
        val pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse)
        val colors = listOf(R.color.red, R.color.yellow, R.color.green)

        shuffled.forEachIndexed { index, letter ->
            val btn = Button(this).apply {
                text = "${letter.letter}\n${letter.emoji}"
                textSize = 28f
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
                    context, colors[index % colors.size]
                )
                setOnClickListener {
                    it.startAnimation(pulseAnim)
                    onLetterSelected(letter)
                }
            }
            binding.optionContainer.addView(btn)
        }
    }

    private fun playTargetSound() {
        audio.speakEnglish("${targetLetter.letter}. ${targetLetter.word}")
    }

    private fun onLetterSelected(letter: LetterItem) {
        if (letter.letter == targetLetter.letter) {
            audio.playSuccessSound()
            score++
            prefs.addStars(2)

            if (score % 3 == 0) {
                AlertDialog.Builder(this)
                    .setTitle("🎉 真棒！")
                    .setMessage("你已经答对 $score 题啦！\n⭐ +2")
                    .setPositiveButton("继续") { d, _ ->
                        d.dismiss()
                        newRound()
                    }
                    .show()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("✨ 答对啦！")
                    .setMessage("这是字母 ${targetLetter.letter}，${targetLetter.word}！\n⭐ +2")
                    .setPositiveButton("下一题") { d, _ ->
                        d.dismiss()
                        newRound()
                    }
                    .show()
            }
        } else {
            audio.playTryAgainSound()
            AlertDialog.Builder(this)
                .setTitle("🤔 再听听看")
                .setMessage("不是这个字母，再点一次喇叭听听看吧！")
                .setPositiveButton("好的") { d, _ -> d.dismiss() }
                .show()
        }
    }
}
