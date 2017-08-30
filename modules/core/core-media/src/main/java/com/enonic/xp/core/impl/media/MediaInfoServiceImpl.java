package com.enonic.xp.core.impl.media;

import java.io.IOException;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.data.PropertySet;
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
    private BinaryExtractor binaryExtractor;

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
            throw Exceptions.unchecked( e );
        }
    }

    @Override
    public ImageOrientation getImageOrientation( ByteSource byteSource )
    {
        final ExtractedData extractedData = binaryExtractor.extract( byteSource );
        final String orientation = extractedData.getImageOrientation();
        return ImageOrientation.from( orientation );
    }

    public ImageOrientation getImageOrientation( ByteSource byteSource, Content media )
    {
        final PropertySet mediaSet = media.getData().getPropertySet( ContentPropertyNames.MEDIA );
        if ( mediaSet != null && mediaSet.hasProperty( ContentPropertyNames.ORIENTATION ) )
        {
            return ImageOrientation.from( mediaSet.getString( ContentPropertyNames.ORIENTATION ) );
        }

        final ExtraData cameraInfo = media.getAllExtraData().getMetadata( MediaInfo.CAMERA_INFO_METADATA_NAME );
        if ( cameraInfo != null && cameraInfo.getData().hasProperty( ContentPropertyNames.ORIENTATION ) )
        {
            return ImageOrientation.from( cameraInfo.getData().getString( ContentPropertyNames.ORIENTATION ) );
        }
        return getImageOrientation( byteSource );
    }

    @Reference
    public void setBinaryExtractor( final BinaryExtractor binaryExtractor )
    {
        this.binaryExtractor = binaryExtractor;
    }
}
