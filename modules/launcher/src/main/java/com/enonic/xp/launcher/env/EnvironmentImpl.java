package com.enonic.xp.launcher.env;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Maps;

import com.enonic.xp.launcher.LauncherException;
import com.enonic.xp.launcher.SharedConstants;

final class EnvironmentImpl
    implements Environment, SharedConstants
{
    protected File homeDir;

    protected File installDir;

    @Override
    public File getHomeDir()
    {
        return this.homeDir;
    }

    @Override
    public File getInstallDir()
    {
        return this.installDir;
    }

    public void validate()
    {
        checkDir( "Install", XP_INSTALL_DIR, this.installDir );
        checkDir( "Home", XP_HOME_DIR, this.homeDir );
    }

    private void checkDir( final String message, final String prop, final File dir )
    {
        if ( dir == null )
        {
            throw new LauncherException( "[%s] directory not set. Please set [%s] system property variable.", message, prop );
        }
    }

    @Override
    public Map<String, String> getAsMap()
    {
        final Map<String, String> map = Maps.newHashMap();
        map.put( XP_HOME_DIR, this.homeDir.getAbsolutePath() );
        map.put( XP_INSTALL_DIR, this.installDir.getAbsolutePath() );
        return map;
    }

    @Override
    public Properties getAsProperties()
    {
        final Properties props = new Properties();
        props.putAll( getAsMap() );
        return props;
    }
}
