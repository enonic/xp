package com.enonic.wem.core.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.Instant;

import org.apache.commons.io.IOUtils;

import com.enonic.wem.api.Icon;
import com.enonic.wem.core.initializer.InitializerTask;

public abstract class BaseInitializer
    extends InitializerTask
{
    private static final String FILE_SEPARATOR = "/";

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

    protected Icon loadSchemaIcon( final String name )
    {
        final String filePath = metaInfFolderBasePath + FILE_SEPARATOR + name.toLowerCase() + ".png";
        try
        {
            final InputStream stream = this.getClass().getResourceAsStream( filePath );
            if ( stream == null )
            {
                return null;
            }
            return Icon.from( stream, "image/png", Instant.now() );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to load icon file: " + filePath, e );
        }
    }
}
