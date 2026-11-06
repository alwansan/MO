package com.alwansan.mo;

import android.os.RemoteException;
import android.system.Os;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/** Shizuku UserService: fixed read-only allowlist, strictly UID 2000. */
public final class ReadOnlyProbeService extends IReadOnlyProbe.Stub {
    public ReadOnlyProbeService() {}
    @Override public void destroy() { System.exit(0); }
    @Override public String getSnapshot(String pkg) throws RemoteException {
        int uid = Os.getuid();
        if (uid != 2000) return "مرفوض: UID " + uid + " ليس ADB Shell 2000";
        if (!ReadOnlyCommandPolicy.allowedPackage(pkg)) return "Package not allowed";
        StringBuilder b = new StringBuilder("Shizuku UserService UID=" + uid + "\n"
                + "Target: " + pkg + "\nلا توجد أي أوامر كتابة/حقن\n\n");
        for (String[] args : ReadOnlyCommandPolicy.commands(pkg)) {
            if (!ReadOnlyCommandPolicy.readOnly(args)) continue;
            b.append("$ ").append(String.join(" ", args)).append("\n");
            b.append(run(args)).append("\n\n");
        }
        b.append("هذه قيم إعدادات النظام، وليست القيم التي تقرؤها اللعبة بالضرورة.");
        return b.toString();
    }
    @Override public String captureLayerSettings() throws RemoteException {
        return OwnedLayerSettings.snapshot();
    }

    @Override public String activateOwnedLayer(String saved) throws RemoteException {
        return OwnedLayerSettings.activate(saved);
    }

    @Override public String restoreOwnedLayer(String saved) throws RemoteException {
        return OwnedLayerSettings.restore(saved);
    }

    @Override public String diagnoseOwnedLayer() throws RemoteException {
        return OwnedLayerSettings.diagnose();
    }

    private static String run(String[] args) {
        Process process = null;
        try {
            process = new ProcessBuilder(args).redirectErrorStream(true).start();
            if (!process.waitFor(4, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                return "[time limit]";
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (InputStream input = process.getInputStream()) {
                byte[] bytes = new byte[1024];
                int n;
                while (buffer.size() < 4096 && (n = input.read(bytes)) != -1) {
                    buffer.write(bytes, 0, Math.min(n, 4096-buffer.size()));
                }
            }
            String result = new String(buffer.toByteArray(), StandardCharsets.UTF_8).trim();
            if (result.isEmpty()) result = "(empty)";
            if (process.exitValue() != 0) result += " (exit=" + process.exitValue() + ")";
            return result;
        } catch (Exception e) {
            return "تعذّرت القراءة: " + e.getClass().getSimpleName();
        } finally { if (process != null) process.destroy(); }
    }
}
