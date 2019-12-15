package com.enonic.xp.server.internal.config;

import java.io.File;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import com.enonic.xp.core.internal.Dictionaries;

@Component
public final class ConfigInstallerImpl
    implements ConfigInstaller
{
    private final static String FILENAME_PROP = "config.filename";

    private final static Logger LOG = LoggerFactory.getLogger( ConfigInstallerImpl.class );

    private ConfigurationAdmin configurationAdmin;

    private ConfigLoader loader;

    @Activate
    public void activate( final BundleContext context )
    {
        this.loader = new ConfigLoader( context );
    }

    @Override
    public void updateConfig( final File file )
    {
        final String pid = parsePid( file.getName() );

        try
        {
            doUpdateConfig( file, pid );
        }
        catch ( final Exception e )
        {
            LOG.error( "Error deleting config [" + pid + "]", e );
        }
    }

    private void doUpdateConfig( final File file, final String pid )
        throws Exception
    {
        final String fileName = file.getName();
        final Map<String, Object> newMap = new HashMap<>( this.loader.load( file ) );
        final Configuration config = getConfiguration( fileName, pid );

        final HashMap<String, Object> oldMap = new HashMap<>();
        final Dictionary<String, Object> currentConfigProps = config.getProperties();
        if ( currentConfigProps != null )
        {
            currentConfigProps.keys().asIterator().forEachRemaining( key -> oldMap.put( key, currentConfigProps.get( key ) ) );
        }
        oldMap.remove( Constants.SERVICE_PID );
        oldMap.remove( FILENAME_PROP );

        if ( newMap.equals( oldMap ) )
        {
            return;
        }

        newMap.put( FILENAME_PROP, fileName );
        config.update( Dictionaries.copyOf( newMap ) );

        LOG.info( "Loaded config for [" + pid + "]" );
    }

    @Override
    public void deleteConfig( final String fileName )
    {
        final String pid = parsePid( fileName );

        try
        {
            doDeleteConfig( fileName, pid );
        }
        catch ( final Exception e )
        {
            LOG.error( "Error deleting config [" + pid + "]", e );
        }
    }

    private void doDeleteConfig( final String fileName, final String pid )
        throws Exception
    {
        final Configuration config = getConfiguration( fileName, pid );
        config.delete();

        LOG.info( "Deleted config for [" + pid + "]" );
    }

    private String parsePid( final String fileName )
    {
        return Files.getNameWithoutExtension( fileName );
    }

    private Configuration getConfiguration( final String fileName, final String pid )
        throws Exception
    {
        final Configuration oldConfiguration = findExistingConfiguration( fileName );
        if ( oldConfiguration != null )
        {
            return oldConfiguration;
        }

        return this.configurationAdmin.getConfiguration( pid, null );
    }

    private Configuration findExistingConfiguration( String fileName )
        throws Exception
    {
        final String filter = "(" + FILENAME_PROP + "=" + escapeFilterValue( fileName ) + ")";
        final Configuration[] configurations = this.configurationAdmin.listConfigurations( filter );

        if ( configurations != null && configurations.length > 0 )
        {
            return configurations[0];
        }
        else
        {
            return null;
        }
    }

    private String escapeFilterValue( final String str )
    {
        return str.
            replaceAll( "[(]", "\\\\(" ).
            replaceAll( "[)]", "\\\\)" ).
            replaceAll( "[=]", "\\\\=" ).
            replaceAll( "[*]", "\\\\*" );
    }

    @Reference
    public void setConfigurationAdmin( final ConfigurationAdmin configurationAdmin )
    {
        this.configurationAdmin = configurationAdmin;
    }
}
