package com.enonic.wem.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.core.home.HomeDir;
import com.enonic.wem.core.lifecycle.InitializingBean;

/**
 * This class implements the mime type resolver.
 */
public final class MimeTypeResolverImpl
    implements MimeTypeResolver, InitializingBean
{

    private static final Logger LOG = LoggerFactory.getLogger( MimeTypeResolverImpl.class.getName() );

    /**
     * Default mime type.
     */
    private final static String DEFAULT_MIME_TYPE = "application/octet-stream";

    public static final String SYSTEM_MIMETYPE_PROPERTIES = "mimetypes.properties";

    /**
     * Mime types collection.
     */
    private Properties mimeTypes;

    private String mimetypesLocation = "config/mimetypes.properties";

    private final HomeDir homeDir;

    /**
     * Construct the resolver.
     */
    @Inject
    public MimeTypeResolverImpl( final HomeDir homeDir )
    {
        this.homeDir = homeDir;
        loadMimeTypes( mimetypesLocation );

    }

    @Override
    public void afterPropertiesSet()
        throws Exception
    {
        this.mimeTypes = loadMimeTypes( mimetypesLocation );
    }

    /**
     * Return the mime type by file.
     */
    @Override
    public String getMimeType( String fileName )
    {
        String ext = fileName.substring( fileName.lastIndexOf( "." ) + 1 );

        if ( ext.equals( "" ) )
        {
            ext = fileName;
        }

        return getMimeTypeByExtension( ext );
    }

    /**
     * Return the mime type by extension.
     */
    @Override
    public String getMimeTypeByExtension( String ext )
    {
        final String key = ext.toLowerCase();

        final String localProperty = this.mimeTypes.getProperty( key );

        if ( localProperty != null )
        {
            return localProperty;
        }

  /*      if ( servletContext != null )
        {
            final String containerProperty = servletContext.getMimeType( key );

            if ( containerProperty != null )
            {
                return containerProperty;
            }
        }
        else
        {
            return DEFAULT_MIME_TYPE;
        }
         */

        return DEFAULT_MIME_TYPE;
    }

    /**
     * Find extension by mime type.
     */
    @Override
    public String getExtension( String mimeType )
    {
        String ext = "";

        if ( mimeType == null || ext.equals( mimeType ) || DEFAULT_MIME_TYPE.equals( mimeType ) )
        {
            return ext;
        }

        for ( final Map.Entry entry : mimeTypes.entrySet() )
        {
            final String key = (String) entry.getKey();
            final String value = (String) entry.getValue();

            if ( value.equals( mimeType ) && mimeType.endsWith( key ) )
            {
                return key;
            }

            if ( value.equals( mimeType ) && ext.equals( "" ) )
            {
                ext = key;
            }
            else if ( value.equals( mimeType ) && !ext.equals( "" ) )
            {
                return ext;
            }
        }

        return ext;
    }

    /**
     * Load mime types. User can override default mimetypes by own in CMS_HOME directory.
     *
     * @param mimetypesLocation location of .properties file
     * @return map of mime types
     */
    private Properties loadMimeTypes( final String mimetypesLocation )
    {
        final Properties userProps = new Properties();
        final Properties systemProps = new Properties();

        try
        {
            // load user defined mime types from mimetypesLocation ( e.g. CMS_HOME )
            final File file = new File( homeDir.toFile(), mimetypesLocation );
            if ( file.exists() )
            {
                userProps.load( new FileInputStream( file ) );

                LOG.info( "loaded {} user-defined mimetypes from file {}", userProps.size(), homeDir.toString() + "/" + mimetypesLocation );
            }
        }
        catch ( Exception e )
        {
            LOG.error( "Unable to load user mimetypes from {}. Reason: {}", homeDir.toString() + "/" + mimetypesLocation, e.toString() );
        }

        try
        {
            final InputStream input = MimeTypeResolverImpl.class.getResourceAsStream( SYSTEM_MIMETYPE_PROPERTIES );

            if ( input == null )
            {
                throw new InternalError( "Unable to find " + SYSTEM_MIMETYPE_PROPERTIES );
            }

            systemProps.load( input );
        }
        catch ( IOException e )
        {
            throw new InternalError( "Unable to load system mimetypes: " + e.toString() );
        }

        // overwrite default properties with user properties
        systemProps.putAll( userProps );

        return systemProps;
    }

}

