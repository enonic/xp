package com.enonic.wem.core.support;


import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.ModuleBasedQualifiedName;

public abstract class BaseInitializer
{
    private static final Logger LOG = LoggerFactory.getLogger( BaseInitializer.class );

    protected Client client;

    private String metaInfFolderBasePath;

    protected BaseInitializer( final String metaInfFolderName )
    {
        this.metaInfFolderBasePath = File.separator + "META-INF" + File.separator + metaInfFolderName;
    }

    protected String loadFileAsString( final String name )
    {
        final String filePath = metaInfFolderBasePath + File.separator + name;
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

    protected Icon loadIcon( final ModuleBasedQualifiedName qualifiedName )
    {
        final String filePath =
            metaInfFolderBasePath + File.separator + qualifiedName.toString().replace( ":", "_" ).toLowerCase() + ".png";
        try
        {
            final byte[] iconData = IOUtils.toByteArray( this.getClass().getResourceAsStream( filePath ) );
            return Icon.from( iconData, "image/png" );
        }
        catch ( Exception e )
        {
            LOG.warn( "Icon not found: " + filePath, e );
            return null;
        }
    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
