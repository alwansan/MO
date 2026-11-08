package com.alwansan.mo.glesprobe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Owned-app-only private directory round trip. No system settings or external app data.
 * Deliberately measures File.exists separately from native-backed file creation:
 * a container File.exists hook can report false even when the directory is usable.
 */
final class FilePathCheck {
    private static final byte[] PAYLOAD = "MO-P25-PRIVATE-IO".getBytes(StandardCharsets.UTF_8);

    static final class Result {
        final boolean exists;
        final boolean directory;
        final boolean writable;
        final String io;
        final String cleanup;

        Result(boolean exists, boolean directory, boolean writable, String io, String cleanup) {
            this.exists = exists;
            this.directory = directory;
            this.writable = writable;
            this.io = io;
            this.cleanup = cleanup;
        }

        String summary() {
            return "File.exists=" + exists + " / isDirectory=" + directory
                    + " / canWrite=" + writable + " / roundTrip=" + io
                    + " / cleanup=" + cleanup;
        }
    }

    private FilePathCheck() {}

    static Result check(File directory) {
        if (directory == null) return new Result(false, false, false, "FAIL(NULL_DIR)", "N/A");
        boolean exists = directory.exists();
        boolean isDirectory = directory.isDirectory();
        boolean canWrite = directory.canWrite();
        if (!isDirectory)
            return new Result(exists, false, canWrite, "FAIL(NOT_DIRECTORY)", "N/A");

        File created = null;
        String io = "FAIL(UNKNOWN)";
        String cleanup = "N/A";
        try {
            created = File.createTempFile("mo_p25_owned_", ".tmp", directory);
            try (FileOutputStream out = new FileOutputStream(created)) {
                out.write(PAYLOAD);
                out.flush();
            }
            byte[] result = new byte[PAYLOAD.length + 1];
            int length;
            try (FileInputStream in = new FileInputStream(created)) {
                length = in.read(result);
                if (length == PAYLOAD.length && in.read() != -1)
                    length = -1;
            }
            io = length == PAYLOAD.length &&
                    Arrays.equals(PAYLOAD, Arrays.copyOf(result, length))
                    ? "PASS" : "FAIL(CONTENT_MISMATCH)";
        } catch (IOException | RuntimeException ex) {
            io = "FAIL(" + ex.getClass().getSimpleName() + ")";
        } finally {
            if (created != null) {
                try {
                    cleanup = created.delete() ? "PASS" : "FAIL(DELETE)";
                } catch (RuntimeException ex) {
                    cleanup = "FAIL(" + ex.getClass().getSimpleName() + ")";
                }
            }
        }
        return new Result(exists, isDirectory, canWrite, io, cleanup);
    }
}
