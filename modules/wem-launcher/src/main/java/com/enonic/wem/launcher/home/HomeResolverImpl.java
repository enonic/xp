package com.enonic.wem.launcher.home;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.enonic.wem.launcher.LauncherException;

public final class HomeResolverImpl
    implements HomeConstants, HomeResolver
{
    private final Properties systemProperties;

    private final Map<String, String> environmentVariables;

    public HomeResolverImpl()
    {
        this.systemProperties = new Properties();
        this.environmentVariables = new HashMap<>();
    }

    public void addSystemProperties( final Properties props )
    {
        this.systemProperties.putAll( props );
    }

    public void addEnvironmentVariables( final Map<String, String> map )
    {
        this.environmentVariables.putAll( map );
    }

    public HomeDir resolve()
    {
        final File dir = validatePath( resolvePath() );
        return new HomeDir( dir );
    }

    private String resolvePath()
    {
        String path = this.systemProperties.getProperty( HOME_DIR_PROP );
        if ( !isNullOrEmpty( path ) )
        {
            return path;
        }

        path = this.environmentVariables.get( HOME_DIR_ENV );
        if ( !isNullOrEmpty( path ) )
        {
            return path;
        }

        throw new LauncherException( "Home directory is not set. Please set either [%s] system property or [%s] environment variable.",
                                     HOME_DIR_PROP, HOME_DIR_ENV );
    }

    private File validatePath( final String path )
    {
        final File dir = new File( path ).getAbsoluteFile();
        if ( !dir.exists() || !dir.isDirectory() )
        {
            throw new LauncherException( "Invalid home directory. [%s] is not a directory.", dir.toString() );
        }

        return dir;
    }

    private boolean isNullOrEmpty( final String str )
    {
        return ( str == null ) || str.isEmpty();
    }
}
