package com.enonic.xp.launcher.impl.env;

import java.nio.file.Path;
import java.util.Map;

public interface Environment
{
    Path getHomeDir();

    Path getInstallDir();

    Map<String, String> getAsMap();
}
