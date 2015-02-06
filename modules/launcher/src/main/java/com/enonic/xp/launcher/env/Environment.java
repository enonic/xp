package com.enonic.xp.launcher.env;

import java.io.File;
import java.util.Map;
import java.util.Properties;

public interface Environment
{
    public File getHomeDir();

    public File getInstallDir();

    public void validate();

    public Map<String, String> getAsMap();

    public Properties getAsProperties();
}
