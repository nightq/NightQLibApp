/*
 * Copyright (C) 2014 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package freedom.nightq.baselibrary.utils;

import android.content.res.Resources;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import freedom.nightq.baselibrary.NightQAppLib;

/**
 * Utilities for working with strings, like splitting, url-encoding, and MD5 digests.
 *
 * @author markus
 */
public class StringUtils {

    /** Splits a String based on a single character, which is usually faster than regex-based String.split(). */
    public static String[] fastSplit(String string, char delimiter) {
        List<String> list = new ArrayList<String>();
        int size = string.length();
        int start = 0;
        for (int i = 0; i < size; i++) {
            if (string.charAt(i) == delimiter) {
                if (start < i) {
                    list.add(string.substring(start, i));
                } else {
                    list.add("");
                }
                start = i + 1;
            } else if (i == size - 1) {
                list.add(string.substring(start, size));
            }
        }
        String[] elements = new String[list.size()];
        list.toArray(elements);
        return elements;
    }

    /**
     * URL-Encodes a given string using UTF-8 (some web pages have problems with UTF-8 and umlauts, consider
     * {@link #encodeUrlIso(String)} also). No UnsupportedEncodingException to handle as it is dealt with in this
     * method.
     */
    public static String encodeUrl(String stringToEncode) {
        try {
            return URLEncoder.encode(stringToEncode, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException(e1);
        }
    }

    /**
     * URL-encodes a given string using ISO-8859-1, which may work better with web pages and umlauts compared to UTF-8.
     * No UnsupportedEncodingException to handle as it is dealt with in this method.
     */
    public static String encodeUrlIso(String stringToEncode) {
        try {
            return URLEncoder.encode(stringToEncode, "ISO-8859-1");
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException(e1);
        }
    }

    /**
     * URL-Decodes a given string using UTF-8. No UnsupportedEncodingException to handle as it is dealt with in this
     * method.
     */
    public static String decodeUrl(String stringToDecode) {
        try {
            return URLDecoder.decode(stringToDecode, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException(e1);
        }
    }

    /**
     * URL-Decodes a given string using ISO-8859-1. No UnsupportedEncodingException to handle as it is dealt with in
     * this method.
     */
    public static String decodeUrlIso(String stringToDecode) {
        try {
            return URLDecoder.decode(stringToDecode, "ISO-8859-1");
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException(e1);
        }
    }

    /**
     * Generates the MD5 digest for a given String based on UTF-8. The digest is padded with zeroes in the front if
     * necessary.
     *
     * @return MD5 digest (32 characters).
     */
    public static String generateMD5String(String stringToEncode) {
        return generateDigestString(stringToEncode, "MD5", "UTF-8", 32);
    }

    /**
     * Generates the SHA-1 digest for a given String based on UTF-8. The digest is padded with zeroes in the front if
     * necessary. The SHA-1 algorithm is considers to produce less collisions than MD5.
     *
     * @return SHA-1 digest (40 characters).
     */
    public static String generateSHA1String(String stringToEncode) {
        return generateDigestString(stringToEncode, "SHA-1", "UTF-8", 40);
    }

    public static String
    generateDigestString(String stringToEncode, String digestAlgo, String encoding, int lengthToPad) {
        // Loosely inspired by http://workbench.cadenhead.org/news/1428/creating-md5-hashed-passwords-java
        MessageDigest digester;
        try {
            digester = MessageDigest.getInstance(digestAlgo);
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae);
        }
        try {
            digester.update(stringToEncode.getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return toHexString(digester.digest(), lengthToPad);
    }

    public static String toHexString(byte[] bytes, int lengthToPad) {
        BigInteger hash = new BigInteger(1, bytes);
        String digest = hash.toString(16);

        while (digest.length() < lengthToPad) {
            digest = "0" + digest;
        }
        return digest;
    }

    /**
     * Simple HTML/XML entity resolving: Only supports unicode enitities and a very limited number text represented
     * entities (apos, quot, gt, lt, and amp). There are many more: http://www.w3.org/TR/REC-html40/sgml/dtd.html
     *
     * @param entity The entity name without & and ; (null throws NPE)
     * @return Resolved entity or the entity itself if it could not be resolved.
     */
    public static String resolveEntity(String entity) {
        if (entity.length() > 1 && entity.charAt(0) == '#') {
            if (entity.charAt(1) == 'x') {
                return String.valueOf((char) Integer.parseInt(entity.substring(2), 16));
            } else {
                return String.valueOf((char) Integer.parseInt(entity.substring(1)));
            }
        } else if (entity.equals("apos")) {
            return "'";
        } else if (entity.equals("quot")) {
            return "\"";
        } else if (entity.equals("gt")) {
            return ">";
        } else if (entity.equals("lt")) {
            return "<";
        } else if (entity.equals("amp")) {
            return "&";
        } else {
            return entity;
        }
    }

    /**
     * Cuts the string at the end if it's longer than maxLength and appends "..." to it. The length of the resulting
     * string including "..." is always less or equal to the given maxLength. It's valid to pass a null text; in this
     * case null is returned.
     */
    public static String ellipsize(String text, int maxLength) {
        if (text != null && text.length() > maxLength) {
            return text.substring(0, maxLength - 3) + "...";
        }
        return text;
    }

    public static String[] splitLines(String text, boolean skipEmptyLines) {
        if (skipEmptyLines) {
            return text.split("[\n\r]+");
        } else {
            return text.split("\\r?\\n");
        }
    }

    public static List<String> findLinesContaining(String text, String searchText) {
        String[] splitLinesSkipEmpty = splitLines(text, true);
        List<String> matching = new ArrayList<String>();
        for (String line : splitLinesSkipEmpty) {
            if (line.contains(searchText)) {
                matching.add(line);
            }
        }
        return matching;
    }

    /**
     * Returns a concatenated string consisting of the given lines seperated by a new line character \n. The last line
     * does not have a \n at the end.
     */
    public static String concatLines(List<String> lines) {
        StringBuilder builder = new StringBuilder();
        int countMinus1 = lines.size() - 1;
        for (int i = 0; i < countMinus1; i++) {
            builder.append(lines.get(i)).append('\n');
        }
        if (!lines.isEmpty()) {
            builder.append(lines.get(countMinus1));
        }
        return builder.toString();
    }

    public static String joinIterableOnComma(Iterable<?> iterable) {
        if (iterable != null) {

            StringBuilder buf = new StringBuilder();
            Iterator<?> it = iterable.iterator();
            while (it.hasNext()) {
                buf.append(it.next());
                if (it.hasNext()) {
                    buf.append(',');
                }
            }
            return buf.toString();
        } else {
            return "";
        }
    }

    /**
     * int 数组 转 string
     * @param array
     * @return
     */
    public static String intArrayToString(Integer[] array) {
        if (array != null) {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < array.length; i++) {
                if (i != 0) {
                    buf.append(',');
                }
                buf.append(array[i]);
            }
            return buf.toString();
        } else {
            return "";
        }

    }

    /**
     * string 转 int 数组
     * @param str
     * @return
     */
//    public static Integer[] stringToIntArray(String str) {
//        if (TextUtils.isEmpty(str)) {
//            String[] strArray = str.split(",");
//            List<Integer> intArray = new ArrayList<>();
//            for (int i = 0; i < strArray.length; i++) {
//                intArray.add(Integer.valueOf(strArray[i]));
//            }
//            return (Integer[]) intArray.toArray();
//        } else {
//            return new Integer[0];
//        }
//
//    }

    /**
     * int 转 string 数组
     * @param intArr
     * @return
     */
    public static String[] intToStringArray(Integer[] intArr) {
        if (intArr != null) {
            String[] strArray = new String[intArr.length];
            for (int i = 0; i < intArr.length; i++) {
                strArray[i] = (String.valueOf(intArr[i]));
            }
            return strArray;
        } else {
            return new String[0];
        }

    }

    public static String joinArrayOnComma(String[] array) {
        if (array != null) {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < array.length; i++) {
                if (i != 0) {
                    buf.append(',');
                }
                buf.append(array[i]);
            }
            return buf.toString();
        } else {
            return "";
        }
    }

    /**
     * 连接url的各个部分
     *
     * @param base
     * @param paths
     *            各个path前后需不能含/\
     * @return
     */
    public static String joinUrl(String base, String... paths) {
        StringBuilder url = new StringBuilder();
        if (!TextUtils.isEmpty(base)) {
            if (base.endsWith("/")) {
                url.append(base.substring(0, base.length() - 1));
            } else {
                url.append(base);
            }
        }

        int length = paths.length;
        for (int i = 0; i < length; i++) {
            url.append("/").append(paths[i]);
        }

        return url.toString();
    }

    private static Resources resources;
//    public static String getStringFromRes(int resId) {
//        if(resources == null) {
//            resources = NightQAppLib.getAppContext().getResources();
//        }
//        return resources.getString(resId);
//    }

    public static String getStringFromRes(int resId, Object... formatArgs) {
        if(resources == null) {
            resources = NightQAppLib.getAppContext().getResources();
        }

        return resources.getString(resId, formatArgs);
    }

    public static String getQuantityStringFromRes(int resId, int quantity, Object... formatArgs) {
        if(resources == null) {
            resources = NightQAppLib.getAppContext().getResources();
        }

        return resources.getQuantityString(resId, quantity, formatArgs);
    }


    // /** not really public yet, umlauts!? */
    // public static boolean __isValidEmail(String email) {
    // // TODO compile and cache pattern using a ThreadLocal
    // return Pattern.matches("^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+", email);
    // }

    public static boolean onlyCharAndNum_(String str) {
        // 只允许字母和数字和下划线
        String regEx = "^\\w+$";
        return Pattern.matches(regEx, str);
    }

    public static boolean onlyChar(String str) {
        // 只允许字母和数字和下划线
        String regEx = "^[A-Za-z]+$";
        return Pattern.matches(regEx, str);
    }

    public static boolean isChineseName(String str) {
        String regEx = "^[\\u4e00-\\u9fa5]{2,4}+$";
        return Pattern.matches(regEx, str);
    }

    /**
     * 手机号验证
     *
     * @param  str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 通过判断是否是 http 开始 来判断是否是 url
     * @return
     */
    public static boolean isUrlString (String string) {
        return !TextUtils.isEmpty(string) && string.startsWith("http");
    }

    /**
     * 字符串都是数字吗
     * @param str
     * @return
     */
    public static boolean isNumber(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if(!isNum.matches()){
            return false;
        }
        return true;
    }
}
