package com.enonic.wem.core.support.dao;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FilenameUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.api.util.MediaTypes;

public final class IconDao
{
    private static final String ICON_FILE_NAME = "thumb";

    private final ImmutableMap<MediaType, String> imageTypeExtensions =
        ImmutableMap.of( MediaType.JPEG, "jpeg", MediaType.GIF, "gif", MediaType.BMP, "bmp", MediaType.PNG, "png" );

    public void writeIcon( final Icon icon, final Path itemPath )
    {
        if ( icon != null )
        {
            try
            {
                try (final DirectoryStream<Path> iconFiles = Files.newDirectoryStream( itemPath, ICON_FILE_NAME + ".*" ))
                {
                    for ( Path iconFile : iconFiles )
                    {
                        Files.deleteIfExists( iconFile );
                    }
                }
                final String iconFileName = getIconFileName( icon );
                final Path iconFile = itemPath.resolve( iconFileName );
                Files.copy( icon.asInputStream(), iconFile, StandardCopyOption.REPLACE_EXISTING );
            }
            catch ( IOException e )
            {
                throw new SystemException( e, "Could not store icon in [{0}]", itemPath );
            }
        }
    }

    public Icon readIcon( final Path itemPath )
    {
        try (final DirectoryStream<Path> iconFiles = Files.newDirectoryStream( itemPath, ICON_FILE_NAME + ".*" ))
        {
            for ( Path iconFile : iconFiles )
            {
                if ( Files.isRegularFile( iconFile ) )
                {
                    final String extension = FilenameUtils.getExtension( iconFile.getFileName().toString() );
                    final MediaType mediaType = MediaTypes.instance().fromExt( extension );
                    return Icon.from( Files.newInputStream( iconFile ), mediaType.toString() );
                }
            }
            return null;
        }
        catch ( IOException e )
        {
            throw new SystemException( e, "Could not read icon from [{0}]", itemPath );
        }
    }

    private String getIconFileName( final Icon icon )
    {
        final String ext = imageTypeExtensions.get( MediaType.parse( icon.getMimeType() ) );
        return ext == null ? ICON_FILE_NAME : ICON_FILE_NAME + "." + ext;
    }
}
