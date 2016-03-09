package freedom.nightq.baselibrary.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import java.io.File;

import freedom.nightq.baselibrary.NightQAppLib;
import freedom.nightq.baselibrary.R;

/**
 * Created by H3c on 2/2/15.
 */
public class DeviceUtils {
    public static ApplicationInfo mApplicationInfo;
    public static PackageManager mPackageManager;
    public static PackageInfo mPackageInfo;
    public static TelephonyManager mTelephonyManager;
    public static float density;
    public static float scaledDensity;
    public static int screenWPixels;
    public static int screenHPixels;
    public static int statusBarHeight;

    public static void init () {
        WindowManager wm = (WindowManager) NightQAppLib.getAppContext().
                getSystemService(Context.WINDOW_SERVICE);

        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        density = (float) metrics.widthPixels / metrics.densityDpi;
        density = metrics.density;
        scaledDensity = metrics.scaledDensity;
        screenWPixels = size.x;
        screenHPixels = size.y;

        int resourceId =  NightQAppLib.getAppContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = NightQAppLib.getAppContext().getResources().getDimensionPixelSize(resourceId);
        }
    }

    public static int getHeightavailable() {
        return screenHPixels - statusBarHeight;
    }

    /**
     * 返回 Dimension
     * @param resourceId
     * @return
     */
    public static int getDimension (int resourceId) {
        return NightQAppLib.getAppContext().getResources().getDimensionPixelSize(resourceId);
    }

    private static void initPackageInfo() {
        if(mPackageManager == null) {
            mPackageManager = NightQAppLib.getAppContext().getPackageManager();
        }

        if(mPackageInfo == null) {
            try {
                mPackageInfo = mPackageManager.getPackageInfo(NightQAppLib.getAppContext().getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static void initTelephonyManager() {
        if(mTelephonyManager == null) {
            mTelephonyManager = (TelephonyManager) NightQAppLib.getAppContext().
                    getSystemService(Context.TELEPHONY_SERVICE);
        }
    }

    public static int spToPx(int sp) {
        return (int) (sp * scaledDensity);
    }

    public static int dpToPx(double dp) {
        return (int) (dp * density);
    }

    /**
     * 手机型号简称(GT-N7102)
     * @return
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 手机型号(n7102)
     * @return
     */
    public static String getDeviceName() {
        return Build.DEVICE;
    }

    /**
     * 手机厂家(samsung)
     * @return
     */
    public static String getManufacturer() {
        // Build.BRAND 也可以
        return Build.MANUFACTURER;
    }

    /**
     * 硬件序列号(4dfd09d082156009)
     * @return
     */
    public static String getSerial() {
        return Build.SERIAL;
    }

    /**
     * 获得Mac地址(34:23:ba:14:07:62)
     * @return
     */
    public static String getMacAddress() {
        WifiManager wifiManager = NetworkUtil.getWifiManager();
        if(wifiManager != null) {
            WifiInfo info = wifiManager.getConnectionInfo();
            return info.getMacAddress();
        }

        return null;
    }

    /**
     * 设备IMEI(355546057471164)
     * @return
     */
    public static String getIMEI() {
        initTelephonyManager();

        if(mTelephonyManager != null) {
            return mTelephonyManager.getDeviceId();
        }

        return null;
    }

    /**
     * SIM卡序列号(89860113871048601206)
     * @return
     */
    public static String getSIMCardSerial() {
        initTelephonyManager();

        if(mTelephonyManager != null) {
            return mTelephonyManager.getSimSerialNumber();
        }

        return null;
    }

    /**
     * SIM卡Id(460013242301689)
     * @return
     */
    public static String getSIMCardId() {
        initTelephonyManager();

        if(mTelephonyManager != null) {
            return mTelephonyManager.getSubscriberId();
        }

        return null;
    }

    /**
     * 手机号(+8615623240890)
     * @return
     */
    public static String getPhoneNum() {
        initTelephonyManager();

        if(mTelephonyManager != null) {
            String phoneNumStr = mTelephonyManager.getLine1Number();
            if(phoneNumStr != null && phoneNumStr.startsWith("+86")) {
                phoneNumStr = phoneNumStr.substring(3);
            }

            long phoneNum = 0;
            try {
                phoneNum = Long.valueOf(phoneNumStr);
            } catch (Exception e) {
            }

            if(phoneNum > 0) {
                return phoneNumStr;
            }
        }

        return null;
    }

    public static String getPhoneCode(){
        initTelephonyManager();

        String CountryID="";
        String CountryZipCode="";

        if(mTelephonyManager != null) {
            //getNetworkCountryIso
            CountryID= mTelephonyManager.getSimCountryIso().toUpperCase();

            String[] rl= NightQAppLib.getAppContext().getResources()
                    .getStringArray(R.array.CountryCodes);
            for(int i=0;i<rl.length;i++){
                String[] g=rl[i].split(",");
                if(g[1].trim().equals(CountryID.trim())){
                    CountryZipCode=g[0];
                    break;
                }
            }
        }

        if(TextUtils.isEmpty(CountryZipCode)) {
            CountryZipCode = "86";
        }

        return CountryZipCode;
    }

    /**
     * 设备唯一ID(cd2e5f0b4f365e41)
     * @return
     */
    public static String getDeviceUniqueId() {
        return Settings.Secure.getString(
                NightQAppLib.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getOSName() {
        return Build.PRODUCT;
    }

    /**
     * 系统版本号(17)
     * @return
     */
    public static int getOSVersionCode() {
        return Build.VERSION.SDK_INT;
    }


    /**
     * App版本名
     * @return
     */
    public static String getAppVersionName() {
        initPackageInfo();

        if(mPackageInfo != null) {
            return mPackageInfo.versionName;
        }

        return null;
    }

    /**
     * App版本号
     * @return
     */
    public static int getAppVersionCode() {
        initPackageInfo();

        if(mPackageInfo != null) {
            return mPackageInfo.versionCode;
        }

        return 0;
    }

    public static String getAppChannel() {
        initPackageInfo();

        if(mApplicationInfo == null) {
            try {
                mApplicationInfo = mPackageManager.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (mApplicationInfo != null && mApplicationInfo.metaData != null) {
            return mApplicationInfo.metaData.getString("UMENG_CHANNEL");
        }

        return null;
    }

    public static String getPackageName() {
        initPackageInfo();

        if(mPackageInfo != null) {
            return mPackageInfo.packageName;
        }

        return null;
    }

    /**
     * 是否透明底栏
     * @return
     */
    public static boolean isTranslucentNavigation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && !DeviceUtils.hasPermanentMenuKey()) {
            return true;
        }

        return false;
    }

    /**
     * 判断是否有物理按键
     * @return
     */
    public static boolean hasPermanentMenuKey() {
        return ViewConfiguration.get(NightQAppLib.getAppContext())
                .hasPermanentMenuKey();
    }

    /**
     * 获得SD卡存储大小
     * @return
     */
    public static long getSDCardTotalSize() {
        String sDcString = Environment.getExternalStorageState();
        if (sDcString.equals(android.os.Environment.MEDIA_MOUNTED)) {
            // 取得sdcard文件路径
            File pathFile = android.os.Environment.getExternalStorageDirectory();
            StatFs statfs = new android.os.StatFs(pathFile.getPath());
            // 获取SDCard上BLOCK总数
            long nTotalBlocks;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                nTotalBlocks = statfs.getBlockCountLong();
            } else {
                nTotalBlocks = statfs.getBlockCount();
            }
            // 获取SDCard上每个block的SIZE
            long nBlocSize;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                nBlocSize = statfs.getBlockSizeLong();
            } else {
                nBlocSize = statfs.getBlockSize();
            }
            // 计算SDCard 总容量大小MB
            long nSDTotalSize = nTotalBlocks * nBlocSize / 1024 / 1024;
            return nSDTotalSize;
        } else {
            return 0;
        }
    }

    /**
     * 获得SD卡可用存储空间
     * @return
     */
    public static long getSDCardFreeSize() {
        String sDcString = Environment.getExternalStorageState();
        if (sDcString.equals(android.os.Environment.MEDIA_MOUNTED)) {
            // 取得sdcard文件路径
            File pathFile = android.os.Environment.getExternalStorageDirectory();
            StatFs statfs = new android.os.StatFs(pathFile.getPath());
            // 获取可供程序使用的Block的数量
            long nAvailaBlock;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                nAvailaBlock = statfs.getAvailableBlocksLong();
            } else {
                nAvailaBlock = statfs.getAvailableBlocks();
            }
            // 获取SDCard上每个block的SIZE
            long nBlocSize;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                nBlocSize = statfs.getBlockSizeLong();
            } else {
                nBlocSize = statfs.getBlockSize();
            }
            long nSDFreeSize = nAvailaBlock * nBlocSize / 1024 / 1024;
            return nSDFreeSize;
        } else {
            return 0;
        }
    }

    public static boolean isSamsung() {
        if(DeviceUtils.getManufacturer().equals("samsung")) {
            return true;
        }

        return false;
    }

    public static boolean isMeizu() {
        if(DeviceUtils.getManufacturer().equals("Meizu")) {
            return true;
        }

        return false;
    }

    public static boolean isYawaySony() {
        if(DeviceUtils.getManufacturer().equals("Sony") && DeviceUtils.getDeviceName().equals("D5803")) {
            return true;
        }

        return false;
    }

    public static boolean isNote2() {
        if(isSamsung() && DeviceUtils.getDeviceName().equals("n7102")) {
            return true;
        }

        return false;
    }

    /**
     * 大于等于5.0 支持statusBar自定义颜色
     * @return
     */
    public static boolean isUpAsLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 大于4.0 支持statusBar透明
     * @return
     */
    public static boolean isUpAsKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }
}
