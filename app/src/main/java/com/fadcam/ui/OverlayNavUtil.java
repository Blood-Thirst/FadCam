package com.fadcam.ui;

import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.fadcam.R;
import com.fadcam.MainActivity;

/**
 * OverlayNavUtil
 * Centralizes logic for showing & dismissing overlay fragments (settings style) reusing
 * the proven TrashFragment fade-out pattern. This avoids duplicated animation and state bugs.
 */
public final class OverlayNavUtil {
    private OverlayNavUtil() {}

    public static void show(FragmentActivity activity, Fragment fragment, String tag){
        if(activity==null) return;
        View container = activity.findViewById(R.id.overlay_fragment_container);
        if(container==null) return;
        container.setVisibility(View.VISIBLE);
        container.setAlpha(0f);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.overlay_fragment_container, fragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
        container.animate().alpha(1f).setDuration(160).start();
    }

    /** Dismiss overlay with fade and restore ViewPager state like TrashFragment. */
    public static void dismiss(FragmentActivity activity){
        if(activity==null) return;
        View container = activity.findViewById(R.id.overlay_fragment_container);
        if(container==null || container.getVisibility()!=View.VISIBLE){
            return;
        }
        int currentPosition = 0;
        ViewPager2 vp = activity.findViewById(R.id.view_pager);
        if(vp!=null) currentPosition = vp.getCurrentItem();
        container.animate().alpha(0f).setDuration(160).withEndAction(() -> {
            container.setVisibility(View.GONE);
            container.setAlpha(1f);
            if(activity instanceof MainActivity){
                if(activity.getSupportFragmentManager().getBackStackEntryCount()>0){
                    activity.getSupportFragmentManager().popBackStack();
                }
            }
        }).start();
    }
}
