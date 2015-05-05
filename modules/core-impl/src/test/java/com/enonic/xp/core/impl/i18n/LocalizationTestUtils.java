package com.enonic.xp.core.impl.i18n;

import java.util.Properties;

import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.enonic.xp.i18n.MessageBundle;


public class LocalizationTestUtils
{
    private static final String BASE_RESOURCE_CLASSPATH = "classpath:com/enonic/xp/core/impl/i18n/";

    public static Properties create_Default_Properties()
        throws Exception
    {
        return getPropertiesFromFile( BASE_RESOURCE_CLASSPATH + "phrases.properties" );
    }

    public static Properties create_NO_Properties()
        throws Exception
    {
        return getPropertiesFromFile( BASE_RESOURCE_CLASSPATH + "phrases_no.properties" );
    }

    public static Properties create_EN_US_Properties()
        throws Exception
    {
        return getPropertiesFromFile( BASE_RESOURCE_CLASSPATH + "phrases_en-us.properties" );
    }

    public static Properties getPropertiesFromFile( String path )
        throws Exception
    {
        final Properties properties = new Properties();

        final ResourceLoader resourceLoader = new FileSystemResourceLoader();
        final Resource resource = resourceLoader.getResource( path );

        properties.load( resource.getInputStream() );
        return properties;
    }

    public static MessageBundle create_US_NO_DEFAULT_resourceBundle()
        throws Exception
    {
        final Properties properties = new Properties();

        properties.putAll( create_Default_Properties() );
        properties.putAll( create_NO_Properties() );
        properties.putAll( create_EN_US_Properties() );

        return new MessageBundleImpl( properties );
    }

}