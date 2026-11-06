package com.alwansan.mo;
import org.junit.Test;
import static org.junit.Assert.*;
public class ReadOnlyCommandPolicyTest {
    @Test public void strictAllowedTargets(){
        assertTrue(ReadOnlyCommandPolicy.allowedPackage("com.tencent.mhadv"));
        assertFalse(ReadOnlyCommandPolicy.allowedPackage("com.tencent.mhadv;rm -rf /"));
        assertFalse(ReadOnlyCommandPolicy.allowedPackage("com.unlisted"));
    }
    @Test public void allBuiltCommandsAreReads(){
        for(String[] args:ReadOnlyCommandPolicy.commands("com.tencent.mhadv")){
            assertTrue(String.join(" ",args),ReadOnlyCommandPolicy.readOnly(args));
        }
    }
    @Test public void mutationsDenied(){
        assertFalse(ReadOnlyCommandPolicy.readOnly(new String[]{"sh","-c","id"}));
        assertFalse(ReadOnlyCommandPolicy.readOnly(new String[]{"settings","put","global","gpu_debug_app","foo"}));
        assertFalse(ReadOnlyCommandPolicy.readOnly(new String[]{"cmd","game","mode","performance","com.tencent.mhadv"}));
    }
}
