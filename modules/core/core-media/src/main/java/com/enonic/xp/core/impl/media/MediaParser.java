package com.enonic.xp.core.impl.media;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import com.google.common.io.ByteSource;

import com.enonic.xp.util.Exceptions;

public class MediaParser
{
    private final Detector detector;

    private final Parser parser;

    public MediaParser( final Detector detector, final Parser parser )
    {
        this.detector = detector;
        this.parser = parser;
    }

    ParsedMediaData parseMetadata( final ByteSource byteSource )
    {
        final ParseContext context = new ParseContext();
        final BodyContentHandler handler = new BodyContentHandler();
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

        return ParsedMediaData.create().
            handler( getContent( handler ) ).
            metadata( metadata ).
            build();
    }


    private String getContent( final BodyContentHandler contentHandler )
    {
        return contentHandler.toString();
    }

}
