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

import java.util.Calendar;

import freedom.nightq.baselibrary.R;

/**
 * Simple Date and time utils.
 */
public class DateUtils {
    /**
     * Calendar objects are rather expensive: for heavy usage it's a good idea to use a single instance per thread
     * instead of calling Calendar.getAppContext() multiple times. Calendar.getAppContext() creates a new instance each
     * time.
     */
    public static final class DefaultCalendarThreadLocal extends ThreadLocal<Calendar> {
        @Override
        protected Calendar initialValue() {
            return Calendar.getInstance();
        }
    }

    private static ThreadLocal<Calendar> calendarThreadLocal = new DefaultCalendarThreadLocal();

    public static long getTimeForDay(int year, int month, int day) {
        return getTimeForDay(calendarThreadLocal.get(), year, month, day);
    }

    /** @param calendar helper object needed for conversion */
    public static long getTimeForDay(Calendar calendar, int year, int month, int day) {
        calendar.clear();
        calendar.set(year, month - 1, day);
        return calendar.getTimeInMillis();
    }

    /** Sets hour, minutes, seconds and milliseconds to the given values. Leaves date info untouched. */
    public static void setTime(Calendar calendar, int hourOfDay, int minute, int second, int millisecond) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
    }

    /** Readable yyyyMMdd int representation of a day, which is also sortable. */
    public static int getDayAsReadableInt(long time) {
        Calendar cal = calendarThreadLocal.get();
        cal.setTimeInMillis(time);
        return getDayAsReadableInt(cal);
    }

    /** Readable yyyyMMdd representation of a day, which is also sortable. */
    public static int getDayAsReadableInt(Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        return year * 10000 + month * 100 + day;
    }

    /** Returns midnight of the given day. */
    public static long getTimeFromDayReadableInt(int day) {
        return getTimeFromDayReadableInt(calendarThreadLocal.get(), day, 0);
    }

    /** @param calendar helper object needed for conversion */
    public static long getTimeFromDayReadableInt(Calendar calendar, int readableDay, int hour) {
        int day = readableDay % 100;
        int month = readableDay / 100 % 100;
        int year = readableDay / 10000;

        calendar.clear(); // We don't set all fields, so we should clear the calendar first
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);

        return calendar.getTimeInMillis();
    }

    public static int getDayDifferenceOfReadableInts(int dayOfBroadcast1, int dayOfBroadcast2) {
        long time1 = getTimeFromDayReadableInt(dayOfBroadcast1);
        long time2 = getTimeFromDayReadableInt(dayOfBroadcast2);

        // Don't use getDayDifference(time1, time2) here, it's wrong for some days.
        // Do float calculation and rounding at the end to cover daylight saving stuff etc.
        float daysFloat = (time2 - time1) / 1000 / 60 / 60 / 24f;
        return Math.round(daysFloat);
    }

    public static int getDayDifference(long time1, long time2) {
        return (int) ((time2 - time1) / 1000 / 60 / 60 / 24);
    }

    public static long addDays(long time, int days) {
        Calendar calendar = calendarThreadLocal.get();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTimeInMillis();
    }

    public static void addDays(Calendar calendar, int days) {
        calendar.add(Calendar.DAY_OF_YEAR, days);
    }


    public static String getFormatDate(long time) {
        int[] ymd = getDateByYMD(time);
        return StringUtils.getStringFromRes(R.string.homepageTimeFormat, ymd[0]+"", ymd[1]+"", ymd[2]+"");
    }

    public static String getFormatDateByMDHM(long time) {
        int[] ymd = getDateByYMDHMS(time);
        String hour = (ymd[3] < 10) ? "0" + ymd[3] : ymd[3] + "";
        String min = (ymd[4] < 10) ? "0" + ymd[4] : ymd[4] + "";
        return StringUtils.getStringFromRes(R.string.timeFormat_MDHM, ymd[1]+"", ymd[2]+"", hour, min);
    }

    public static int[] getDateByYMD(long time) {
        Calendar cal = calendarThreadLocal.get();
        cal.setTimeInMillis(time);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        return new int[] {year, month, day};
    }

    public static int[] getDateByYMDHMS(long time) {
        Calendar cal = calendarThreadLocal.get();
        cal.setTimeInMillis(time);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        return new int[] {year, month, day, hour, min, second};
    }

    /**
     * 获取距离此时的时间
     * @param time
     * @return
     */
    public static String getDistanceNowTime(long time) {
        long currentTime = Calendar.getInstance().getTimeInMillis();

        long value = (currentTime - time) / 1000;
        if(value < 60) {
            return StringUtils.getStringFromRes(R.string.time_current);
        } else if(value < 60 * 60) {
            int minute = (int)(value / 60);
            return StringUtils.getStringFromRes(R.string.time_minute_ago, minute);
        } else if(value < 24 * 60 * 60) {
            int hour = (int)(value / (60 * 60));
            return StringUtils.getStringFromRes(R.string.time_hour_ago, hour);
        } else if (value < (2 * 24 * 60 * 60)) {
            return StringUtils.getStringFromRes(R.string.yesterday);
        }

        return getFormatDateByMDHM(time);
    }

    public static String getInternationYearStr(int year) {
        return StringUtils.getStringFromRes(R.string.year , year);
    }

    public static String getInternationDayStr(int day) {
        return StringUtils.getStringFromRes(R.string.day , day);
    }

    public static String getInternationMonthStr(int month) {
        int strRes = 0;
        switch (month) {
            case 1:
                strRes = R.string.january;
                break;
            case 2:
                strRes = R.string.february;
                break;
            case 3:
                strRes = R.string.march;
                break;
            case 4:
                strRes = R.string.april;
                break;
            case 5:
                strRes = R.string.may;
                break;
            case 6:
                strRes = R.string.june;
                break;
            case 7:
                strRes = R.string.july;
                break;
            case 8:
                strRes = R.string.august;
                break;
            case 9:
                strRes = R.string.september;
                break;
            case 10:
                strRes = R.string.october;
                break;
            case 11:
                strRes = R.string.december;
                break;
            case 12:
                strRes = R.string.november;
                break;
        }

        return strRes > 0 ? StringUtils.getStringFromRes(strRes) : null;
    }
}
