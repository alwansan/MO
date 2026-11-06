package com.alwansan.mo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

/** Read-only inventory; never opens, patches or uninstalls a game's files. */
public final class GameInventory {
    private static final String[][] TARGETS = {
            {"Monster Hunter Outlanders (Global)", "com.tencent.mhadv"},
            {"Monster Hunter Outlanders (Garena)", "com.garena.game.mho"},
            {"PUBG Mobile (Global)", "com.tencent.ig"},
            {"BGMI", "com.pubg.imobile"}
    };

    private GameInventory() {}

    public static String inspect(Context context) {
        PackageManager pm = context.getPackageManager();
        StringBuilder out = new StringBuilder();
        for (String[] target : TARGETS) {
            out.append(target[0]).append("\n  ").append(target[1]).append("\n  ");
            try {
                PackageInfo info;
                if (Build.VERSION.SDK_INT >= 33) {
                    info = pm.getPackageInfo(target[1],
                            PackageManager.PackageInfoFlags.of(0));
                } else {
                    info = pm.getPackageInfo(target[1], 0);
                }
                out.append("مثبت — الإصدار: ")
                        .append(info.versionName != null ? info.versionName : "غير معروف");
                try {
                    ApplicationInfo app;
                    if (Build.VERSION.SDK_INT >= 33) {
                        app = pm.getApplicationInfo(target[1],
                                PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA));
                    } else {
                        app = pm.getApplicationInfo(target[1], PackageManager.GET_META_DATA);
                    }
                    boolean debuggable = (app.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
                    boolean optIn = app.metaData != null
                            && app.metaData.getBoolean("com.android.graphics.injectLayers.enable", false);
                    out.append("\n  Target SDK: ").append(app.targetSdkVersion)
                            .append("\n  Debuggable: ").append(debuggable ? "YES" : "NO")
                            .append("\n  External GLES layer opt-in: ").append(optIn ? "YES" : "NO")
                            .append("\n  Official rootless GLES layer eligibility: ")
                            .append(GameLayerEligibility.classify(
                                    debuggable, optIn, app.targetSdkVersion));
                } catch (PackageManager.NameNotFoundException | SecurityException ignored) {
                    out.append("\n  GLES layer eligibility: unknown (package details inaccessible)");
                }
            } catch (PackageManager.NameNotFoundException e) {
                out.append("غير مثبت أو غير ظاهر");
            }
            out.append("\n\n");
        }
        return out.toString().trim();
    }
}
