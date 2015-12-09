package com.enonic.xp.launcher.impl.env;

import java.io.File;
import java.util.Map;

public interface Environment
{
    File getHomeDir();

    File getInstallDir();

    void validate();

    Map<String, String> getAsMap();
}
