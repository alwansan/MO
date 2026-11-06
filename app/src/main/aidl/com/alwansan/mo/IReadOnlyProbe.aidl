package com.alwansan.mo;
interface IReadOnlyProbe {
    void destroy() = 16777114;
    String getSnapshot(String packageName) = 1;
    String captureLayerSettings() = 2;
    String activateOwnedLayer(String saved) = 3;
    String restoreOwnedLayer(String saved) = 4;
    String diagnoseOwnedLayer() = 5;
}
