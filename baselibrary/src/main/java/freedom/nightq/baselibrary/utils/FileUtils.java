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

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import freedom.nightq.baselibrary.NightQAppLib;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.Checksum;


/**
 * Utils for dealing with files.
 *
 * @author Markus
 */
public class FileUtils {

    public static String readCharsFromAssetsFile(String fileName) {
        try {
            InputStream is = NightQAppLib.getAppContext().getResources().getAssets().open(fileName);
            Reader reader = new InputStreamReader(is);
            return IoUtils.readAllCharsAndClose(reader);
        } catch (Exception e) {}
        return null;
    }

    public static void writeBitmap(File file, Bitmap bmp){
        FileOutputStream os = null;
        try {
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            os = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, os);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
            final File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
            file.renameTo(to);
            to.delete();
        } finally {
            IoUtils.safeClose(os);
            if(bmp != null) {
                bmp.recycle();
                bmp = null;
            }
        }
    }

    public static void writeBitmapJPEG(File file, Bitmap bmp){
        FileOutputStream os = null;
        try {
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            os = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
            final File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
            file.renameTo(to);
            to.delete();
        } finally {
            IoUtils.safeClose(os);
            if(bmp != null) {
                bmp.recycle();
                bmp = null;
            }
        }
    }

    /**
     * 返回非空才是正确。
     * @param outputStream
     * @param filename
     * @return
     * @throws OutOfMemoryError
     */
    public static String saveStreamToFile(ByteArrayOutputStream outputStream,
                                          String filename) throws OutOfMemoryError {
        if (filename != null) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(filename + ".tmp", false);
                if (fos != null) {
                    fos.write(outputStream.toByteArray());
                    fos.close();
                    outputStream.close();
                }
                if (!moveFile(filename + ".tmp", filename)) {
                    filename = "";
                }
            } catch (IOException ex) {
                filename = "";
            } catch (OutOfMemoryError ex) {
                filename = "";
            } finally {
                IoUtils.safeClose(fos);
                IoUtils.safeClose(outputStream);
            }
        }
        return filename;
    }

    public static byte[] readBytes(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        return IoUtils.readAllBytesAndClose(is);
    }

    public static void writeBytes(File file, byte[] content) throws IOException {
        OutputStream out = new FileOutputStream(file);
        try {
            out.write(content);
        } finally {
            IoUtils.safeClose(out);
        }
    }

    public static String readUtf8(File file) throws IOException {
        return readChars(file, "UTF-8");
    }

    public static String readChars(File file, String charset) throws IOException {
        Reader reader = new InputStreamReader(new FileInputStream(file), charset);
        return IoUtils.readAllCharsAndClose(reader);
    }

    public static void writeUtf8(File file, CharSequence text) throws IOException {
        writeChars(file, "UTF-8", text);
    }

    public static void writeChars(File file, String charset, CharSequence text) throws IOException {
        Writer writer = new OutputStreamWriter(new FileOutputStream(file), charset);
        IoUtils.writeAllCharsAndClose(writer, text);
    }

    /** Copies a file to another location. */
    public static void copyFile(File from, File to) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(from));
        try {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(to));
            try {
                IoUtils.copyAllBytes(in, out);
            } finally {
                IoUtils.safeClose(out);
            }
        } finally {
            IoUtils.safeClose(in);
        }
    }

    /**
     * 移动单个文件
     *
     * @param oldPath
     *            String 原文件路径 如：c:/fqf.txt
     * @param newPath
     *            String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static boolean moveFile(String oldPath, String newPath) {
        try {
            copyFile(new File(oldPath), new File(newPath));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /** Copies a file to another location. */
    public static void copyFile(String fromFilename, String toFilename) throws IOException {
        copyFile(new File(fromFilename), new File(toFilename));
    }

    /** To read an object in a quick & dirty way. Prepare to handle failures when object serialization changes! */
    public static Object readObject(File file) throws IOException,
            ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(file);
        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(fileIn));
        try {
            return in.readObject();
        } finally {
            IoUtils.safeClose(in);
        }
    }

    /** To store an object in a quick & dirty way. */
    public static void writeObject(File file, Object object) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(fileOut));
        try {
            out.writeObject(object);
            out.flush();
            // Force sync
            fileOut.getFD().sync();
        } finally {
            IoUtils.safeClose(out);
        }
    }

    /** @return MD5 digest (32 characters). */
    public static String getMd5(File file) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            return IoUtils.getMd5(in);
        } finally {
            IoUtils.safeClose(in);
        }
    }

    /** @return SHA-1 digest (40 characters). */
    public static String getSha1(File file) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            return IoUtils.getSha1(in);
        } finally {
            IoUtils.safeClose(in);
        }
    }

    public static void updateChecksum(File file, Checksum checksum) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            IoUtils.updateChecksum(in, checksum);
        } finally {
            IoUtils.safeClose(in);
        }
    }

    public static String getMimeType(String fileUrl) {
        //LogHelper.v("file.getPath()", fileUrl);
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(fileUrl);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        //LogHelper.v("file.type()", type == null ? "none" : type);
        return type == null ? "" : type;
    }

    /**
     * 计算文件的checksum
     * @return
     */
    public static String computeMd5Hash(String path) {
        FileInputStream fis = null;
        String md5Hash = null;
        try {
            fis = new FileInputStream(path);
            md5Hash = IoUtils.getMd5(fis);
            fis.close();
        } catch (Exception e) {

        }
        return md5Hash;
    }

    /**
     * 判断文件是否存在，且长度大于0
     * @return
     */
    public static boolean isFileExist (String path) {
        //上传照片视频之前判断文件都还存在
        if (!TextUtils.isEmpty(path)) {
            File tmp = new File(path);
            if (tmp.exists() && tmp.length() > 0) {
                return true;
            }
        }
        return false;
    }

    public static String getFileFormatSize(long fileSize) {
        return getFileFormatSize(fileSize, false);
    }

    public static String getFileFormatSize(long fileSize, boolean onlyNum) {
        DecimalFormat fnum = new DecimalFormat("##0.00");
//        if(fileSize < 1024) {
//            return fileSize + " Byte";
//        } else if(fileSize < 1024 * 1024) {
//            return fnum.format(fileSize / 1024.0f) + " KB";
//        } else
        if(fileSize < 1024 * 1024 * 1024) {
            return fnum.format(fileSize / 1024.0f / 1024.0f) + (onlyNum ? "" : " MB");
        } else if(fileSize < 1024 * 1024 * 1024 * 1024) {
            return fnum.format(fileSize / 1024.0f / 1024.0f / 1024.0f) + (onlyNum ? "" : " G");
        }

        return "0 MB";
    }

    public static long folderSize(File directory) {
        long length = 0;
        if (directory.listFiles() != null) {
            for (File file : directory.listFiles()) {
                if (file.isFile())
                    length += file.length();
                else
                    length += folderSize(file);
            }
        }
        return length;
    }

    public static boolean deleteDirectory(File directory) {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
        }
        // 只删除文件，不删除文件夹。
        return true;//(directory.delete());
    }

    public static boolean deletePhoto(String path) {
        try {
            File file = new File(path);
            if(file.exists()) {
                file.delete();
                PhotoUtils.updateGallery(path);
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }
    /**
     * 通过Uri获取文件在本地存储的真实路径
     *
     * @param contentUri
     * @return
     */
    public static String getRealPathFromURI(Uri contentUri) {
        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        String path = null;
        try {
            cursor = NightQAppLib.getAppContext().getContentResolver().query(contentUri, proj, // Which
                    // columns
                    // to
                    // return
                    null, // WHERE clause; which rows to return (all rows)
                    null, // WHERE clause selection arguments (none)
                    null); // Order-by clause (ascending by name)
            if (cursor != null) {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);
            } else {

                String tmp = contentUri.getEncodedPath();
                if (!TextUtils.isEmpty(tmp)) {
                    File file = new File(tmp);
                    if (file.isFile()) {
                        path = tmp;
                    }
                }
            }
        } catch (Exception e) {
            // nothing
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (TextUtils.isEmpty(path)) {
            path = PhotoUtils.getPath(contentUri);
        }
        if (TextUtils.isEmpty(path)) {
            try {
                InputStream input = NightQAppLib.getAppContext().getContentResolver().openInputStream(
                        contentUri);
                Bitmap picture = BitmapFactory.decodeStream(input, null, null);
                path = PhotoUtils.compressBitmap(true, picture, getPhotoCacheFilePath(), 75);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    /**
     * 获取cache下面的缓存图片路径
     * @return
     */
    public static String getPhotoCacheFilePath() {
        return getTmpPhotoCacheFile().getAbsolutePath();
    }

    public static File getPhotoCacheFile() {
        return new File(StorageUtils.getAppTmpCacheDir() + "/TH_" + Calendar.getInstance().getTimeInMillis() + ".jpg");
    }

    public static File getTmpPhotoCacheFile() {
        return new File(StorageUtils.getAppTmpCacheDir() + "tmpTakePhoto.jpg");
    }

    /** 创建一个文件用来保存照片 */
    public static File createAPhotoFile(String filePath){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File mediaStorageDir = new File((TextUtils.isEmpty(filePath) ?
                    PhotoUtils.getSystemCameraTakePhotoFilePath() : filePath));
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    return null;
                }
            }

            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile;
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "Pac_IMG_"+ timeStamp + ".jpg");
            return mediaFile;
        }
        return null;
    }

    public static boolean downloadFile(String urlStr, String filePath) {
        return downloadFile(urlStr, filePath, null);
    }
    public static boolean downloadFile(String urlStr, String filePath, FileDownloadListener listener) {
        File file = new File(filePath);
        if(!file.getParentFile().exists()) {
            file.mkdirs();
        }

        //如果目标文件已经存在，则删除。产生覆盖旧文件的效果
        if(file.exists()) {
            file.delete();
        }

        OutputStream os = null;
        InputStream is = null;
        try {
            // 构造URL
            URL url = new URL(urlStr);
            // 打开连接
            URLConnection con = url.openConnection();
            //获得文件的长度
            int contentLength = con.getContentLength();
            // 输入流
            is = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            long readSize = 0;
            // 输出的文件流
            os = new FileOutputStream(filePath);
            if(listener != null) {
                listener.onFileDownload(0, 0, contentLength);
            }
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
                readSize += len;
                if(listener != null) {
                    listener.onFileDownload((int) (readSize * 100 / contentLength), readSize, contentLength);
                }
            }

            if(listener != null) {
                listener.onFileDownload(100, contentLength, contentLength);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(listener != null) {
                listener.onFileDownload(-1, -1, -1);
            }
            return false;
        } finally {
            if(os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public interface FileDownloadListener {
        void onFileDownload(int process, long currentSize, long totalSize);
    }
}
