package freedom.nightq.baselibrary.utils;

import android.text.TextUtils;

import java.io.File;
import java.net.URI;

import retrofit.RetrofitError;
import retrofit.mime.TypedFile;

/**
 * Created by Nightq on 14-6-7.
 */
public class ServerHelper {
    public static final String HTTP_PUT = "PUT";
    public static final String HTTP_DELETE = "DELETE";

    public static TypedFile getTypedFileFromPath (String path) {
        TypedFile typedFile = null;
        if (!TextUtils.isEmpty(path)) {
            File file = null;
            if (path.startsWith("file://")) {
                file = new File(URI.create(path));
            } else {
                file = new File(path);
            }
            if (file.exists()) {
                String type = FileUtils.getMimeType(path);
                if (TextUtils.isEmpty(type)) {
                    type = "image/jpeg";
                }
                typedFile = new TypedFile(type, file);
            }
        }
        return typedFile;
    }

    public static String getContentFromError (RetrofitError error) {
        String content = " ";
        if (error == null) {
            content += " null ";
        } else {
            content += (error.isNetworkError() ? " isNetworkError " : " ");
            content += (error.getMessage());
            content += " ";
            if (error.getResponse() != null) {
                content += " Reason : " + error.getResponse().getReason();
                content += " Status : " + error.getResponse().getStatus();
                content += " Url : " + error.getResponse().getUrl();
            }

            content += error.getCause() != null ? error.getCause().getMessage() : "";
            content +=  " " + error.getLocalizedMessage();
        }
        return content;
    }

}
