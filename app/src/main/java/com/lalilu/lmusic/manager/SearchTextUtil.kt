package com.lalilu.lmusic.manager

import android.content.Context
import android.text.TextUtils
import com.cm55.kanhira.KakasiDictReader
import com.cm55.kanhira.Kanhira
import com.lalilu.R
import com.lalilu.lmusic.utils.KanaToRomaji
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

/**
 * 关键词匹配
 *
 * @param keyword 关键词
 * @param list 所需要进行查询匹配的一系列item
 * @param getString 从item中获取所需要进行查询的原始字符串
 */
inline fun <I> SearchTextUtil.filter(
    keyword: String?,
    list: List<I>,
    getString: (I) -> String
): List<I> {
    if (keyword == null || TextUtils.isEmpty(keyword)) return list
    val keywords = keyword.split(" ")

    return list.filter {
        val originStr = getString(it)
        var resultStr = originStr
        val isContainChinese = isContainChinese(originStr)
        val isContainKatakanaOrHinagana = isContainKatakanaOrHinagana(originStr)
        if (isContainChinese || isContainKatakanaOrHinagana) {
            if (isContainChinese) {
                val chinese = toHanYuPinyinString(originStr)
                resultStr += " $chinese"
            }

            val japanese = toHiraString(originStr)
            val romaji = toRomajiString(japanese)
            resultStr += " $romaji"
        }
        checkKeywords(resultStr, keywords)
    }
}

/**
 * 用于实现搜索功能的工具类
 *
 * 包含功能：汉字转拼音、汉字转假名、假名转罗马字
 *         字符串汉字检测，字符串匹配
 */
@Singleton
class SearchTextUtil @Inject constructor(
    @ApplicationContext context: Context
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO

    private val kanaToRomaji = KanaToRomaji()
    private val format = HanyuPinyinOutputFormat().also {
        it.caseType = HanyuPinyinCaseType.UPPERCASE
        it.toneType = HanyuPinyinToneType.WITHOUT_TONE
        it.vCharType = HanyuPinyinVCharType.WITH_U_UNICODE
    }

    /**
     * 异步加载Kanhira组件
     */
    private val mKanhira = MutableStateFlow<Kanhira?>(null).apply {
        launch {
            emit(
                Kanhira(
                    KakasiDictReader.load(
                        context.resources.openRawResource(R.raw.kakasidict_utf_8),
                        Charsets.UTF_8.name()
                    )
                )
            )
        }
    }

    /**
     * 检查字符串[str]与[keywords]的每一个item有没有相似的部分
     */
    fun checkKeywords(str: CharSequence?, keywords: List<String>): Boolean {
        keywords.forEach { keyword ->
            if (!checkKeyword(str, keyword)) return false
        }
        return true
    }

    /**
     * 检查字符串[str]与[keyword]有没有相似的部分
     */
    private fun checkKeyword(str: CharSequence?, keyword: String): Boolean {
        str ?: return false
        return str.toString().uppercase(Locale.getDefault()).contains(
            keyword.uppercase(Locale.getDefault())
        )
    }

    /**
     * 将汉字转为拼音
     */
    fun toHanYuPinyinString(text: String): String? {
        return PinyinHelper.toHanYuPinyinString(text, format, "", true)
    }

    /**
     * 将日文汉字转为平假名或片假名
     */
    fun toHiraString(text: String): String {
        return runBlocking {
            val kanhira = mKanhira.first() ?: return@runBlocking ""
            return@runBlocking kanhira.convert(text)
        }
    }

    /**
     * 将片假名或平假名转为罗马字
     */
    fun toRomajiString(text: String): String? {
        return kanaToRomaji.convert(text)
    }

    /**
     * 判断是否包含汉字（日文汉字、简体汉字、繁体汉字）
     */
    fun isContainChinese(str: String): Boolean {
        return Pattern.compile("[\u4e00-\u9fa5]").matcher(str).find()
    }

    /**
     * 判断是否包含日文片假名或平假名
     */
    fun isContainKatakanaOrHinagana(str: String): Boolean {
        return Pattern.compile("[\u3040-\u309f]").matcher(str).find() ||
                Pattern.compile("[\u30a0-\u30ff]").matcher(str).find()
    }
}