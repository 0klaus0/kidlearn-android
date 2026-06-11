package com.kidlearn.app

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.kidlearn.app.databinding.ActivityGamesBinding
import com.kidlearn.app.games.*

/**
 * 游戏选择菜单 Activity
 * 让儿童选择5种互动游戏：配对、拖拽、拼图、声音识别、涂色
 */
class GamesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGamesBinding
    private lateinit var prefs: AppPreferences
    private lateinit var audio: AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences.getInstance(this)
        audio = AudioManager.getInstance(this)
        prefs.incrementGamesPlayed()

        setupUI()
    }

    private fun setupUI() {
        binding.tvTitle.text = getString(R.string.title_games)
        val pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        binding.btnMatching.text = "${GameType.MATCHING.emoji}\n${GameType.MATCHING.displayName}"
        binding.btnDrag.text = "${GameType.DRAGDROP.emoji}\n${GameType.DRAGDROP.displayName}"
        binding.btnPuzzle.text = "${GameType.PUZZLE.emoji}\n${GameType.PUZZLE.displayName}"
        binding.btnSound.text = "${GameType.SOUND.emoji}\n${GameType.SOUND.displayName}"
        binding.btnColor.text = "${GameType.COLORING.emoji}\n${GameType.COLORING.displayName}"

        binding.btnMatching.setOnClickListener {
            it.startAnimation(pulseAnim)
            startActivity(Intent(this, MatchingGameActivity::class.java))
        }
        binding.btnDrag.setOnClickListener {
            it.startAnimation(pulseAnim)
            startActivity(Intent(this, DragDropGameActivity::class.java))
        }
        binding.btnPuzzle.setOnClickListener {
            it.startAnimation(pulseAnim)
            startActivity(Intent(this, PuzzleGameActivity::class.java))
        }
        binding.btnSound.setOnClickListener {
            it.startAnimation(pulseAnim)
            startActivity(Intent(this, SoundGameActivity::class.java))
        }
        binding.btnColor.setOnClickListener {
            it.startAnimation(pulseAnim)
            startActivity(Intent(this, ColorGameActivity::class.java))
        }
    }
}
