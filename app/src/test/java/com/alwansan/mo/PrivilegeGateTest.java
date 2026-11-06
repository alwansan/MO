package com.alwansan.mo;

import org.junit.Test;
import static org.junit.Assert.*;

public class PrivilegeGateTest {
    @Test public void offlineIsNeverPrivileged() {
        assertEquals(PrivilegeGate.Level.OFFLINE, PrivilegeGate.classify(false, 0));
        assertFalse(PrivilegeGate.rootlessShizukuSupported(false, 2000));
    }

    @Test public void adbShellIsAllowedForReadOnlyProbe() {
        assertEquals(PrivilegeGate.Level.ADB_SHELL, PrivilegeGate.classify(true, 2000));
        assertTrue(PrivilegeGate.rootlessShizukuSupported(true, 2000));
    }

    @Test public void rootShizukuIsNotUsed() {
        assertEquals(PrivilegeGate.Level.ROOT_PRESENT_NOT_USED,
                PrivilegeGate.classify(true, 0));
        assertFalse(PrivilegeGate.rootlessShizukuSupported(true, 0));
    }

    @Test public void unexpectedUidIsRejected() {
        assertEquals(PrivilegeGate.Level.OTHER_IDENTITY,
                PrivilegeGate.classify(true, 1000));
        assertFalse(PrivilegeGate.rootlessShizukuSupported(true, 1000));
    }
}
