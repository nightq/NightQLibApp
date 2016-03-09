package freedom.nightq.baselibrary.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nightq on 15/10/28.
 */
public class ClickSpanUtil {

    public static class ParamRange implements Comparable<ParamRange> {
        // 这时第几个参数
        public int paramPosition;
        public int start;
        public int end;

        public ParamRange() {
        }

        @Override
        public int compareTo(ParamRange another) {
            if (paramPosition == another.paramPosition) {
                return 0;
            } else if (paramPosition > another.paramPosition) {
                return 1;
            }
            return -1;
        }
    }

    /**
     * 格式化字符串，可以增加点击事件
     */
    public static SpannableStringBuilder formatSpanString(
            //  for   *1$s love *2$s, but *3$s do not love *4$s
            // 可以是spannable , 会使用 SpannableStringBuilder.replace 替换
            String format,
            // format 中的参数替换
            SparseArray<CharSequence> formatArg,
            // format 中对应位置的参数点击出发 clickableSpans 对应位置的事件。
            SparseArray<ClickableSpan> clickableSpans) {

        // init
        if (formatArg == null) {
            formatArg = new SparseArray<>();
        }
        if (clickableSpans == null) {
            clickableSpans = new SparseArray<>();
        }
        // get param 数量
        Matcher matcher = Pattern
                .compile("\\*([0-9]+)\\$s", Pattern.CASE_INSENSITIVE)
                .matcher(format);
        int paramCount = 0;
        int paramPosition = 0;
        String group;

        // 记录每个参数 的位置
        List<ParamRange> paramsRanges = new ArrayList<>();
        ParamRange paramRange;
        while (matcher.find()) {
            group = matcher.group();
            try {
                group = group.substring(1, group.length() - 2);
                paramPosition = Integer.valueOf(group);
                paramRange = new ParamRange();
                paramRange.paramPosition = paramPosition;
                paramRange.start = matcher.start();
                paramRange.end = matcher.end();
                paramsRanges.add(paramRange);
            } catch (Exception e) {
                LogUtils.e("nightq", "formatClickSpan Exception = " + group);
            }
            if (paramPosition > paramCount) {
                paramCount = paramPosition;
            }
        }
        // 对参数排序
        Collections.sort(paramsRanges);

        // 被邀请的那些人的名字
        SpannableStringBuilder formatSSB = new SpannableStringBuilder(format);
        ParamRange replaceParam;
        for (int i = paramsRanges.size() - 1; i >= 0; i--) {
            // 逆序替换参数
            replaceParam = paramsRanges.get(i);
            replaceParamSpan(
                    formatSSB,
                    replaceParam,
                    formatArg.valueAt(replaceParam.paramPosition-1),
                    clickableSpans.valueAt(replaceParam.paramPosition-1));
        }
        return formatSSB;
    }


    /**
     * 替换param
     */
    private static void replaceParamSpan(
            SpannableStringBuilder formatSSB,
            ParamRange paramRange,
            CharSequence arg,
            ClickableSpan clickableSpan) {
        if (TextUtils.isEmpty(arg)) {
            return;
        }
        int length = arg.length();
        CharSequence setArg;
        if (arg instanceof Spannable) {
            setArg = arg;
        } else {
            setArg = new SpannableString(arg);
        }
        if (clickableSpan != null) {
            ((Spannable)setArg).setSpan(
                    clickableSpan,
                    0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        formatSSB.replace(paramRange.start, paramRange.end,
                setArg);
    }

    /**
     * 生成 参数 SparseArray
     * @param args
     * @return
     */
    public static <T> SparseArray<T> generateFormatArg (T... args) {
        SparseArray<T> formatArgs = new SparseArray<>();
        if (args == null) {
            return formatArgs;
        }
        for (int i=0; i<args.length; i++) {
            formatArgs.setValueAt(i, args[i]);
        }
        return formatArgs;
    }
}
