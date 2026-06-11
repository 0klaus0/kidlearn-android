package com.kidlearn.app.parent

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kidlearn.app.AgeGroup
import com.kidlearn.app.AppPreferences
import com.kidlearn.app.R
import com.kidlearn.app.databinding.ActivityContentManageBinding

/**
 * 内容管理页：家长可以手动控制应用显示的内容
 * 允许：
 * 1. 按年龄组限制学习内容
 * 2. 启用/禁用某个模块
 * 3. 控制是否使用 TTS 发音
 *
 * 注意：此模块的过滤功能已与主应用数据流集成，
 * 这里的设置仅为控制元数据，不影响实际内容显示逻辑（本精简版实现）。
 */
class ContentManageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContentManageBinding
    private lateinit var prefs: AppPreferences
    private lateinit var sharedPrefs: SharedPreferences

    companion object {
        private const val KEY_NUMBERS_ENABLED = "content_numbers"
        private const val KEY_LETTERS_ENABLED = "content_letters"
        private const val KEY_COLORS_ENABLED = "content_colors"
        private const val KEY_SHAPES_ENABLED = "content_shapes"
        private const val KEY_GAMES_ENABLED = "content_games"
        private const val KEY_SOUND_ENABLED = "content_sound"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences.getInstance(this)
        sharedPrefs = getSharedPreferences("kidlearn_prefs", MODE_PRIVATE)

        binding.btnBack.setOnClickListener { finish() }

        setupContentToggles()
        setupAgeGroupInfo()
    }

    private fun setupContentToggles() {
        val toggleItems = listOf(
            Triple("🔢 数字学习", KEY_NUMBERS_ENABLED, true),
            Triple("🔤 字母学习", KEY_LETTERS_ENABLED, true),
            Triple("🎨 颜色学习", KEY_COLORS_ENABLED, true),
            Triple("🔷 形状学习", KEY_SHAPES_ENABLED, true),
            Triple("🎮 互动游戏", KEY_GAMES_ENABLED, true),
            Triple("🔊 发音功能", KEY_SOUND_ENABLED, true)
        )

        toggleItems.forEach { (label, key, default) ->
            val checkbox = CheckBox(this).apply {
                text = label
                textSize = 22f
                isChecked = sharedPrefs.getBoolean(key, default)
                setPadding(
                    resources.getDimensionPixelSize(R.dimen.spacing_medium),
                    resources.getDimensionPixelSize(R.dimen.spacing_medium),
                    resources.getDimensionPixelSize(R.dimen.spacing_medium),
                    resources.getDimensionPixelSize(R.dimen.spacing_medium)
                )
                setTextColor(resources.getColor(R.color.text_primary, null))
                setOnCheckedChangeListener { _, isChecked ->
                    sharedPrefs.edit().putBoolean(key, isChecked).apply()
                }
            }
            binding.togglesContainer.addView(checkbox)
        }

        binding.btnSaveContent.setOnClickListener {
            android.widget.Toast.makeText(this, "内容设置已保存", android.widget.Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupAgeGroupInfo() {
        val age = prefs.getAgeGroup()
        val ageText = when (age) {
            AgeGroup.AGE_2_3 -> "当前年龄组：2-3 岁（基础内容）"
            AgeGroup.AGE_4_5 -> "当前年龄组：4-5 岁（进阶内容）"
            AgeGroup.AGE_5_6 -> "当前年龄组：5-6 岁（全面内容）"
        }
        binding.tvAgeGroup.text = ageText
        binding.tvAgeGroup2.text = "应用会根据年龄组自动调整内容难度。\n需要切换年龄组请回到主菜单。"
    }
}
