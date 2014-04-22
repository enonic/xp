package com.enonic.wem.core.schema;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FilenameUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;

import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.api.schema.SchemaIcon;
import com.enonic.wem.api.util.MediaTypes;

public final class SchemaIconDao
{
    private static final String ICON_FILE_NAME = "thumb";

    private final ImmutableMap<MediaType, String> imageTypeExtensions =
        ImmutableMap.of( MediaType.JPEG, "jpeg", MediaType.GIF, "gif", MediaType.BMP, "bmp", MediaType.PNG, "png" );

    public void writeSchemaIcon( final SchemaIcon icon, final Path schemaPath )
    {
        if ( icon != null )
        {
            try
            {
                try (final DirectoryStream<Path> iconFiles = Files.newDirectoryStream( schemaPath, ICON_FILE_NAME + ".*" ))
                {
                    for ( Path iconFile : iconFiles )
                    {
                        Files.deleteIfExists( iconFile );
                    }
                }
                final String iconFileName = getIconFileName( icon );
                final Path iconFile = schemaPath.resolve( iconFileName );
                Files.copy( icon.asInputStream(), iconFile, StandardCopyOption.REPLACE_EXISTING );
            }
            catch ( IOException e )
            {
                throw new SystemException( e, "Could not store schema icon in [{0}]", schemaPath );
            }
        }
    }

    public SchemaIcon readSchemaIcon( final Path schemaPath )
    {
        try (final DirectoryStream<Path> iconFiles = Files.newDirectoryStream( schemaPath, ICON_FILE_NAME + ".*" ))
        {
            for ( Path iconFile : iconFiles )
            {
                if ( Files.isRegularFile( iconFile ) )
                {
                    final String extension = FilenameUtils.getExtension( iconFile.getFileName().toString() );
                    final MediaType mediaType = MediaTypes.instance().fromExt( extension );
                    return SchemaIcon.from( Files.newInputStream( iconFile ), mediaType.toString() );
                }
            }
            return null;
        }
        catch ( IOException e )
        {
            throw new SystemException( e, "Could not read schema icon from [{0}]", schemaPath );
        }
    }

    private String getIconFileName( final SchemaIcon icon )
    {
        final String ext = imageTypeExtensions.get( MediaType.parse( icon.getMimeType() ) );
        return ext == null ? ICON_FILE_NAME : ICON_FILE_NAME + "." + ext;
    }
}
