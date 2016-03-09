package freedom.nightq.baselibrary.utils;


import java.io.File;

/**
 * Created by Nightq on 15/12/24.
 */
public class ProcessPicFileUtil {

    private static ProcessPicFileUtil mInstance;

    private File cacheFile;

    public static ProcessPicFileUtil getInstance () {
        if (mInstance == null) {
            mInstance = new ProcessPicFileUtil();
        }
        return mInstance;
    }

    private ProcessPicFileUtil() {
        cacheFile = new File(StorageUtils.getProcessTmpDirPath());
        if (! cacheFile.exists()){
            if (! cacheFile.mkdirs()){
                LogUtils.e("nightq", "failed to create upload cache directory");
            }
        }
    }


    /**
     * 创建上传的缓存文件
     * 每次会返回不同的值，即使 key 相同。
     * @return
     */
    public synchronized String createCacheFile (String key) {
        return createCacheFile(key, "");
    }

    /**
     * 创建上传的缓存文件
     * 每次会返回不同的值，即使 key 相同。
     * @param addr 文件后缀
     * @return
     */
    private synchronized String createCacheFile (String key, String addr) {
        return new File(cacheFile,
                StringUtils.generateMD5String(key) + addr)
                .getAbsolutePath();
    }

    public synchronized void clearCache () {
        FileUtils.deleteDirectory(cacheFile);
    }

}
