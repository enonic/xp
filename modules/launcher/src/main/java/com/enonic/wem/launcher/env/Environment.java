package com.enonic.wem.launcher.env;

import java.io.File;
import java.util.Map;

public interface Environment
{
    public File getHomeDir();

    public File getInstallDir();

    public boolean isDevMode();

    public boolean isConsoleMode();

    public Map<String, String> getAsMap();
}
