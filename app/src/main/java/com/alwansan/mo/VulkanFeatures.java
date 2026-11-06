package com.alwansan.mo;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;

/** PackageManager Vulkan declaration only; no native GPU spoof. */
public final class VulkanFeatures {
    private VulkanFeatures() {}
    public static String inspect(Context context) {
        PackageManager pm=context.getPackageManager();
        FeatureInfo[] ff=pm.getSystemAvailableFeatures();
        if (ff==null) return "لم يرجع النظام ميزات Vulkan";
        StringBuilder b=new StringBuilder();
        for(FeatureInfo f:ff){
            if(f.name==null || !f.name.startsWith("android.hardware.vulkan."))continue;
            if(f.name.equals("android.hardware.vulkan.version")){
                b.append("Vulkan declared API: ").append((f.version>>>22)&1023)
                 .append(".").append((f.version>>>12)&1023).append("\n");
            } else {
                b.append(f.name.replace("android.hardware.vulkan.",""))
                 .append(": ").append(f.version).append("\n");
            }
        }
        if(b.length()==0)return "لم يعلن Android عن Vulkan features";
        return b.append("هذه بيانات النظام المعلنة وليست VkPhysicalDeviceProperties للعبة.").toString();
    }
}
