package io.github.tavstal.portallock;

import io.github.tavstal.portallock.models.ConfigField;

public class CommonConfig {

    @ConfigField(order = 1, comment = "Shows more logs than usual. Helps locating errors.")
    public boolean EnableDebugMode;
    @ConfigField(order = 100, comment = "DO NOT TOUCH THIS. This helps handlig config related changes after updates.")
    public int FileVersion;

    public CommonConfig() {
        EnableDebugMode = false;

        FileVersion = 1;
    }
}
