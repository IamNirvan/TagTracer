package com.tagtracer.util;

import android.os.Build;

public abstract class BuildInfo {
    public static String getManufacturer() {
        return Build.MANUFACTURER.toLowerCase();
    }
}
