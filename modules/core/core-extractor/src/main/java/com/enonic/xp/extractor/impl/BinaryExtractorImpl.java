package com.enonic.xp.extractor.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.google.common.io.ByteSource;

import com.enonic.xp.extractor.BinaryExtractor;
import com.enonic.xp.extractor.ExtractedData;

@Component(immediate = true)
public class BinaryExtractorImpl
    implements BinaryExtractor
{
    private final static Logger LOG = LoggerFactory.getLogger( BinaryExtractorImpl.class );

    private final static int WRITE_LIMIT = 100 * 1000;

    private Detector detector;

    private Parser parser;

    @Override
    public ExtractedData extract( final ByteSource source )
    {
        final ParseContext context = new ParseContext();
        final BodyContentHandler handler = new BodyContentHandler( WRITE_LIMIT );
        final Metadata metadata = new Metadata();

        try (final InputStream stream = source.openStream())
        {
            final AutoDetectParser autoDetectParser = new AutoDetectParser( this.detector, this.parser );

            autoDetectParser.parse( stream, handler, metadata, context );
        }
        catch ( IOException | SAXException | TikaException e )
        {
            LOG.error( "Error extracting binary", e );
        }

        return ExtractorResultFactory.create( metadata, handler );
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
