package com.kidlearn.app.parent

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kidlearn.app.AppPreferences
import com.kidlearn.app.MainMenuActivity
import com.kidlearn.app.R
import com.kidlearn.app.databinding.ActivityParentLoginBinding

/**
 * 家长控制登录页：验证家长密码后进入控制面板
 * 默认密码为 1234（首次进入时提示修改）
 */
class ParentLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityParentLoginBinding
    private lateinit var prefs: AppPreferences
    private var inputPassword: StringBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPreferences.getInstance(this)

        binding.btnBack.setOnClickListener { finish() }
        setupKeypad()

        // 提示首次使用
        binding.tvHint.text = "请输入家长密码\n(默认密码：1234)"

        binding.btnForgot.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("忘记密码")
                .setMessage("默认密码为 1234。\n如果您已修改过密码，请卸载并重新安装应用以重置。")
                .setPositiveButton("知道了") { d, _ -> d.dismiss() }
                .show()
        }
    }

    private fun setupKeypad() {
        // 使用数字键盘（1-9, 0, 清除），避免小朋友打开设置
        val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "清除", "0", "←")
        val size = resources.getDimensionPixelSize(R.dimen.button_min_width)
        val margin = resources.getDimensionPixelSize(R.dimen.spacing_small)

        keys.forEach { key ->
            val btn = Button(this).apply {
                text = key
                textSize = 28f
                textStyle = android.graphics.Typeface.BOLD
                layoutParams = LinearLayout.LayoutParams(0, size, 1f).apply {
                    setMargins(margin, margin, margin, margin)
                }
                setBackgroundResource(R.drawable.bg_card)
                setTextColor(resources.getColor(R.color.text_primary, null))
                backgroundTintList = androidx.core.content.ContextCompat.getColorStateList(
                    context, R.color.primary_light
                )
                setOnClickListener { onKeyPressed(key) }
            }
            binding.keypadGrid.addView(btn)
        }
    }

    private fun onKeyPressed(key: String) {
        when (key) {
            "清除" -> {
                inputPassword.clear()
                updateDisplay()
            }
            "←" -> {
                if (inputPassword.isNotEmpty()) {
                    inputPassword.deleteCharAt(inputPassword.length - 1)
                    updateDisplay()
                }
            }
            else -> {
                if (inputPassword.length < 8) {
                    inputPassword.append(key)
                    updateDisplay()
                    if (inputPassword.length >= 4) {
                        // 自动尝试验证
                        checkPassword()
                    }
                }
            }
        }
    }

    private fun updateDisplay() {
        binding.etPassword.text = "*".repeat(inputPassword.length)
    }

    private fun checkPassword() {
        if (prefs.checkPassword(inputPassword.toString())) {
            // 成功：进入家长控制主界面
            val intent = Intent(this, ParentDashboardActivity::class.java)
            startActivity(intent)
            inputPassword.clear()
            updateDisplay()
            finish()
        } else {
            // 密码错误
            android.widget.Toast.makeText(this, "密码错误，请重试", android.widget.Toast.LENGTH_SHORT).show()
            inputPassword.clear()
            updateDisplay()
        }
    }
}
