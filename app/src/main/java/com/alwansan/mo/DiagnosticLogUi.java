package com.alwansan.mo;

import android.app.Activity;
import android.graphics.Color;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Shared visible export controls on every MO-owned test screen. */
public final class DiagnosticLogUi {
    private DiagnosticLogUi() {}

    public static void attach(Activity activity, LinearLayout column) {
        TextView title = new TextView(activity);
        title.setText("MO / COMPLETE APP DIAGNOSTIC LOG");
        title.setTextSize(17f);
        title.setTextColor(Color.WHITE);
        title.setPadding(0, 22, 0, 5);
        column.addView(title);

        TextView privacy = new TextView(activity);
        privacy.setText("يسجل أحداث MO Graphics Probe وقياسات GLES/Vulkan وفحوص Shizuku والرسوميات "
                + "والأخطاء والتوقيت منذ تشغيل التطبيق وعبر جلساته المحفوظة. "
                + "لا يستطيع قراءة logcat الكامل للنظام أو سجلات الألعاب الأخرى. "
                + "راجع محتوى الملف قبل مشاركته، فقد يتضمن تفاصيل أعطال التطبيق.");
        privacy.setTextSize(13f);
        privacy.setTextColor(Color.rgb(178, 196, 227));
        column.addView(privacy);

        TextView status = new TextView(activity);
        status.setTextIsSelectable(true);
        status.setTextSize(14f);
        status.setTextColor(Color.rgb(225, 237, 255));

        Button all = new Button(activity);
        all.setAllCaps(false);
        all.setText("إخراج السجل: نسخه + حفظ Download/log.txt");
        all.setOnClickListener(v -> {
            all.setEnabled(false);
            status.setText("جارٍ تجهيز السجل وكتابته في Download...");
            DiagnosticLogExporter.copyAndExport(activity, message -> {
                if (activity.isFinishing() || activity.isDestroyed()) return;
                status.setText(message);
                all.setEnabled(true);
            });
        });
        column.addView(all);

        Button copy = new Button(activity);
        copy.setAllCaps(false);
        copy.setText("نسخ سجل MO للحافظة فقط");
        copy.setOnClickListener(v -> {
            status.setText("جارٍ نسخ السجل...");
            DiagnosticLogExporter.copyOnly(activity, text -> {
                if (!activity.isFinishing() && !activity.isDestroyed()) status.setText(text);
            });
        });
        column.addView(copy);
        column.addView(status);
    }
}
