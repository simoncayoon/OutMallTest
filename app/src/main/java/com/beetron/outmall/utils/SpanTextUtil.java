package com.beetron.outmall.utils;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;

/**
 * Created by DKY with IntelliJ IDEA.
 * Author: DKY email: losemanshoe@gmail.com.
 * Date: 2016/2/24.
 * Time: 14:44.
 */
public class SpanTextUtil {

    /**
     * 字符串样式
     * @param indexStart 开始位置
     * @param indexEnd 结束位置
     * @param string 源字符串
     * @param difSize 不同大小
     * @return
     */
    public static SpannableString setSpanSize(int indexStart, int indexEnd, String string, int difSize) {
        SpannableString spannableString = new SpannableString(string);
        //第二个参数boolean dip，如果为true，表示前面的字体大小单位为dip，否则为像素，同上。
        spannableString.setSpan(new AbsoluteSizeSpan(difSize,true), indexStart, indexEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
