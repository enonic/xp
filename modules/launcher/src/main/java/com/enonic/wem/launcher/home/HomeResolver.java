package com.enonic.wem.launcher.home;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import com.google.common.base.Strings;

import com.enonic.wem.launcher.SharedConstants;

public final class HomeResolver
    implements SharedConstants
{
    private final Properties systemProperties;

    public HomeResolver()
    {
        this.systemProperties = new Properties();
    }

    public void addSystemProperties( final Properties props )
    {
        this.systemProperties.putAll( props );
    }

    public void addSystemProperties( final Map<String, String> map )
    {
        this.systemProperties.putAll( map );
    }

    public File resolve()
    {
        return validatePath( resolvePath() );
    }

    private String resolvePath()
    {
        String path = this.systemProperties.getProperty( HOME_PROP );
        if ( !Strings.isNullOrEmpty( path ) )
        {
            return path;
        }

        path = this.systemProperties.getProperty( HOME_ENV );
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
