package com.kidlearn.app

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kidlearn.app.databinding.ActivityLearningBinding

/**
 * 形状学习 Activity
 * 展示6种基础形状（圆、方、三角、长方形、星形、心形）
 */
class ShapesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLearningBinding
    private lateinit var prefs: AppPreferences
    private lateinit var audio: AudioManager
    private lateinit var shapes: List<ShapeItem>
    private var tasksCompleted: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences.getInstance(this)
        audio = AudioManager.getInstance(this)

        shapes = ShapeItem.getForAge(prefs.getAgeGroup())
        binding.tvTitle.text = "${getString(R.string.title_shapes)}（${shapes.size}个）"

        setupNavigation()
        renderShapes()
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

    private fun renderShapes() {
        binding.gridLayout.removeAllViews()
        val pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse)
        val colors = listOf(
            R.color.red, R.color.orange, R.color.yellow, R.color.green,
            R.color.cyan, R.color.blue, R.color.purple, R.color.pink
        )

        shapes.forEachIndexed { index, item ->
            val btn = Button(this).apply {
                text = item.emoji
                textSize = 48f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
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
                    onShapeClicked(item)
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

    private fun onShapeClicked(item: ShapeItem) {
        val shapeName = getString(item.nameResId)
        audio.speakChinese("这是$shapeName")
        AlertDialog.Builder(this)
            .setTitle("${item.emoji} $shapeName")
            .setMessage("这是$shapeName，是不是很漂亮呀？")
            .setPositiveButton("我知道啦") { dialog, _ -> dialog.dismiss() }
            .show()

        prefs.incrementShapesCompleted()
        prefs.addStars(1)
        tasksCompleted++

        if (tasksCompleted % 3 == 0) {
            showCelebration()
        }
    }

    private fun showCelebration() {
        audio.playSuccessSound()
        AlertDialog.Builder(this)
            .setTitle("⭐ ${getString(R.string.wonderful)}")
            .setMessage("你认识了好多形状啦！\n⭐ +1")
            .setPositiveButton("继续学习") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
