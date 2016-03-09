package freedom.nightq.baselibrary.utils;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.FaceDetector;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by H3c on 5/27/15.
 */
public class FaceDetectorUtil {
    private static final int MAX_FACES = 5;

    public static ArrayList<RectF> process(Bitmap bmp) {
        Bitmap tmpBmp = bmp.copy(Bitmap.Config.RGB_565, true);
        FaceDetector faceDet = new FaceDetector(tmpBmp.getWidth(), tmpBmp.getHeight(), MAX_FACES);

        FaceDetector.Face[] faceList = new FaceDetector.Face[MAX_FACES];
        faceDet.findFaces(tmpBmp, faceList);

        ArrayList<RectF> faceRects = new ArrayList();
        for (int i=0; i < faceList.length; i++) {
            FaceDetector.Face face = faceList[i];
            Log.d("FaceDet", "Face [" + face + "]");
            if (face != null) {
                Log.d("FaceDet", "Face ["+i+"] - Confidence ["+face.confidence()+"]");
                PointF pf = new PointF();
                //getMidPoint(PointF point);
                //Sets the position of the mid-point between the eyes.
                face.getMidPoint(pf);
                Log.d("FaceDet", "\t Eyes distance ["+face.eyesDistance()+"] - Face midpoint ["+pf.x+"&"+pf.y+"]");
                RectF r = new RectF();
                r.left = pf.x - face.eyesDistance() / 2;
                r.right = pf.x + face.eyesDistance() / 2;
                r.top = pf.y - face.eyesDistance() / 2;
                r.bottom = pf.y + face.eyesDistance() / 2;
                faceRects.add(r);
            }
        }

        return faceRects;
    }
}
