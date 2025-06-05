package com.enonic.xp.core.impl.media;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.content.Media;
import com.enonic.xp.extractor.BinaryExtractor;
import com.enonic.xp.extractor.ExtractedData;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.util.Exceptions;

@Component
public final class MediaInfoServiceImpl
    implements MediaInfoService
{
    private final BinaryExtractor binaryExtractor;

    @Activate
    public MediaInfoServiceImpl( @Reference final BinaryExtractor binaryExtractor )
    {
        this.binaryExtractor = binaryExtractor;
    }

    @Override
    public MediaInfo parseMediaInfo( final ByteSource byteSource )
    {
        final MediaInfo.Builder builder = MediaInfo.create();

        final ExtractedData extractedData = binaryExtractor.extract( byteSource );

        addMetadata( byteSource, builder, extractedData );
        builder.setTextContent( extractedData.getText() );

        return builder.build();
    }

    private void addMetadata( final ByteSource byteSource, final MediaInfo.Builder builder, final ExtractedData extractedData )
    {
        builder.mediaType( extractedData.get( HttpHeaders.CONTENT_TYPE ) );

        final Set<String> names = extractedData.names();
        for ( final String name : names )
        {
            final String value = extractedData.get( name );
            builder.addMetadata( name, value );
        }
        try
        {
            builder.addMetadata( MediaInfo.MEDIA_INFO_BYTE_SIZE, String.valueOf( byteSource.size() ) );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Override
    public ImageOrientation getImageOrientation( ByteSource byteSource )
    {
        final ExtractedData extractedData = binaryExtractor.extract( byteSource );
        final String orientation = extractedData.getImageOrientation();

        if ( ImageOrientation.isValid( orientation ) )
        {
            return ImageOrientation.from( orientation );
        }

        return null;
    }

    @Override
    public ImageOrientation getImageOrientation( ByteSource byteSource, Media media )
    {
        final ImageOrientation imageOrientation = media.getOrientation();

        if ( imageOrientation != null )
        {
            return imageOrientation;
        }

        return getImageOrientation( byteSource );
    }
}
