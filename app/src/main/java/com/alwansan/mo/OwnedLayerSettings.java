package com.alwansan.mo;

import android.system.Os;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * GPU Layer proof is allowed ONLY for the disposable MO-owned test package.
 * Four exact Settings.Global keys are captured (including presence/absence),
 * edited only after explicit user approval, then restored exactly.
 */
public final class OwnedLayerSettings {
    public static final String TARGET = "com.alwansan.mo.glesprobe";
    public static final String LIBRARY = "libMOGlesProofLayer.so";
    public static final String[] KEYS = {
            "enable_gpu_debug_layers",
            "gpu_debug_app",
            "gpu_debug_layers_gles",
            "gpu_debug_layer_app"
    };
    private static final String[] TARGET_VALUES = {"1", TARGET, LIBRARY, TARGET};

    private OwnedLayerSettings() {}

    private static final class Outcome {
        final int code;
        final String value;
        Outcome(int code, String value) { this.code = code; this.value = value; }
    }

    private static Outcome exec(String... arguments) {
        Process process = null;
        ExecutorService reader = Executors.newSingleThreadExecutor();
        try {
            process = new ProcessBuilder(arguments).redirectErrorStream(true).start();
            InputStream stream = process.getInputStream();
            Future<String> text = reader.submit(() -> {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] bytes = new byte[2048];
                int size;
                while ((size = stream.read(bytes)) != -1) {
                    if (output.size() + size > 131072) {
                        throw new IllegalStateException("Command output exceeded safe limit");
                    }
                    output.write(bytes, 0, size);
                }
                return new String(output.toByteArray(), StandardCharsets.UTF_8).trim();
            });
            if (!process.waitFor(5, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                return new Outcome(-1, "Command timeout");
            }
            return new Outcome(process.exitValue(), text.get(2, TimeUnit.SECONDS));
        } catch (Exception e) {
            if (process != null) process.destroyForcibly();
            return new Outcome(-1, e.getClass().getSimpleName());
        } finally {
            reader.shutdownNow();
            if (process != null) process.destroy();
        }
    }

    private static void requireShell() {
        if (Os.getuid() != 2000) throw new IllegalStateException("ADB shell UID 2000 required");
    }

    private static Map<String, String> readCurrent() throws Exception {
        Outcome response = exec("settings", "list", "global");
        if (response.code != 0) throw new IllegalStateException("settings list: " + response.value);
        Map<String, String> values = new HashMap<>();
        for (String line : response.value.split("\n")) {
            int delimiter = line.indexOf('=');
            if (delimiter <= 0) continue;
            String key = line.substring(0, delimiter);
            for (String allowed : KEYS) {
                if (allowed.equals(key)) {
                    values.put(key, line.substring(delimiter + 1));
                }
            }
        }
        return values;
    }

    private static String encode(Map<String, String> values) throws Exception {
        JSONObject root = new JSONObject();
        root.put("format", 1);
        JSONObject entries = new JSONObject();
        for (String key : KEYS) {
            JSONObject entry = new JSONObject();
            entry.put("present", values.containsKey(key));
            if (values.containsKey(key)) entry.put("value", values.get(key));
            entries.put(key, entry);
        }
        root.put("values", entries);
        return root.toString();
    }

    private static Map<String, String> decode(String saved) throws Exception {
        if (saved == null || saved.length() > 16384)
            throw new IllegalArgumentException("Invalid backup length");
        JSONObject object = new JSONObject(saved);
        if (object.getInt("format") != 1)
            throw new IllegalArgumentException("Unknown backup version");
        JSONObject data = object.getJSONObject("values");
        Map<String, String> result = new HashMap<>();
        for (String key : KEYS) {
            JSONObject value = data.getJSONObject(key);
            if (value.getBoolean("present")) {
                String text = value.getString("value");
                if (text.length() > 2048 || text.indexOf('\n') >= 0
                        || text.indexOf('\r') >= 0 || text.indexOf('\0') >= 0) {
                    throw new IllegalArgumentException("Unsafe backed-up value");
                }
                result.put(key, text);
            }
        }
        return result;
    }

    private static boolean equal(Map<String, String> a, Map<String, String> b) {
        for (String key : KEYS) {
            if (a.containsKey(key) != b.containsKey(key)) return false;
            if (a.containsKey(key) && !a.get(key).equals(b.get(key))) return false;
        }
        return true;
    }

    private static String expected(String key) {
        for (int i=0; i<KEYS.length; i++) {
            if (KEYS[i].equals(key)) return TARGET_VALUES[i];
        }
        throw new IllegalArgumentException("Unexpected key");
    }

    private static void write(String key, String value) throws Exception {
        Outcome result = value == null
                ? exec("settings", "delete", "global", key)
                : exec("settings", "put", "global", key, value);
        if (result.code != 0) throw new IllegalStateException("Write failed for " + key);
    }

    private static void restoreInternal(Map<String, String> old) throws Exception {
        for (String key : KEYS) write(key, old.get(key));
        if (!equal(readCurrent(), old)) throw new IllegalStateException("Restore mismatch");
    }

    public static String snapshot() {
        try {
            requireShell();
            return encode(readCurrent());
        } catch (Exception e) {
            return "ERROR: snapshot failed: " + e.getMessage();
        }
    }

    public static String activate(String backup) {
        try {
            requireShell();
            Map<String, String> baseline = decode(backup);
            Map<String, String> actual = readCurrent();
            if (!equal(baseline, actual)) return "ERROR: settings changed since snapshot";
            String previousTarget = actual.get("gpu_debug_app");
            if ("1".equals(actual.get("enable_gpu_debug_layers"))
                    && previousTarget != null && !TARGET.equals(previousTarget)) {
                return "ERROR: GPU debugging active for another application";
            }
            Outcome installed = exec("pm", "path", TARGET);
            if (installed.code != 0 || !installed.value.contains("package:")) {
                return "ERROR: install the MO Layer Test APK first";
            }
            try {
                for (int i = 0; i < KEYS.length; i++) write(KEYS[i], TARGET_VALUES[i]);
                Map<String, String> now = readCurrent();
                for (String key : KEYS) {
                    if (!expected(key).equals(now.get(key))) {
                        throw new IllegalStateException("GPU setting verification failed");
                    }
                }
                return "OK: experimental GLES layer enabled for MO TEST ONLY";
            } catch (Exception e) {
                try {
                    restoreInternal(baseline);
                    return "ERROR: activation failed; previous values restored";
                } catch (Exception rollbackError) {
                    return "ERROR: activation and rollback failed; use MO Restore now";
                }
            }
        } catch (Exception e) {
            return "ERROR: activation refused: " + e.getMessage();
        }
    }

    public static String restore(String backup) {
        try {
            requireShell();
            Map<String, String> baseline = decode(backup);
            Map<String, String> current = readCurrent();
            for (String key : KEYS) {
                boolean isOriginal = current.containsKey(key) == baseline.containsKey(key)
                        && (!baseline.containsKey(key)
                        || baseline.get(key).equals(current.get(key)));
                boolean isOurTest = expected(key).equals(current.get(key));
                if (!isOriginal && !isOurTest) {
                    return "ERROR: external modification to " + key + "; no settings overwritten";
                }
            }
            restoreInternal(baseline);
            return "OK: all original GPU debug values restored";
        } catch (Exception e) {
            return "ERROR: restore failed: " + e.getMessage();
        }
    }
}
