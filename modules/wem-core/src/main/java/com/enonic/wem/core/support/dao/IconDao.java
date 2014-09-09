package com.enonic.wem.core.support.dao;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
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
                    final FileTime lastModifiedTime = Files.readAttributes( iconFile, BasicFileAttributes.class ).lastModifiedTime();
                    final MediaType mediaType = MediaTypes.instance().fromExt( extension );
                    return Icon.from( Files.newInputStream( iconFile ), mediaType.toString(), lastModifiedTime.toInstant() );
                }
            }
            return null;
        }
        catch ( IOException e )
        {
            throw new SystemException( e, "Could not read icon from [{0}]", itemPath );
        }
    }

    public Icon readIcon( final ResourceKey parentResource )
    {
        final Resource imageResource = imageTypeExtensions.values().stream().
            map( ext -> Resource.from( parentResource.resolve( ICON_FILE_NAME + "." + ext ) ) ).
            filter( Resource::exists ).
            findFirst().orElse( null );

        if ( imageResource == null )
        {
            return null;
        }

        final Instant modifiedTime = Instant.ofEpochMilli( imageResource.getTimestamp() );
        final String extension = FilenameUtils.getExtension( imageResource.getKey().getPath() );
        final MediaType mediaType = imageTypeExtensions.entrySet().stream().
            filter( entry -> entry.getValue().equals( extension ) ).
            map( Map.Entry::getKey ).
            findFirst().orElse( MediaType.ANY_IMAGE_TYPE );
        return Icon.from( imageResource.openStream(), mediaType.toString(), modifiedTime );

    }

    private String getIconFileName( final Icon icon )
    {
        final String ext = imageTypeExtensions.get( MediaType.parse( icon.getMimeType() ) );
        return ext == null ? ICON_FILE_NAME : ICON_FILE_NAME + "." + ext;
    }
}
