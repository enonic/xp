package com.enonic.xp.launcher.provision;

import java.io.File;

import com.enonic.xp.launcher.SharedConstants;
import com.enonic.xp.launcher.config.ConfigProperties;

final class BundleLocationResolver
    implements SharedConstants
{
    private final File systemDir;

    private final ConfigProperties config;

    public BundleLocationResolver( final File systemDir, final ConfigProperties config )
    {
        this.systemDir = systemDir;
        this.config = config;
    }

    private boolean isDevMode()
    {
        return this.config.getBoolean( DEV_MODE );
    }

    private String getGroupId()
    {
        return this.config.get( DEV_GROUP_ID );
    }

    private File getProjectDir()
    {
        return this.config.getFile( DEV_PROJECT_DIR );
    }

    public String resolve( final String gav )
    {
        if ( isDevMode() )
        {
            final File file = findInProjectDir( gav );
            if ( ( file != null ) && file.isFile() )
            {
                return file.toURI().toString();
            }
        }

        return getFileInSystem( gav ).toURI().toString();
    }

    private String getFileName( final String[] gav )
    {
        final StringBuilder name = new StringBuilder( gav[1] );
        name.append( "-" ).append( gav[2] );

        if ( gav.length > 3 )
        {
            name.append( "-" ).append( gav[3] );
        }

        if ( gav.length > 4 )
        {
            name.append( "." ).append( gav[4] );
        }
        else
        {
            name.append( ".jar" );
        }

        return name.toString();
    }

    private File getFileInSystem( final String gav )
    {
        final String[] parts = gav.split( ":" );
        final File groupDir = new File( this.systemDir, parts[0].replace( '.', '/' ) );
        final File artifactDir = new File( groupDir, parts[1] );
        final File versionDir = new File( artifactDir, parts[2] );
        return new File( versionDir, getFileName( parts ) );
    }

    private File findInProjectDir( final String gav )
    {
        final String[] parts = gav.split( ":" );
        if ( !parts[0].equals( getGroupId() ) )
        {
            return null;
        }

        final File projectDir = getProjectDir();
        if ( projectDir == null )
        {
            return null;
        }

        final File modulesDir = new File( projectDir, "modules" );
        final File subProjectDir = new File( modulesDir, parts[1] );
        final File targetDir = new File( subProjectDir, "target" );
        final File libDir = new File( targetDir, "libs" );
        return new File( libDir, getFileName( parts ) );
    }
}
