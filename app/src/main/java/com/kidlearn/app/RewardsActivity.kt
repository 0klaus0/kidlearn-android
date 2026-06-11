package com.kidlearn.app

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kidlearn.app.databinding.ActivityRewardsBinding

/**
 * 奖励/成就展示 Activity
 * 功能：
 * 1. 展示儿童收集的星星数量
 * 2. 展示已解锁贴纸（按星星数阶梯解锁）
 * 3. 展示已解锁成就
 */
class RewardsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRewardsBinding
    private lateinit var prefs: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences.getInstance(this)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        renderStars()
        renderStickers()
        renderAchievements()
    }

    private fun renderStars() {
        val stars = prefs.getStars()
        val total = prefs.getTotalStarsEarned()
        val streak = prefs.getStreakDays()
        binding.tvStarsCount.text = "⭐ 当前星星：$stars"
        binding.tvTotalStars.text = "📚 累计获得：$total"
        binding.tvStreak.text = "🔥 连续学习：$streak 天"

        binding.tvStarsCount.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.bounce)
        )
    }

    private fun renderStickers() {
        binding.linearStickers.removeAllViews()
        val unlocked = prefs.getUnlockedStickers()
        val all = StickerItem.getAll()

        // 标题
        val title = TextView(this).apply {
            text = "🎁 贴纸收藏（${unlocked.size}/${all.size}）"
            textSize = resources.getDimension(R.dimen.text_large) / resources.displayMetrics.density
            setTextColor(resources.getColor(R.color.text_primary, null))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, resources.getDimensionPixelSize(R.dimen.spacing_medium),
                0, resources.getDimensionPixelSize(R.dimen.spacing_small))
        }
        binding.linearStickers.addView(title)

        // 每行显示 3 个贴纸
        val rowCount = (all.size + 2) / 3
        for (row in 0 until rowCount) {
            val rowLayout = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 0, 0, resources.getDimensionPixelSize(R.dimen.spacing_small))
            }
            for (col in 0..2) {
                val idx = row * 3 + col
                if (idx < all.size) {
                    val sticker = all[idx]
                    val isUnlocked = unlocked.contains(sticker.id)
                    val stickerView = TextView(this).apply {
                        text = if (isUnlocked) sticker.emoji else "❓"
                        textSize = 48f
                        gravity = android.view.Gravity.CENTER
                        layoutParams = android.widget.LinearLayout.LayoutParams(
                            0,
                            resources.getDimensionPixelSize(R.dimen.sticker_size),
                            1f
                        )
                        setBackgroundResource(R.drawable.bg_card)
                        if (!isUnlocked) {
                            alpha = 0.4f
                        }
                    }
                    rowLayout.addView(stickerView)
                }
            }
            binding.linearStickers.addView(rowLayout)

            // 贴纸说明行
            val descRow = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            for (col in 0..2) {
                val idx = row * 3 + col
                if (idx < all.size) {
                    val sticker = all[idx]
                    val descView = TextView(this).apply {
                        text = "${sticker.requiredStars}⭐"
                        textSize = 14f
                        gravity = android.view.Gravity.CENTER
                        layoutParams = android.widget.LinearLayout.LayoutParams(
                            0,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        )
                        setTextColor(resources.getColor(R.color.text_secondary, null))
                    }
                    descRow.addView(descView)
                }
            }
            binding.linearStickers.addView(descRow)
        }
    }

    private fun renderAchievements() {
        binding.linearAchievements.removeAllViews()
        val unlocked = prefs.getUnlockedAchievements()
        val all = AchievementItem.getAll()

        val title = TextView(this).apply {
            text = "🏆 成就（${unlocked.size}/${all.size}）"
            textSize = resources.getDimension(R.dimen.text_large) / resources.displayMetrics.density
            setTextColor(resources.getColor(R.color.text_primary, null))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, resources.getDimensionPixelSize(R.dimen.spacing_large),
                0, resources.getDimensionPixelSize(R.dimen.spacing_small))
        }
        binding.linearAchievements.addView(title)

        all.forEach { ach ->
            val isUnlocked = unlocked.contains(ach.id)
            val text = "${if (isUnlocked) ach.emoji else "🔒"} ${getString(ach.nameResId)}"
            val itemView = TextView(this).apply {
                this.text = text
                textSize = resources.getDimension(R.dimen.text_medium) / resources.displayMetrics.density
                setPadding(
                    resources.getDimensionPixelSize(R.dimen.spacing_medium),
                    resources.getDimensionPixelSize(R.dimen.spacing_medium),
                    resources.getDimensionPixelSize(R.dimen.spacing_medium),
                    resources.getDimensionPixelSize(R.dimen.spacing_medium)
                )
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(
                        0, 0, 0,
                        resources.getDimensionPixelSize(R.dimen.spacing_small)
                    )
                }
                setBackgroundResource(R.drawable.bg_card)
                if (!isUnlocked) {
                    alpha = 0.5f
                }
            }
            binding.linearAchievements.addView(itemView)
        }
    }
}
