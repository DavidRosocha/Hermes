package utils;

import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;

import android.graphics.BitmapFactory;
import android.util.Base64;
public class ImageUtil {

    public static String encodeImageAsByteString(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encodedImage;
    }

    public static Bitmap decodeImageToBitmap(String byteString) {
        byte[] imageBytes = Base64.decode(byteString,Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0 , imageBytes.length);
        return decodedImage;
    }


}
