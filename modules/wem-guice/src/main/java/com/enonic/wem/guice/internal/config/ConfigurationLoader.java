package com.enonic.wem.guice.internal.config;

import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.felix.utils.properties.InterpolationHelper;
import org.apache.felix.utils.properties.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationAdmin;

import com.google.inject.ProvisionException;

import com.enonic.wem.guice.Configuration;

@Singleton
final class ConfigurationLoader
    implements Provider<Configuration>
{
    @Inject
    @Named(Constants.SERVICE_PID)
    protected String pid;

    @Inject
    protected ConfigurationAdmin configurationAdmin;

    @Inject
    protected BundleContext context;

    @Override
    public Configuration get()
    {
        try
        {
            return doLoad();
        }
        catch ( final Exception e )
        {
            throw new ProvisionException( "Failed to load configuration", e );
        }
    }

    private Configuration doLoad()
        throws Exception
    {
        final Properties defaultProps = loadDefault();
        interpolate( defaultProps );

        final Dictionary loadedConfig = this.configurationAdmin.getConfiguration( this.pid ).getProperties();

        final Configuration config = new Configuration();
        config.putAll( defaultProps );
        config.putAll( toMap( loadedConfig ) );
        return config;
    }

    private Properties loadDefault()
        throws Exception
    {
        final Properties props = new Properties();

        final URL url = this.context.getBundle().getResource( "/OSGI-INF/default.properties" );
        if ( url == null )
        {
            return props;
        }

        props.load( url );
        return props;
    }

    private void interpolate( final Properties props )
    {
        InterpolationHelper.performSubstitution( props, this.context );
    }

    private Map<String, String> toMap( final Dictionary props )
    {
        final Map<String, String> map = new HashMap<>();
        final Enumeration e = props.keys();
        while ( e.hasMoreElements() )
        {
            final String key = (String) e.nextElement();
            map.put( key, props.get( key ).toString() );
        }

        return map;
    }
}
