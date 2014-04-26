package com.enonic.wem.launcher.home;

import java.io.File;

import com.google.common.base.Strings;

import com.enonic.wem.launcher.SharedConstants;
import com.enonic.wem.launcher.util.SystemProperties;

public final class HomeResolver
    implements SharedConstants
{
    private final SystemProperties systemProperties;

    public HomeResolver( final SystemProperties systemProperties )
    {
        this.systemProperties = systemProperties;
    }

    public HomeDir resolve()
    {
        return new HomeDir( validatePath( resolvePath() ) );
    }

    private String resolvePath()
    {
        String path = this.systemProperties.get( HOME_PROP );
        if ( !Strings.isNullOrEmpty( path ) )
        {
            return path;
        }

        path = this.systemProperties.getEnv( HOME_ENV );
        if ( !Strings.isNullOrEmpty( path ) )
        {
            return path;
        }

        throw new IllegalArgumentException(
            String.format( "Home directory not set. Please set either [%s] system property or [%s] environment variable.", HOME_PROP,
                           HOME_ENV )
        );
    }

    private File validatePath( final String path )
    {
        final File dir = new File( path ).getAbsoluteFile();
        if ( !dir.exists() || !dir.isDirectory() )
        {
            throw new IllegalArgumentException( String.format( "Invalid home directory: [%s] is not a directory", path ) );
        }

        return dir;
    }
}
