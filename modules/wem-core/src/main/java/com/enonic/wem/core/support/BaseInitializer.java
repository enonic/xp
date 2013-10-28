package com.enonic.wem.core.support;


import java.io.IOException;
import java.io.StringWriter;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.QualifiedName;
import com.enonic.wem.core.initializer.InitializerTask;

public abstract class BaseInitializer
    extends InitializerTask
{
    private static final Logger LOG = LoggerFactory.getLogger( BaseInitializer.class );

    private static final String FILE_SEPARATOR = "/";

    protected Client client;

    private String metaInfFolderBasePath;

    protected BaseInitializer( final int order, final String metaInfFolderName )
    {
        super( order );
        this.metaInfFolderBasePath = FILE_SEPARATOR + "META-INF" + FILE_SEPARATOR + metaInfFolderName;
    }

    protected String loadFileAsString( final String name )
    {
        final String filePath = metaInfFolderBasePath + FILE_SEPARATOR + name;
        final StringWriter writer = new StringWriter();
        try
        {
            IOUtils.copy( getClass().getResourceAsStream( filePath ), writer );
            return writer.toString();
        }
        catch ( IOException e )
        {
            LOG.warn( "File not found: " + filePath, e );
            return null;
        }
    }

    protected Icon loadIcon( final String name )
    {
        final String filePath = metaInfFolderBasePath + FILE_SEPARATOR + name.toLowerCase() + ".png";
        try
        {
            final byte[] iconData = IOUtils.toByteArray( this.getClass().getResourceAsStream( filePath ) );
            return Icon.from( iconData, "image/png" );
        }
        catch ( Exception e )
        {
            LOG.warn( "Icon not found: " + filePath );
            return null;
        }
    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
