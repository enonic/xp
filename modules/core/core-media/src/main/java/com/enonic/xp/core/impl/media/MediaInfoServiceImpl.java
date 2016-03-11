package com.enonic.xp.core.impl.media;

import java.io.IOException;

import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.Parser;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.media.ExtractedTextInfo;
import com.enonic.xp.media.ImageOrientation;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.util.Exceptions;

@Component
public final class MediaInfoServiceImpl
    implements MediaInfoService
{
    private Parser parser;

    private Detector detector;

    private MediaParser mediaParser;

    @Activate
    public void activate()
    {
        this.mediaParser = new MediaParser( detector, parser );
    }

    @Override
    public MediaInfo parseMediaInfo( final ByteSource byteSource )
    {
        final MediaInfo.Builder builder = MediaInfo.create();

        final ParsedMediaData parsedMediaData = mediaParser.parseMetadata( byteSource );

        addMetadata( byteSource, builder, parsedMediaData );

        builder.setExtratedTextInfo( new ExtractedTextInfo( parsedMediaData.getContent() ) );

        return builder.build();
    }

    private void addMetadata( final ByteSource byteSource, final MediaInfo.Builder builder, final ParsedMediaData parsedMediaData )
    {
        final Metadata metadata = parsedMediaData.getMetadata();

        builder.mediaType( metadata.get( Metadata.CONTENT_TYPE ) );

        final String[] names = metadata.names();
        for ( final String name : names )
        {
            final String value = metadata.get( name );
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
        final Metadata metadata = this.parseMetadata( byteSource );
        final String orientation = metadata.get( Metadata.ORIENTATION );
        return ImageOrientation.from( orientation );
    }

    private Metadata parseMetadata( final ByteSource byteSource )
    {
        return this.mediaParser.parseMetadata( byteSource ).getMetadata();

        /*
        final ParseContext context = new ParseContext();
        final ContentHandler handler = new BodyContentHandler();
        final Metadata metadata = new Metadata();

        // Parse metadata
        try (final InputStream stream = byteSource.openStream())
        {
            final AutoDetectParser autoDetectParser = new AutoDetectParser( this.detector, this.parser );

            autoDetectParser.parse( stream, handler, metadata, context );

        }
        catch ( IOException | SAXException | TikaException e )
        {
            throw Exceptions.unchecked( e );
        }

        System.out.println( handler.toString() );

        return metadata;
        */
    }

    @Reference
    public void setParser( final Parser parser )
    {
        this.parser = parser;
    }

    @Reference
    public void setDetector( final Detector detector )
    {
        this.detector = detector;
    }
}
