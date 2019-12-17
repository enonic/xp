package com.enonic.xp.launcher.impl.env;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.enonic.xp.launcher.LauncherException;
import com.enonic.xp.launcher.impl.SharedConstants;

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

    @Override
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
        final Map<String, String> map = new HashMap<>();
        map.put( XP_HOME_DIR, this.homeDir.getAbsolutePath() );
        map.put( XP_INSTALL_DIR, this.installDir.getAbsolutePath() );
        return map;
    }
}
