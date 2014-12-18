package com.enonic.wem.core.media;

import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.util.Exceptions;

public final class MediaInfoService
{
    private Parser parser;

    private Detector detector;

    public MediaInfo parseMediaInfo( final ByteSource byteSource )
    {
        try
        {
            return doParseMediaInfo( byteSource );
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private MediaInfo doParseMediaInfo( final ByteSource byteSource )
        throws Exception
    {
        final ParseContext context = new ParseContext();
        final ContentHandler handler = new DefaultHandler();
        final Metadata metadata = new Metadata();
        final MediaInfo.Builder builder = MediaInfo.create();

        // Parse metadata
        final AutoDetectParser autoDetectParser = new AutoDetectParser( this.detector, this.parser );
        autoDetectParser.parse( byteSource.openStream(), handler, metadata, context );

        // Get the detected media-type
        builder.mediaType( metadata.get( Metadata.CONTENT_TYPE ) );

        // Append metadata to info object
        final String[] names = metadata.names();
        for ( final String name : names )
        {
            final String value = metadata.get( name );
            builder.addMetadata( name, value );
        }

        return builder.build();
    }

    public void setParser( final Parser parser )
    {
        this.parser = parser;
    }

    public void setDetector( final Detector detector )
    {
        this.detector = detector;
    }
}
