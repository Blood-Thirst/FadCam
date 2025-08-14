package com.fadcam.shortcuts;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Manages persisted customization for app shortcuts: custom label and icon per shortcut id.
 */
public class ShortcutsPreferences {

    private static final String PREFS = "shortcuts_prefs";

    private final SharedPreferences sp;
    private final Context ctx;

    public ShortcutsPreferences(@NonNull Context context) {
        this.ctx = context.getApplicationContext();
        this.sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void setCustomLabel(@NonNull String shortcutId, @Nullable String label) {
        SharedPreferences.Editor e = sp.edit();
        String key = keyLabel(shortcutId);
        if (label == null || label.trim().isEmpty()) {
            e.remove(key);
        } else {
            e.putString(key, label.trim());
        }
        e.apply();
    }

    @Nullable
    public String getCustomLabel(@NonNull String shortcutId) {
        return sp.getString(keyLabel(shortcutId), null);
    }

    public void clearCustomLabel(@NonNull String shortcutId) {
        sp.edit().remove(keyLabel(shortcutId)).apply();
    }

    /**
     * Saves a square icon bitmap for the shortcut and returns the file path.
     */
    @Nullable
    public String setCustomIconFromBitmap(@NonNull String shortcutId, @NonNull Bitmap bitmap) {
        Bitmap squared = toSquare(bitmap);
        // 192x192 px target for adaptive icon source
        Bitmap scaled = Bitmap.createScaledBitmap(squared, 192, 192, true);
        File out = new File(getIconsDir(), fileName(shortcutId));
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(out);
            scaled.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            sp.edit().putString(keyIcon(shortcutId), out.getAbsolutePath()).apply();
            return out.getAbsolutePath();
        } catch (IOException e) {
            // swallow; caller may fallback to default icon
            return null;
        } finally {
            try { if (fos != null) fos.close(); } catch (IOException ignored) {}
        }
    }

    /**
     * Convenience to decode and save from a content Uri.
     */
    @Nullable
    public String setCustomIconFromUri(@NonNull String shortcutId, @NonNull Uri imageUri) {
        try (InputStream is = ctx.getContentResolver().openInputStream(imageUri)) {
            if (is == null) return null;
            android.graphics.Bitmap bmp = android.graphics.BitmapFactory.decodeStream(is);
            if (bmp == null) return null;
            return setCustomIconFromBitmap(shortcutId, bmp);
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public String getCustomIconPath(@NonNull String shortcutId) {
        return sp.getString(keyIcon(shortcutId), null);
    }

    public void clearCustomIcon(@NonNull String shortcutId) {
        String path = getCustomIconPath(shortcutId);
        if (path != null) {
            new File(path).delete();
        }
        sp.edit().remove(keyIcon(shortcutId)).apply();
    }

    public void reset(@NonNull String shortcutId) {
        clearCustomLabel(shortcutId);
        clearCustomIcon(shortcutId);
    }

    private File getIconsDir() {
        File dir = new File(ctx.getFilesDir(), "shortcut_icons");
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();
        return dir;
    }

    private static String keyLabel(String id) { return "label_" + id; }
    private static String keyIcon(String id) { return "icon_" + id; }
    private static String fileName(String id) { return id + ".png"; }

    private static Bitmap toSquare(Bitmap src) {
        if (src.getWidth() == src.getHeight()) return src;
        int size = Math.min(src.getWidth(), src.getHeight());
        int x = (src.getWidth() - size) / 2;
        int y = (src.getHeight() - size) / 2;
        try {
            return Bitmap.createBitmap(src, x, y, size, size);
        } catch (Exception e) {
            // Fallback: draw centered into square
            Bitmap out = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(out);
            c.drawColor(Color.TRANSPARENT);
            Rect dst = new Rect(0,0,size,size);
            c.drawBitmap(src, null, dst, new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            return out;
        }
    }
}
