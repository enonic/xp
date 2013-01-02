package com.enonic.wem.itest.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.framework.util.PropertiesUtil;

import com.enonic.cms.core.home.HomeDir;

final class ConfigLoader
{
    private final static Logger LOG = LoggerFactory.getLogger( ConfigLoader.class );

    private final static String CMS_PROPERTIES = "config/cms.properties";

    private final static String DEFAULT_PROPERTIES = "com/enonic/vertical/default.properties";

    private final HomeDir homeDir;

    private final Properties systemProperties;

    private ClassLoader classLoader;

    public ConfigLoader( final HomeDir homeDir )
    {
        this.homeDir = homeDir;
        this.systemProperties = new Properties();
        setClassLoader( getClass().getClassLoader() );

        addSystemProperties( System.getenv() );
        addSystemProperties( System.getProperties() );
    }

    public void addSystemProperties( final Properties props )
    {
        this.systemProperties.putAll( props );
    }

    public void addSystemProperties( final Map<String, String> map )
    {
        this.systemProperties.putAll( map );
    }

    public void setClassLoader( final ClassLoader classLoader )
    {
        this.classLoader = classLoader;
    }

    public ConfigProperties load()
    {
        final Properties props = new Properties();
        props.putAll( loadDefaultProperties() );
        props.putAll( loadCmsProperties() );
        props.putAll( this.homeDir.toProperties() );

        final ConfigProperties config = new ConfigProperties();
        config.putAll( PropertiesUtil.interpolate( props, this.systemProperties ) );
        return config;
    }

    private Properties loadDefaultProperties()
    {
        final InputStream in = this.classLoader.getResourceAsStream( DEFAULT_PROPERTIES );
        if ( in == null )
        {
            throw new IllegalArgumentException( "Could not find default.properties [" +
                                                    DEFAULT_PROPERTIES + "] in classpath" );
        }

        try
        {
            return loadFromStream( in );
        }
        catch ( final Exception e )
        {
            throw new IllegalArgumentException( "Could not load default.properties [" +
                                                    DEFAULT_PROPERTIES + "] from classpath", e );
        }
    }

    private Properties loadCmsProperties()
    {
        final File file = new File( this.homeDir.toFile(), CMS_PROPERTIES );
        if ( !file.exists() || file.isDirectory() )
        {
            LOG.info( "Could not find cms.properties from [{0}]. Using defaults.", file.getAbsolutePath() );
            return new Properties();
        }

        try
        {
            return loadFromStream( new FileInputStream( file ) );
        }
        catch ( final Exception e )
        {
            LOG.error( "Failed to load cms.properties from [" + file.getAbsolutePath() + "]. Using defaults.", e );
        }

        return new Properties();
    }

    private Properties loadFromStream( final InputStream in )
        throws IOException
    {
        final Properties props = new Properties();
        props.load( in );
        in.close();
        return props;
    }
}
