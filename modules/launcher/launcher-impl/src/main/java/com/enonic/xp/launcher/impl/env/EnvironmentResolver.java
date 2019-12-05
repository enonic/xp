package com.enonic.xp.launcher.impl.env;

import java.io.File;

import com.enonic.xp.launcher.impl.SharedConstants;

import static com.google.common.base.Strings.isNullOrEmpty;

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
        if ( isNullOrEmpty( path ) )
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
        if ( isNullOrEmpty( path ) )
        {
            return installDir != null ? new File( installDir, "home" ) : null;
        }

        return new File( path );
    }

    private String firstOf( final String value1, final String value2 )
    {
        if ( !isNullOrEmpty( value1 ) )
        {
            return value1;
        }

        return value2;
    }
}
