package course.android.letgo_307945402_204317770.Network;

import android.graphics.Bitmap;

import org.json.JSONObject;

/**
 * NetworkResListener interface
 */
public interface NetworkResListener {
    /**
     * callback method which called when the resources update is started
     */
    public void onPreUpdate(String str);

    public void onPostUpdate(JSONObject res, String table, ResStatus status);

    public void onPostUpdate(Bitmap res, ResStatus status);
}
