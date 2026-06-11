package com.kidlearn.app

/**
 * 年龄分组枚举：对应 2-3岁、4-5岁、5-6岁 三个难度等级
 */
enum class AgeGroup(val minAge: Int, val maxAge: Int) {
    AGE_2_3(2, 3),
    AGE_4_5(4, 5),
    AGE_5_6(5, 6);

    companion object {
        fun fromString(value: String): AgeGroup {
            return values().find { it.name == value } ?: AGE_2_3
        }
    }
}

/**
 * 学习分类枚举
 */
enum class LearningCategory(val displayName: String) {
    NUMBERS("数字"),
    LETTERS("字母"),
    COLORS("颜色"),
    SHAPES("形状"),
    GAMES("游戏")
}

/**
 * 数字学习数据：0-10
 */
data class NumberItem(
    val number: Int,
    val nameResId: Int,
    val emoji: String,
    val countExample: String
) {
    companion object {
        fun getAll(): List<NumberItem> = listOf(
            NumberItem(0, R.string.number_zero, "0️⃣", "没有苹果"),
            NumberItem(1, R.string.number_one, "1️⃣", "1 个苹果"),
            NumberItem(2, R.string.number_two, "2️⃣", "2 个香蕉"),
            NumberItem(3, R.string.number_three, "3️⃣", "3 只小猫"),
            NumberItem(4, R.string.number_four, "4️⃣", "4 只小狗"),
            NumberItem(5, R.string.number_five, "5️⃣", "5 朵小花"),
            NumberItem(6, R.string.number_six, "6️⃣", "6 个气球"),
            NumberItem(7, R.string.number_seven, "7️⃣", "7 颗糖果"),
            NumberItem(8, R.string.number_eight, "8️⃣", "8 辆小车"),
            NumberItem(9, R.string.number_nine, "9️⃣", "9 颗星星"),
            NumberItem(10, R.string.number_ten, "🔟", "10 个小朋友")
        )

        fun getForAge(age: AgeGroup): List<NumberItem> {
            return when (age) {
                AgeGroup.AGE_2_3 -> getAll().take(6)  // 2-3岁：0-5
                AgeGroup.AGE_4_5 -> getAll().take(9)  // 4-5岁：0-8
                AgeGroup.AGE_5_6 -> getAll()           // 5-6岁：0-10
            }
        }
    }
}

/**
 * 字母学习数据：A-Z
 */
data class LetterItem(
    val letter: Char,
    val word: String,
    val emoji: String
) {
    companion object {
        fun getAll(): List<LetterItem> = listOf(
            LetterItem('A', "Apple", "🍎"),
            LetterItem('B', "Ball", "⚽"),
            LetterItem('C', "Cat", "🐱"),
            LetterItem('D', "Dog", "🐶"),
            LetterItem('E', "Egg", "🥚"),
            LetterItem('F', "Fish", "🐟"),
            LetterItem('G', "Giraffe", "🦒"),
            LetterItem('H', "Hat", "🎩"),
            LetterItem('I', "Ice cream", "🍦"),
            LetterItem('J', "Juice", "🧃"),
            LetterItem('K', "Kite", "🪁"),
            LetterItem('L', "Lion", "🦁"),
            LetterItem('M', "Moon", "🌙"),
            LetterItem('N', "Nest", "🪺"),
            LetterItem('O', "Orange", "🍊"),
            LetterItem('P', "Pig", "🐷"),
            LetterItem('Q', "Queen", "👑"),
            LetterItem('R', "Rabbit", "🐰"),
            LetterItem('S', "Sun", "☀️"),
            LetterItem('T', "Tree", "🌳"),
            LetterItem('U', "Umbrella", "☂️"),
            LetterItem('V', "Van", "🚐"),
            LetterItem('W', "Whale", "🐋"),
            LetterItem('X', "Xylophone", "🎹"),
            LetterItem('Y', "Yarn", "🧶"),
            LetterItem('Z', "Zebra", "🦓")
        )

        fun getForAge(age: AgeGroup): List<LetterItem> {
            return when (age) {
                AgeGroup.AGE_2_3 -> getAll().take(10)  // A-J
                AgeGroup.AGE_4_5 -> getAll().take(18)  // A-R
                AgeGroup.AGE_5_6 -> getAll()           // A-Z
            }
        }
    }
}

/**
 * 颜色学习数据：8种基础颜色
 */
data class ColorItem(
    val colorResId: Int,
    val colorValue: Int,
    val nameResId: Int,
    val emoji: String
) {
    companion object {
        fun getAll(): List<ColorItem> = listOf(
            ColorItem(R.color.red, 0xFFE57373.toInt(), R.string.color_red, "🍎"),
            ColorItem(R.color.orange, 0xFFFFB74D.toInt(), R.string.color_orange, "🍊"),
            ColorItem(R.color.yellow, 0xFFFFF176.toInt(), R.string.color_yellow, "🌟"),
            ColorItem(R.color.green, 0xFF81C784.toInt(), R.string.color_green, "🍀"),
            ColorItem(R.color.cyan, 0xFF4DD0E1.toInt(), R.string.color_cyan, "💧"),
            ColorItem(R.color.blue, 0xFF64B5F6.toInt(), R.string.color_blue, "🐳"),
            ColorItem(R.color.purple, 0xFFBA68C8.toInt(), R.string.color_purple, "🍇"),
            ColorItem(R.color.pink, 0xFFF48FB1.toInt(), R.string.color_pink, "🌸")
        )

        fun getForAge(age: AgeGroup): List<ColorItem> {
            return when (age) {
                AgeGroup.AGE_2_3 -> getAll().take(4)  // 红橙黄绿
                AgeGroup.AGE_4_5 -> getAll().take(6)  // 红橙黄绿青蓝
                AgeGroup.AGE_5_6 -> getAll()           // 全部8种
            }
        }
    }
}

/**
 * 形状学习数据：6种基础形状
 */
data class ShapeItem(
    val shapeType: String,
    val nameResId: Int,
    val emoji: String
) {
    companion object {
        fun getAll(): List<ShapeItem> = listOf(
            ShapeItem("circle", R.string.shape_circle, "⭕"),
            ShapeItem("square", R.string.shape_square, "⬛"),
            ShapeItem("triangle", R.string.shape_triangle, "🔺"),
            ShapeItem("rectangle", R.string.shape_rectangle, "▬"),
            ShapeItem("star", R.string.shape_star, "⭐"),
            ShapeItem("heart", R.string.shape_heart, "❤️")
        )

        fun getForAge(age: AgeGroup): List<ShapeItem> {
            return when (age) {
                AgeGroup.AGE_2_3 -> getAll().take(3)  // 圆、方、三角
                AgeGroup.AGE_4_5 -> getAll().take(5)  // 加长方形、星形
                AgeGroup.AGE_5_6 -> getAll()           // 全部6种
            }
        }
    }
}

/**
 * 贴纸数据
 */
data class StickerItem(
    val id: String,
    val name: String,
    val emoji: String,
    val requiredStars: Int
) {
    companion object {
        fun getAll(): List<StickerItem> = listOf(
            StickerItem("s1", "小星星", "⭐", 5),
            StickerItem("s2", "小猫咪", "🐱", 10),
            StickerItem("s3", "小狗狗", "🐶", 15),
            StickerItem("s4", "小兔子", "🐰", 20),
            StickerItem("s5", "小花朵", "🌸", 25),
            StickerItem("s6", "小彩虹", "🌈", 30),
            StickerItem("s7", "小太阳", "☀️", 40),
            StickerItem("s8", "皇冠", "👑", 50),
            StickerItem("s9", "奖杯", "🏆", 75),
            StickerItem("s10", "钻石", "💎", 100)
        )
    }
}

/**
 * 成就数据
 */
data class AchievementItem(
    val id: String,
    val nameResId: Int,
    val description: String,
    val emoji: String
) {
    companion object {
        fun getAll(): List<AchievementItem> = listOf(
            AchievementItem("ach_first_star", R.string.ach_first_star, "获得第一颗星星", "⭐"),
            AchievementItem("ach_ten_stars", R.string.ach_ten_stars, "收集10颗星星", "🌟"),
            AchievementItem("ach_fifty_stars", R.string.ach_fifty_stars, "收集50颗星星", "💫"),
            AchievementItem("ach_number_master", R.string.ach_number_master, "学习完所有数字", "🔢"),
            AchievementItem("ach_letter_master", R.string.ach_letter_master, "学习完所有字母", "🔤"),
            AchievementItem("ach_color_master", R.string.ach_color_master, "认识所有颜色", "🎨"),
            AchievementItem("ach_shape_master", R.string.ach_shape_master, "认识所有形状", "🔷"),
            AchievementItem("ach_game_master", R.string.ach_game_master, "玩10次游戏", "🎮"),
            AchievementItem("ach_first_day", R.string.ach_first_day, "第一天学习", "📅"),
            AchievementItem("ach_seven_days", R.string.ach_seven_days, "连续学习7天", "🏅")
        )
    }
}

/**
 * 游戏类型
 */
enum class GameType(val displayName: String, val emoji: String) {
    MATCHING("配对游戏", "🎯"),
    DRAGDROP("拖拽游戏", "✋"),
    PUZZLE("拼图游戏", "🧩"),
    SOUND("声音识别", "🔊"),
    COLORING("涂色游戏", "🖍️")
}
