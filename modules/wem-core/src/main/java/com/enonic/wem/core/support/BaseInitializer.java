package com.enonic.wem.core.support;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;

import com.google.common.io.ByteStreams;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.blob.CreateBlob;
import com.enonic.wem.api.icon.Icon;
import com.enonic.wem.api.schema.SchemaIcon;
import com.enonic.wem.core.initializer.InitializerTask;

public abstract class BaseInitializer
    extends InitializerTask
{
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
            throw new RuntimeException( "Failed to load file: " + filePath, e );
        }
    }

    protected Icon loadIcon( final String name )
    {
        final String filePath = metaInfFolderBasePath + FILE_SEPARATOR + name.toLowerCase() + ".png";
        try
        {
            final InputStream stream = this.getClass().getResourceAsStream( filePath );
            if ( stream == null )
            {
                return null;
            }

            final byte[] iconData = IOUtils.toByteArray( stream );
            final CreateBlob createBlob = Commands.blob().create( ByteStreams.newInputStreamSupplier( iconData ).getInput() );
            final Blob blob = client.execute( createBlob );
            return Icon.from( blob.getKey(), "image/png" );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to load icon file: " + filePath, e );
        }
    }

    protected SchemaIcon loadSchemaIcon( final String name )
    {
        final String filePath = metaInfFolderBasePath + FILE_SEPARATOR + name.toLowerCase() + ".png";
        try
        {
            final InputStream stream = this.getClass().getResourceAsStream( filePath );
            if ( stream == null )
            {
                return null;
            }
            return SchemaIcon.from( stream, "image/png" );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to load icon file: " + filePath, e );
        }
    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
