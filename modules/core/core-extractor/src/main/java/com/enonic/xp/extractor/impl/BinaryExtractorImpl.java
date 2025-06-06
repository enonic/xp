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
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.google.common.io.ByteSource;

import com.enonic.xp.extractor.BinaryExtractor;
import com.enonic.xp.extractor.ExtractedData;
import com.enonic.xp.extractor.impl.config.ExtractorConfig;

@Component(configurationPid = "com.enonic.xp.extractor")
public class BinaryExtractorImpl
    implements BinaryExtractor
{
    private static final Logger LOG = LoggerFactory.getLogger( BinaryExtractorImpl.class );

    private final Detector detector;

    private final Parser parser;

    private volatile int bodySizeLimit;

    @Activate
    public BinaryExtractorImpl( @Reference final Detector detector, @Reference final Parser parser )
    {
        this.detector = detector;
        this.parser = parser;
    }

    @Activate
    @Modified
    public void activate( final ExtractorConfig extractorConfig )
    {
        this.bodySizeLimit = extractorConfig.body_size_limit();
    }

    @Override
    public ExtractedData extract( final ByteSource source )
    {
        final BodyContentHandler handler = new BodyContentHandler( bodySizeLimit );
        final Metadata metadata = new Metadata();

        try (InputStream stream = source.openStream())
        {
            final AutoDetectParser autoDetectParser = new AutoDetectParser( this.detector, this.parser );

            autoDetectParser.parse( stream, handler, metadata, new ParseContext() );
        }
        catch ( IOException | SAXException | TikaException e )
        {
            LOG.warn( "Error extracting binary: {}", e.getMessage(), e );
        }

        return ExtractorResultFactory.create( metadata, handler );
    }
}
