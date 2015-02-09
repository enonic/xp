package com.enonic.xp.launcher.provision;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.launcher.SharedConstants;
import com.enonic.xp.launcher.config.ConfigProperties;

final class BundleLocationResolver
    implements SharedConstants
{
    private final static Logger LOG = LoggerFactory.getLogger( ProvisionActivator.class );

    private final File systemDir;

    private final boolean devMode;

    private final File projectDir;

    private final String groupId;

    public BundleLocationResolver( final File systemDir, final ConfigProperties config )
    {
        this.systemDir = systemDir;
        this.projectDir = config.getFile( DEV_PROJECT_DIR );
        this.devMode = config.getBoolean( DEV_MODE ) && ( this.projectDir != null );
        this.groupId = config.get( DEV_GROUP_ID );

        if ( this.devMode )
        {
            LOG.info( "Development mode is on. Loading [{}] bundles from [{}].", this.groupId, this.projectDir.getAbsolutePath() );
        }
    }

    public String resolve( final String gav )
    {
        if ( this.devMode )
        {
            final File file = findInProjectDir( gav );
            if ( file != null )
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
        if ( !parts[0].equals( this.groupId ) )
        {
            return null;
        }

        final File modulesDir = new File( this.projectDir, "modules" );
        final File subProjectDir = new File( modulesDir, parts[1] );
        final File targetDir = new File( subProjectDir, "target" );
        final File libDir = new File( targetDir, "libs" );
        return new File( libDir, getFileName( parts ) );
    }
}
