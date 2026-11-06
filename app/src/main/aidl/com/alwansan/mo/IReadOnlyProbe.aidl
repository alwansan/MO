package com.alwansan.mo;
interface IReadOnlyProbe {
    void destroy() = 16777114;
    String getSnapshot(String packageName) = 1;
}
