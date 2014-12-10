package com.enonic.wem.core.schema;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;

public final class IconDao
{
    private static final String ICON_FILE_NAME = "thumb";

    private final ImmutableMap<MediaType, String> imageTypeExtensions =
        ImmutableMap.of( MediaType.JPEG, "jpeg", MediaType.GIF, "gif", MediaType.BMP, "bmp", MediaType.PNG, "png" );

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
        try (final InputStream stream = imageResource.openStream())
        {
            return Icon.from( stream, mediaType.toString(), modifiedTime );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to close icon file with resource from: " + imageResource.getUrl().toString(), e );
        }

    }
}
