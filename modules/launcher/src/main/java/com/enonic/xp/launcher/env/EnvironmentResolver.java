package com.enonic.xp.launcher.env;

import java.io.File;

import com.google.common.base.Strings;

import com.enonic.xp.launcher.SharedConstants;

public final class EnvironmentResolver
    implements SharedConstants
{
    private final SystemProperties properties;

    public EnvironmentResolver( final SystemProperties properties )
    {
        this.properties = properties;
    }

    public Environment resolve()
    {
        final EnvironmentImpl env = new EnvironmentImpl();
        env.installDir = resolveInstallDir();
        env.homeDir = resolveHomeDir( env.installDir );
        return env;
    }

    private File resolveInstallDir()
    {
        final String path = this.properties.get( XP_INSTALL_DIR );
        if ( Strings.isNullOrEmpty( path ) )
        {
            return null;
        }

        return new File( path );
    }

    private File resolveHomeDir( final File installDir )
    {
        final String propValue = this.properties.get( XP_HOME_DIR );
        final String envValue = this.properties.getEnv( XP_HOME_DIR_ENV );

        final String path = firstOf( propValue, envValue );
        if ( Strings.isNullOrEmpty( path ) )
        {
            return installDir != null ? new File( installDir, "home" ) : null;
        }

        return new File( path );
    }

    private String firstOf( final String value1, final String value2 )
    {
        if ( !Strings.isNullOrEmpty( value1 ) )
        {
            return value1;
        }

        return value2;
    }
}
