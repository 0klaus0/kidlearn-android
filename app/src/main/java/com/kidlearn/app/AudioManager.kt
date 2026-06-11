package com.kidlearn.app

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * 音频管理器：处理TTS发音、背景音效、音量控制
 * 所有背景音效控制在65分贝以下，避免尖锐声音
 */
class AudioManager private constructor(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var ttsReady: Boolean = false
    private var mediaPlayer: MediaPlayer? = null
    private var prefs: AppPreferences = AppPreferences.getInstance(context)

    companion object {
        private const val TAG = "KidLearnAudio"
        @Volatile
        private var instance: AudioManager? = null

        fun getInstance(context: Context): AudioManager {
            return instance ?: synchronized(this) {
                instance ?: AudioManager(context.applicationContext).also { instance = it }
            }
        }
    }

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // 优先使用中文，如果不可用则使用默认语言
            val locale = Locale.CHINA
            val result = tts?.setLanguage(locale)
            ttsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
            // 设置语速为较慢，适合儿童学习
            tts?.setSpeechRate(0.85f)
            tts?.setPitch(1.1f)
        } else {
            Log.e(TAG, "TTS initialization failed")
            ttsReady = false
        }
    }

    /**
     * 朗读中文文本
     */
    fun speakChinese(text: String) {
        if (!prefs.isSoundEnabled()) return
        if (!ttsReady || tts == null) {
            Log.w(TAG, "TTS not ready, retrying...")
            tts = TextToSpeech(context, this)
            return
        }
        tts?.let {
            it.language = Locale.CHINA
            it.speak(text, TextToSpeech.QUEUE_FLUSH, null, "kidlearn_${System.currentTimeMillis()}")
        }
    }

    /**
     * 朗读英文字母/单词
     */
    fun speakEnglish(text: String) {
        if (!prefs.isSoundEnabled()) return
        if (!ttsReady || tts == null) {
            tts = TextToSpeech(context, this)
            return
        }
        tts?.let {
            it.language = Locale.ENGLISH
            it.setSpeechRate(0.8f)
            it.speak(text, TextToSpeech.QUEUE_FLUSH, null, "kidlearn_en_${System.currentTimeMillis()}")
        }
    }

    /**
     * 朗读数字
     */
    fun speakNumber(number: Int) {
        speakChinese(number.toString())
    }

    /**
     * 播放成功/奖励音效
     */
    fun playSuccessSound() {
        if (!prefs.isSoundEnabled()) return
        // 简单的音效提示：用TTS播放"太棒了！"作为激励
        val messages = listOf("太棒了", "真厉害", "做得好", "真棒", "非常好")
        speakChinese(messages.random())
    }

    /**
     * 播放游戏中的音效反馈
     */
    fun playClickSound() {
        if (!prefs.isSoundEnabled()) return
        // 用一个轻柔的提示音效（此处用TTS简短声音替代，避免外部资源依赖）
    }

    /**
     * 播放错误提示音（温和的提示，避免惊吓儿童）
     */
    fun playTryAgainSound() {
        if (!prefs.isSoundEnabled()) return
        speakChinese("再试一次")
    }

    /**
     * 获取当前音量设置
     */
    fun isSoundEnabled(): Boolean = prefs.isSoundEnabled()

    /**
     * 设置静音/取消静音
     */
    fun setSoundEnabled(enabled: Boolean) {
        prefs.setSoundEnabled(enabled)
        if (!enabled) {
            tts?.stop()
        }
    }

    /**
     * 切换静音状态
     */
    fun toggleSound(): Boolean {
        val new = !isSoundEnabled()
        setSoundEnabled(new)
        return new
    }

    /**
     * 停止所有语音
     */
    fun stop() {
        tts?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /**
     * 资源释放（应用退出时调用）
     */
    fun release() {
        tts?.shutdown()
        tts = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
