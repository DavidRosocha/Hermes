package utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ActivityUtils {

    /**
     * Displays a short toast message
     * @param context
     * @param message
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Navigates to the specified activity with optional data
     * @param context
     * @param target
     * @param extras
     */
    public static void navigateToNextActivity(Context context, Class<?> target, IntentExtras extras) {
        Intent intent = new Intent(context,target);
        if (extras != null) {
            extras.addExtras(intent);
        }
        context.startActivity(intent);
    }

    /**
     * Functional interface to provide intent extras
     */
    public interface IntentExtras {
        void addExtras(Intent intent);
    }
}
