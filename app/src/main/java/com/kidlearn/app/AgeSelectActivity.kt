package com.kidlearn.app

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.kidlearn.app.databinding.ActivityAgeSelectBinding

/**
 * 年龄选择入口 Activity
 * 路径：启动 -> 年龄选择 -> 年龄分组确认 -> 主菜单
 *
 * 功能：
 * 1. 让家长/用户选择儿童的年龄段（2-3/4-5/5-6岁）
 * 2. 根据年龄段自动适配学习内容难度
 * 3. 已选过则直接进入主菜单
 */
class AgeSelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgeSelectBinding
    private lateinit var prefs: AppPreferences
    private var selectedAge: AgeGroup = AgeGroup.AGE_2_3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = AppPreferences.getInstance(this)

        // 若已设置年龄且时间未超限，直接进入主菜单
        if (prefs.isAgeSet()) {
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityAgeSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        // 默认选中第一个年龄段
        updateSelection(AgeGroup.AGE_2_3)

        val pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse)

        binding.btnAge23.setOnClickListener {
            it.startAnimation(pulseAnim)
            selectedAge = AgeGroup.AGE_2_3
            updateSelection(selectedAge)
        }
        binding.btnAge45.setOnClickListener {
            it.startAnimation(pulseAnim)
            selectedAge = AgeGroup.AGE_4_5
            updateSelection(selectedAge)
        }
        binding.btnAge56.setOnClickListener {
            it.startAnimation(pulseAnim)
            selectedAge = AgeGroup.AGE_5_6
            updateSelection(selectedAge)
        }

        binding.btnConfirm.setOnClickListener {
            it.startAnimation(pulseAnim)
            prefs.setAgeGroup(selectedAge)
            val intent = Intent(this, MainMenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun updateSelection(age: AgeGroup) {
        // 重置所有按钮背景
        binding.btnAge23.setBackgroundResource(R.drawable.bg_age_button)
        binding.btnAge45.setBackgroundResource(R.drawable.bg_age_button)
        binding.btnAge56.setBackgroundResource(R.drawable.bg_age_button)

        // 高亮选中按钮
        when (age) {
            AgeGroup.AGE_2_3 -> binding.btnAge23.setBackgroundResource(R.drawable.bg_age_button_selected)
            AgeGroup.AGE_4_5 -> binding.btnAge45.setBackgroundResource(R.drawable.bg_age_button_selected)
            AgeGroup.AGE_5_6 -> binding.btnAge56.setBackgroundResource(R.drawable.bg_age_button_selected)
        }
    }
}
