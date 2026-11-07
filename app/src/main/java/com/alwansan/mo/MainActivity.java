package com.alwansan.mo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import rikka.shizuku.Shizuku;

/**
 * MO P1: read-only graphics & package identity scanner with Shizuku UID verification.
 * No root, no game patching and no system graphics-property writes.
 */
public final class MainActivity extends Activity {
    private static final int SHIZUKU_PERMISSION_REQUEST = 4007;
    private static final int COLOR_TEXT = Color.rgb(237, 243, 255);
    private static final int COLOR_MUTED = Color.rgb(162, 180, 208);
    private static final int COLOR_ACCENT = Color.rgb(162, 188, 255);
    private static final int COLOR_CARD = Color.rgb(28, 39, 60);

    private TextView shizukuOutput;
    private TextView deviceOutput;
    private TextView gpuOutput;
    private TextView gameOutput;
    private TextView vulkanOutput;
    private TextView nativeVulkanOutput;
    private TextView shellOutput;
    private TextView layerOutput;
    private IReadOnlyProbe layerService;
    private boolean layerBusy = false;
    private int pendingLayerAction = 0; // 1=enable; 2=restore; 3=diagnose; 4=owned cleanup
    private static final String LAYER_PREFS = "mo_layer_backups";
    private static final String LAYER_BACKUP = "gpu_debug_original";
    private static final String LAYER_RECOVERY = "mo_owned_precleanup_snapshot";
    private static final String LAYER_BACKUP_ARCHIVE = "previous_backup_archived";
    private boolean probeBound = false;
    private boolean probeConnecting = false;

    private final Shizuku.UserServiceArgs probeArgs = new Shizuku.UserServiceArgs(
            new ComponentName("com.alwansan.mo", ReadOnlyProbeService.class.getName()))
            .daemon(false).processNameSuffix("probe-readonly").version(4);

    private final ServiceConnection probeConnection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder binder) {
            DiagnosticLog.log("SHIZUKU_SERVICE_CONNECTED", "binderPresent=" + (binder != null));
            probeBound = true;
            probeConnecting = false;
            if (binder == null || !binder.pingBinder()) {
                shellOutput.setText("خدمة القراءة غير متاحة");
                return;
            }
            IReadOnlyProbe service = IReadOnlyProbe.Stub.asInterface(binder);
            layerService = service;
            if (pendingLayerAction != 0) {
                int action = pendingLayerAction;
                pendingLayerAction = 0;
                runLayerAction(action);
            }
            shellOutput.setText("فحص إعدادات Outlanders عبر Shizuku...");
            new Thread(() -> {
                String result;
                try { result = service.getSnapshot("com.tencent.mhadv"); }
                catch (RemoteException | RuntimeException e) {
                    result = "تعذّرت القراءة: " + e.getClass().getSimpleName();
                }
                final String output = result;
                DiagnosticLog.log("SHIZUKU_SYSTEM_SNAPSHOT", output);
                runOnUiThread(() -> { if (!isFinishing() && !isDestroyed()) shellOutput.setText(output); });
            }, "MO-ReadOnly-Shell").start();
        }
        @Override public void onServiceDisconnected(ComponentName name) {
            DiagnosticLog.log("SHIZUKU_SERVICE_DISCONNECTED", "Service connection lost");
            probeBound = false;
            probeConnecting = false;
            layerService = null;
            layerBusy = false;
            pendingLayerAction = 0;
            if (shellOutput != null) shellOutput.setText("خدمة Shizuku غير متصلة؛ لم نغيّر شيئاً");
        }
    };

    private final Shizuku.OnBinderReceivedListener receivedListener =
            () -> runOnUiThread(this::refreshShizuku);
    private final Shizuku.OnBinderDeadListener deadListener =
            () -> runOnUiThread(this::refreshShizuku);
    private final Shizuku.OnRequestPermissionResultListener permissionListener =
            (requestCode, grantResult) -> {
                if (requestCode == SHIZUKU_PERMISSION_REQUEST) {
                    runOnUiThread(this::refreshShizuku);
                }
            };

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        DiagnosticLog.log("MAIN_OPEN", "MainActivity created; Shizuku probe optional");
        createInterface();
        refreshShizuku();
        refreshDevice();
        gameOutput.setText(GameInventory.inspect(this));
        refreshGraphics();
        vulkanOutput.setText(VulkanFeatures.inspect(this));
        refreshNativeVulkan();

        try {
            Shizuku.addBinderReceivedListenerSticky(receivedListener);
            Shizuku.addBinderDeadListener(deadListener);
            Shizuku.addRequestPermissionResultListener(permissionListener);
        } catch (RuntimeException e) {
            shizukuOutput.setText("Shizuku API غير متاح: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        if (probeBound || probeConnecting) {
            try { Shizuku.unbindUserService(probeArgs, probeConnection, true); }
            catch (RuntimeException ignored) { /* server may have stopped */ }
            probeBound = false;
            probeConnecting = false;
        }
        try {
            Shizuku.removeBinderReceivedListener(receivedListener);
            Shizuku.removeBinderDeadListener(deadListener);
            Shizuku.removeRequestPermissionResultListener(permissionListener);
        } catch (RuntimeException ignored) {
            // Provider may not be running; no privileged action.
        }
        layerService = null;
        DiagnosticLog.log("MAIN_DESTROY", "Shizuku service unbound");
        super.onDestroy();
    }

    private void createInterface() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(16, 23, 37));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(28), dp(18), dp(32));
        scroll.addView(root);
        setContentView(scroll);

        TextView heading = text("MO  /  Graphics Probe", 26, COLOR_TEXT, true);
        root.addView(heading);

        TextView subtitle = text(
                "النسخة التجريبية " + BuildConfig.VERSION_NAME + " — فحص هوية GPU وCPU والألعاب عبر Shizuku بدون روت.",
                14, COLOR_MUTED, false);
        subtitle.setPadding(0, dp(6), 0, dp(18));
        root.addView(subtitle);

        // One tap collects the current MO diagnostic panels and writes the
        // complete available local event history to Download/log.txt.
        DiagnosticLogUi.attach(this, root, this::recordUiSnapshotForLog);

        LinearLayout shizukuCard = card(root, "01  /  SHIZUKU", 
                "فحص خدمة Shizuku والصلاحية الفعلية. لن نطلب روت.");
        shizukuOutput = text("جارٍ الفحص...", 14, COLOR_TEXT, false);
        shizukuCard.addView(shizukuOutput);
        shizukuCard.addView(action("السماح لـMO باستعمال Shizuku", this::requestShizuku));

        LinearLayout deviceCard = card(root, "02  /  DEVICE IDENTITY",
                "الهوية الحقيقية للجهاز كما تقرؤها عملية MO.");
        deviceOutput = text("جارٍ الفحص...", 14, COLOR_TEXT, false);
        deviceCard.addView(deviceOutput);
        deviceCard.addView(action("إعادة فحص الجهاز", this::refreshDevice));

        LinearLayout gpuCard = card(root, "03  /  OPENGL ES",
                "ننشئ EGL context صغيرًا ونقرأ GL_VENDOR وGL_RENDERER مباشرة.");
        gpuOutput = text("جارٍ فحص GPU...", 14, COLOR_TEXT, false);
        gpuOutput.setTextIsSelectable(true);
        gpuCard.addView(gpuOutput);
        gpuCard.addView(action("إعادة فحص GPU", this::refreshGraphics));

        LinearLayout vkCard = card(root, "03B  /  VULKAN SUPPORT",
                "معلومات Vulkan التي يعلنها النظام؛ لا تعني تغيير تعريف GPU داخل اللعبة.");
        vulkanOutput = text("فحص Vulkan...", 14, COLOR_TEXT, false);
        vkCard.addView(vulkanOutput);
        vkCard.addView(action("تحديث دعم Vulkan", () -> vulkanOutput.setText(VulkanFeatures.inspect(this))));

        LinearLayout nativeVkCard = card(root, "03C  /  VULKAN NATIVE GPU",
                "قراءة فعلية من vkGetPhysicalDeviceProperties داخل MO، وليست عملية اللعبة.");
        nativeVulkanOutput = text("جارٍ فحص Vulkan...", 14, COLOR_TEXT, false);
        nativeVulkanOutput.setTextIsSelectable(true);
        nativeVkCard.addView(nativeVulkanOutput);
        nativeVkCard.addView(action("إعادة فحص Vulkan GPU", this::refreshNativeVulkan));

        LinearLayout gameCard = card(root, "04  /  GAME TARGETS",
                "فحص الحزم والإصدارات وعلامات السماح الرسمية بطبقات GLES دون فتح ملفات الألعاب.");
        gameOutput = text("جارٍ الفحص...", 14, COLOR_TEXT, false);
        gameCard.addView(gameOutput);
        gameCard.addView(action("إعادة فحص الألعاب", 
                () -> gameOutput.setText(GameInventory.inspect(this))));

        LinearLayout shellCard = card(root, "05  /  SHIZUKU SYSTEM INSPECTOR",
                "قراءة فقط: Game Mode وANGLE وخصائص النظام. لا توجد أوامر كتابة.");
        shellOutput = text("اضغط فحص إعدادات Outlanders عبر Shizuku.", 14, COLOR_TEXT, false);
        shellOutput.setTextIsSelectable(true);
        shellCard.addView(shellOutput);
        shellCard.addView(action("فحص Game Mode وANGLE", this::readSystemSettings));

        LinearLayout layerCard = card(root, "06  /  OWNED GLES LAYER TEST",
                "تجربة على تطبيق MO منفصل فقط. تحفظ الإعدادات القديمة قبل التفعيل. لا تمس Outlanders.");
        layerOutput = text(layerPrefs().contains(LAYER_BACKUP)
                ? "يوجد إعداد محفوظ بحاجة للاستعادة. لا تحذف MO قبل الاستعادة!"
                : "ثبّت تطبيق الاختبار المنفصل ثم فعّل تجربة GLES اختياريًا.",
                14, COLOR_TEXT, false);
        layerOutput.setTextIsSelectable(true);
        layerCard.addView(layerOutput);
        layerCard.addView(action("تفعيل تجربة GLES للتطبيق المملوك لـMO", this::confirmLayerEnable));
        layerCard.addView(action("استعادة إعدادات GPU Debug القديمة", this::confirmLayerRestore));
        layerCard.addView(action("تنظيف طبقة MO العالقة (عند فقدان الأصل)", this::confirmOwnedCleanup));
        layerCard.addView(action("فتح MO Layer Test", this::openOwnedProbe));
        layerCard.addView(action("تشخيص GLES (قراءة فقط)", () -> requestLayerAction(3)));

        LinearLayout reportCard = card(root, "07  /  TEST REPORT",
                "نسخ تشخيص خالٍ من معرفات الحساب والموقع؛ أنت تقرر متى تشاركه.");
        reportCard.addView(action("نسخ التقرير", this::copyReport));

        TextView note = text("تنبيه: Shizuku عبر ADB ليس روتًا ولا يحقن بيانات " +
                "مزيفة داخل عملية لعبة مغلقة. هذه النسخة تقيس الأساس قبل تجارب الطبقات الرسمية.",
                13, COLOR_MUTED, false);
        note.setPadding(dp(4), dp(16), dp(4), 0);
        root.addView(note);
    }

    private LinearLayout card(LinearLayout parent, String title, String description) {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(16), dp(17), dp(16), dp(16));
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(COLOR_CARD);
        shape.setCornerRadius(dp(18));
        shape.setStroke(dp(1), Color.rgb(48, 65, 91));
        panel.setBackground(shape);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(14);
        parent.addView(panel, params);

        TextView titleView = text(title, 15, COLOR_ACCENT, true);
        panel.addView(titleView);
        TextView descView = text(description, 13, COLOR_MUTED, false);
        descView.setPadding(0, dp(5), 0, dp(13));
        panel.addView(descView);
        return panel;
    }

    private Button action(String label, Runnable callback) {
        Button btn = new Button(this);
        btn.setText(label);
        btn.setAllCaps(false);
        btn.setTextColor(Color.rgb(14, 23, 40));
        btn.setTextSize(13);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_ACCENT);
        bg.setCornerRadius(dp(12));
        btn.setBackground(bg);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(45));
        params.topMargin = dp(13);
        btn.setLayoutParams(params);
        btn.setOnClickListener(v -> callback.run());
        return btn;
    }

    private TextView text(String value, int sp, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextColor(color);
        v.setTextSize(sp);
        v.setGravity(Gravity.START);
        v.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        v.setLineSpacing(dp(3), 1f);
        if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return v;
    }

    private void refreshShizuku() {
        boolean alive;
        int uid = -1;
        int version = -1;
        boolean granted = false;
        try {
            alive = Shizuku.pingBinder();
            if (alive) {
                uid = Shizuku.getUid();
                version = Shizuku.getVersion();
                granted = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
            }
        } catch (RuntimeException | LinkageError e) {
            alive = false;
        }

        PrivilegeGate.Level level = PrivilegeGate.classify(alive, uid);
        shizukuOutput.setText(
                PrivilegeGate.explain(level)
                + "\nBinder: " + (alive ? "متصل" : "غير متصل")
                + "\nShizuku API version: " + (alive ? version : "-")
                + "\nPermission: " + (granted ? "GRANTED" : "NOT GRANTED")
                + "\nلا توجد صلاحيات إضافية مُفعّلة تلقائيًا.");
    }

    private void requestShizuku() {
        try {
            if (!Shizuku.pingBinder()) {
                Toast.makeText(this, "ابدأ خدمة Shizuku من تطبيقها أولًا", Toast.LENGTH_LONG).show();
                refreshShizuku();
                return;
            }
            if (Shizuku.getUid() != PrivilegeGate.ADB_SHELL_UID) {
                Toast.makeText(this, "MO يقبل Shizuku بوضع ADB فقط", Toast.LENGTH_LONG).show();
                refreshShizuku();
                return;
            }
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "الإذن ممنوح بالفعل", Toast.LENGTH_SHORT).show();
                refreshShizuku();
            } else {
                Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST);
            }
        } catch (RuntimeException | LinkageError e) {
            Toast.makeText(this, "تعذّر الاتصال بـShizuku", Toast.LENGTH_LONG).show();
            refreshShizuku();
        }
    }

    private SharedPreferences layerPrefs() {
        return getSharedPreferences(LAYER_PREFS, MODE_PRIVATE);
    }

    private void openOwnedProbe() {
        Intent launch = getPackageManager().getLaunchIntentForPackage(OwnedLayerSettings.TARGET);
        if (launch == null) {
            layerOutput.setText("تطبيق الاختبار غير مثبت. نزّله من GitHub Actions وثبّته أولًا.");
            return;
        }
        startActivity(launch);
    }

    private void confirmLayerEnable() {
        new AlertDialog.Builder(this)
                .setTitle("تفعيل تجربة GLES Layer")
                .setMessage("سيغير MO أربعة مفاتيح GPU Debug عالمية لطبقة تخص التطبيق الاختباري "
                        + "com.alwansan.mo.glesprobe فقط، وليس Outlanders. "
                        + "سيحفظ القيم السابقة ويتيح استعادتها. "
                        + "يجب الضغط على استعادة قبل حذف أو تحديث MO. "
                        + "قد لا يدعم HyperOS التحميل الفعلي للطبقة.")
                .setNegativeButton("إلغاء", null)
                .setPositiveButton("تفعيل الاختبار", (dialog, which) -> requestLayerAction(1))
                .show();
    }

    private void confirmLayerRestore() {
        new AlertDialog.Builder(this)
                .setTitle("استعادة الإعدادات السابقة")
                .setMessage("سيعيد MO القيم الأربع الأصلية، إلا إذا تغيرت من تطبيق آخر.")
                .setNegativeButton("إلغاء", null)
                .setPositiveButton("استعادة", (dialog, which) -> requestLayerAction(2))
                .show();
    }

    private void confirmOwnedCleanup() {
        new AlertDialog.Builder(this)
                .setTitle("تنظيف بقايا طبقة MO فقط")
                .setMessage("استعمل زر الاستعادة العادي أولًا إذا كانت عندك نسخة أصلية سليمة. "
                        + "هذا خيار إنقاذ عند ضياع النسخة أو حفظ نسخة وهي أصلًا مفعّلة. "
                        + "لن يغيّر شيئًا إلا إذا طابقت أربع قيم نظامية بصمة اختبار MO تمامًا. "
                        + "سيحفظ نسخة للحالة الحالية قبل التنظيف، ثم يعطّل GPU Debug إلى 0 "
                        + "ويزيل أسماء طبقة MO. هذه ليست استعادة مضمونة لأي إعدادات قديمة مجهولة. "
                        + "لا يمس اللعبة أو ملفاتها.")
                .setNegativeButton("إلغاء", null)
                .setPositiveButton("تنظيف اختبار MO فقط", (dialog, which) -> requestLayerAction(4))
                .show();
    }

    private void requestLayerAction(int action) {
        if (layerBusy) {
            layerOutput.setText("عملية تفعيل أو استعادة قيد التنفيذ.");
            return;
        }
        if (action == 1 && layerPrefs().contains(LAYER_BACKUP)) {
            layerOutput.setText("توجد نسخة احتياطية سابقة. استعدها أولاً قبل التفعيل.");
            return;
        }
        if (action == 2 && !layerPrefs().contains(LAYER_BACKUP)) {
            layerOutput.setText("لا توجد نسخة احتياطية محفوظة لدى MO.");
            return;
        }
        try {
            if (!Shizuku.pingBinder() || Shizuku.getUid() != PrivilegeGate.ADB_SHELL_UID
                    || Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                layerOutput.setText("تحتاج Shizuku ADB UID 2000 مع إذن GRANTED.");
                return;
            }
            layerBusy = true;
            layerOutput.setText("جارٍ الاتصال بخدمة Shizuku...");
            if (probeBound && layerService != null) {
                runLayerAction(action);
            } else {
                pendingLayerAction = action;
                if (!probeConnecting) {
                    probeConnecting = true;
                    Shizuku.bindUserService(probeArgs, probeConnection);
                }
            }
        } catch (RuntimeException | LinkageError e) {
            layerBusy = false;
            pendingLayerAction = 0;
            layerOutput.setText("فشل الاتصال: " + e.getClass().getSimpleName());
        }
    }

    private void runLayerAction(int action) {
        final IReadOnlyProbe service = layerService;
        new Thread(() -> {
            String outcome;
            String currentLayerSettings = null;
            String currentSystemSnapshot = null;
            try {
                if (service == null) {
                    outcome = "ERROR: Shizuku UserService disconnected";
                } else if (action == 1) {
                    String backup = service.captureLayerSettings();
                    if (backup == null || backup.startsWith("ERROR:")) {
                        outcome = "لم يتم حفظ الإعدادات: " + backup;
                    } else if (!layerPrefs().edit().putString(LAYER_BACKUP, backup).commit()) {
                        outcome = "تعذّر حفظ النسخة الاحتياطية؛ لم يحدث أي تغيير";
                    } else {
                        outcome = service.activateOwnedLayer(backup);
                    }
                } else if (action == 2) {
                    String backup = layerPrefs().getString(LAYER_BACKUP, null);
                    if (backup == null) {
                        outcome = "لا توجد إعدادات محفوظة";
                    } else {
                        outcome = service.restoreOwnedLayer(backup);
                        if (outcome != null && outcome.startsWith("OK:")) {
                            if (!layerPrefs().edit().remove(LAYER_BACKUP).commit()) {
                                outcome += "\nWarning: backup record may remain";
                            }
                        }
                    }
                } else if (action == 4) {
                    String before = service.captureLayerSettings();
                    if (before == null || before.startsWith("ERROR:")) {
                        outcome = "ERROR: unable to snapshot current GPU settings: " + before;
                    } else if (!layerPrefs().edit().putString(LAYER_RECOVERY, before).commit()) {
                        outcome = "ERROR: could not persist recovery snapshot; no settings changed";
                    } else {
                        outcome = service.cleanupStaleOwnedLayer(before);
                        if (outcome != null && outcome.startsWith("OK:")) {
                            SharedPreferences.Editor e = layerPrefs().edit();
                            String old = layerPrefs().getString(LAYER_BACKUP, null);
                            if (old != null) e.putString(LAYER_BACKUP_ARCHIVE, old);
                            e.remove(LAYER_BACKUP);
                            if (!e.commit()) outcome += "\nWARNING: could not clear stale backup flag";
                        }
                    }
                } else {
                    outcome = service.diagnoseOwnedLayer();
                }
                // The existing SHIZUKU SYSTEM panel was cached from before the
                // settings change. Refresh both panels in one read-only pass
                // after every operation; never mistake a stale 1 for a failed
                // cleanup (or a stale 0 for a successful cleanup).
                if (service != null) {
                    try {
                        currentLayerSettings = service.diagnoseOwnedLayer();
                    } catch (Exception refreshError) {
                        currentLayerSettings = "تعذّر تأكيد حالة GPU بعد العملية: "
                                + refreshError.getClass().getSimpleName();
                    }
                    try {
                        currentSystemSnapshot = service.getSnapshot("com.tencent.mhadv");
                    } catch (Exception refreshError) {
                        currentSystemSnapshot = "تعذّر تحديث فحص Shizuku بعد العملية؛ "
                                + "البيانات السابقة أُلغيت. أعد الفحص.";
                    }
                }
            } catch (Exception e) {
                outcome = "فشلت العملية: " + e.getClass().getSimpleName();
            }
            DiagnosticLog.log("OWNED_GPU_ACTION_RESULT", "action=" + action
                    + "; outcome=" + outcome + "; refreshedOwned=" + currentLayerSettings
                    + "; refreshedSystem=" + currentSystemSnapshot);
            final String display = outcome;
            final String verifiedSettings = currentLayerSettings;
            final String refreshedSystem = currentSystemSnapshot;
            runOnUiThread(() -> {
                layerBusy = false;
                if (!isFinishing() && !isDestroyed()) {
                    if (refreshedSystem != null) {
                        shellOutput.setText(refreshedSystem);
                    } else if (action == 1 || action == 2 || action == 4) {
                        // Never leave a potentially misleading pre-write
                        // report visible if a refresh itself failed.
                        shellOutput.setText("قد يكون تقرير Shizuku السابق قديمًا. "
                                + "اضغط فحص Game Mode وANGLE لتحديثه.");
                    }
                    layerOutput.setText(display
                            + (verifiedSettings == null ? "" :
                                "\n\n[READBACK AFTER ACTION]\n" + verifiedSettings)
                            + "\n" + (layerPrefs().contains(LAYER_BACKUP)
                            ? "تنبيه: يجب استعادة إعدادات GPU قبل حذف أو تحديث MO."
                            : "لا توجد تغييرات معلقة مسجلة لدى MO."));
                }
            });
        }, "MO-OwnedLayer-Transaction").start();
    }

    private void readSystemSettings() {
        DiagnosticLog.log("SHIZUKU_READ_REQUEST", "Game Mode / ANGLE / GPU keys (read-only)");
        if (layerBusy) {
            shellOutput.setText("هناك عملية ضبط GPU قيد التنفيذ. أعد المحاولة لاحقًا.");
            return;
        }
        if (probeConnecting) { shellOutput.setText("جارٍ الاتصال بخدمة القراءة..."); return; }
        if (probeBound) {
            try { Shizuku.unbindUserService(probeArgs, probeConnection, true); }
            catch (RuntimeException ignored) { }
            probeBound = false;
        }
        try {
            if (!Shizuku.pingBinder() || Shizuku.getUid() != PrivilegeGate.ADB_SHELL_UID
                    || Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                shellOutput.setText("يتطلب Shizuku متصلاً بوضع ADB وصلاحية GRANTED");
                return;
            }
            if (Shizuku.getVersion() < 10) {
                shellOutput.setText("خدمة الفحص تتطلب Shizuku API 10 أو أعلى");
                return;
            }
            probeConnecting = true;
            shellOutput.setText("جارٍ الاتصال لخدمة الفحص المحدودة...");
            Shizuku.bindUserService(probeArgs, probeConnection);
        } catch (RuntimeException | LinkageError e) {
            probeConnecting = false;
            shellOutput.setText("تعذّر الاتصال: " + e.getClass().getSimpleName());
        }
    }

    private void refreshDevice() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memory = new ActivityManager.MemoryInfo();
        if (manager != null) manager.getMemoryInfo(memory);
        long totalGiB = memory.totalMem / (1024L * 1024L * 1024L);
        String soc = Build.VERSION.SDK_INT >= 31 ? Build.SOC_MODEL : "غير متاح";
        String abis = Build.SUPPORTED_ABIS.length > 0 ? Build.SUPPORTED_ABIS[0] : "-";
        String hz = String.format(Locale.US, "%.1f", 
                getWindowManager().getDefaultDisplay().getRefreshRate());

        deviceOutput.setText("Manufacturer: " + Build.MANUFACTURER
                + "\nModel: " + Build.MODEL
                + "\nDevice: " + Build.DEVICE
                + "\nSoC: " + soc
                + "\nAndroid: " + Build.VERSION.RELEASE + "  (API " + Build.VERSION.SDK_INT + ")"
                + "\nCPU cores (visible): " + Runtime.getRuntime().availableProcessors()
                + "\nABI: " + abis
                + "\nRAM total: ~" + totalGiB + " GiB"
                + "\nCurrent screen refresh: " + hz + " Hz"
                + "\n120Hz شاشة لا يعني 120 FPS داخل الألعاب.");
        DiagnosticLog.log("DEVICE_REPORT", deviceOutput.getText().toString());
    }

    private void refreshGraphics() {
        gpuOutput.setText("جارٍ إنشاء EGL context آمن للقراءة...");
        new Thread(() -> {
            String report = GraphicsProbe.inspect();
            DiagnosticLog.log("GLES_REPORT", report);
            runOnUiThread(() -> {
                if (!isFinishing() && !isDestroyed()) gpuOutput.setText(report);
            });
        }, "MO-GLES-ReadOnly").start();
    }

    private void refreshNativeVulkan() {
        nativeVulkanOutput.setText("جارٍ قراءة VkPhysicalDeviceProperties...");
        new Thread(() -> {
            String result = NativeVulkanProbe.inspect();
            DiagnosticLog.log("VULKAN_REPORT", result);
            runOnUiThread(() -> {
                if (!isFinishing() && !isDestroyed()) nativeVulkanOutput.setText(result);
            });
        }, "MO-Vulkan-Native-ReadOnly").start();
    }

    private void recordUiSnapshotForLog() {
        DiagnosticLog.log("MO_FULL_UI_SNAPSHOT", "MO Graphics Probe v"
                + BuildConfig.VERSION_NAME
                + "\n[SHIZUKU]\n" + safePanel(shizukuOutput)
                + "\n[DEVICE]\n" + safePanel(deviceOutput)
                + "\n[GLES]\n" + safePanel(gpuOutput)
                + "\n[GAMES]\n" + safePanel(gameOutput)
                + "\n[VULKAN SUPPORT]\n" + safePanel(vulkanOutput)
                + "\n[VULKAN NATIVE]\n" + safePanel(nativeVulkanOutput)
                + "\n[SHIZUKU SYSTEM]\n" + safePanel(shellOutput)
                + "\n[OWNED GPU DEBUG]\n" + safePanel(layerOutput));
    }

    private static String safePanel(TextView panel) {
        return panel == null ? "(not initialized)" : panel.getText().toString();
    }

    private void copyReport() {
        DiagnosticLog.log("COPY_REPORT", "Owner requested copy of current MO panels");
        String report = "MO Graphics Probe v" + BuildConfig.VERSION_NAME + "\n"
                + "Local read-only diagnostics\n\n"
                + "[SHIZUKU]\n" + shizukuOutput.getText() + "\n\n"
                + "[DEVICE]\n" + deviceOutput.getText() + "\n\n"
                + "[OPENGL ES]\n" + gpuOutput.getText() + "\n\n"
                + "[GAMES]\n" + gameOutput.getText() + "\n\n"
                + "[VULKAN SUPPORT]\n" + vulkanOutput.getText() + "\n\n"
                + "[SHIZUKU SYSTEM READ-ONLY]\n" + shellOutput.getText() + "\n\n"
                + "[VULKAN NATIVE GPU]\n" + nativeVulkanOutput.getText() + "\n\n"
                + "[OWNED GLES LAYER TEST]\n" + layerOutput.getText() + "\n\n"
                + "No game was modified. No root was used.";
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText("MO graphics report", report));
            Toast.makeText(this, "تم نسخ التقرير", Toast.LENGTH_SHORT).show();
        }
    }

    private int dp(float value) {
        return Math.round(getResources().getDisplayMetrics().density * value);
    }
}
