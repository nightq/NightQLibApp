package freedom.nightq.baselibrary.utils;

import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by H3c on 12/5/15.
 */
public class GoToURLSpan extends ClickableSpan {
    String url;

    public GoToURLSpan(String url){
        this.url = url;
    }

    public void onClick(View view) {
        Uri webPage = Uri.parse(url); //http:<URL> or https:<URL>
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        view.getContext().startActivity(intent);
    }

    public static void setWebClickableText(TextView textView, String text) {
        int i=0;
        SpannableString spannableString = new SpannableString(text);
        Matcher urlMatcher = WEB_URL.matcher(text);
        while(urlMatcher.find()) {
            String url = urlMatcher.group(i);
            int start = urlMatcher.start(i);
            int end = urlMatcher.end(i++);
            spannableString.setSpan(new GoToURLSpan(url), start, end, 0);
        }

        textView.setText(spannableString);
        if(urlMatcher.find()) {
            textView.setMovementMethod(new LinkMovementMethod());
        }
    }

    public static SpannableString getWebClickableText(String text) {
        int i=0;
        SpannableString spannableString = new SpannableString(text);
        Matcher urlMatcher = WEB_URL.matcher(text);
        while(urlMatcher.find()) {
            String url = urlMatcher.group(i);
            int start = urlMatcher.start(i);
            int end = urlMatcher.end(i++);
            spannableString.setSpan(new GoToURLSpan(url), start, end, 0);
        }

        return spannableString;
    }

    public static final Pattern WEB_URL = Pattern.compile(
            "([a-zA-z]+://[^\\s]*)");
}