package course.android.letgo_307945402_204317770.Utills;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * class used for {@link android.graphics.Bitmap} conversion/ decoding images or writing to storage
 */
public final class Converter {

    private Converter(){}

    public static Bitmap decodeImage(byte[] arr){
        Bitmap img;

        img = BitmapFactory.decodeByteArray(arr, 0,arr.length);

        return img;
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap){
        ByteArrayOutputStream bas;
        byte[] bArray;
        try {
            bas = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,0 ,bas);
            bArray = bas.toByteArray();
            bas.flush();
            bas.close();
        } catch (IOException e) {
            Log.e("IOException ",e.getMessage());
            return null;
        }
        return bArray;

    }

    public static String saveImage(String id, Bitmap bitmap){
        try{
            String name = id+".png";
            String fDir = Environment.getExternalStorageDirectory().toString();
            File file = new File(fDir+"/saved_images");
            file.mkdir();

            File dir = new File(file, name);

            FileOutputStream fos = new FileOutputStream(dir);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();

            return dir.getAbsolutePath();
        }catch (Exception e){
            Log.e("Exception ",e.getMessage());
            return null;
        }

    }
}
