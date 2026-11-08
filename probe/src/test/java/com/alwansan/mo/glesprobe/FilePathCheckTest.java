package com.alwansan.mo.glesprobe;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.*;

public final class FilePathCheckTest {
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    @Test public void privateDirectoryRoundTripCleansOnlyOwnedFile() throws Exception {
        File filesDir = temporary.newFolder("owned");
        File neighbor = new File(filesDir, "existing-user-owned.txt");
        assertTrue(neighbor.createNewFile());
        FilePathCheck.Result result = FilePathCheck.check(filesDir);
        assertTrue(result.exists);
        assertTrue(result.directory);
        assertEquals("PASS", result.io);
        assertEquals("PASS", result.cleanup);
        assertTrue("unrelated existing file must survive", neighbor.exists());
        assertEquals("no residual temporary files", 1, filesDir.list().length);
    }

    @Test public void missingPrivateDirectoryIsNotCreatedOrWritten() throws Exception {
        File missing = new File(temporary.getRoot(), "missing");
        FilePathCheck.Result result = FilePathCheck.check(missing);
        assertFalse(result.exists);
        assertFalse(result.directory);
        assertEquals("FAIL(NOT_DIRECTORY)", result.io);
        assertFalse(missing.exists());
    }

    @Test public void nonDirectoryInputIsRefused() throws Exception {
        File file = temporary.newFile("not-a-dir");
        FilePathCheck.Result result = FilePathCheck.check(file);
        assertTrue(result.exists);
        assertFalse(result.directory);
        assertEquals("FAIL(NOT_DIRECTORY)", result.io);
        assertTrue(file.exists());
    }
}
