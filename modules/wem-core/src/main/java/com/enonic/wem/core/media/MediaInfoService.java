package com.enonic.wem.core.media;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.enonic.wem.api.util.Exceptions;

public final class MediaInfoService
{
    private final static Logger LOG = LoggerFactory.getLogger( MediaInfoService.class );

    private Parser parser;

    private Detector detector;

    public void diag()
    {
        LOG.info( "Tika..." );
        LOG.info( "* Detector = " + this.detector );
        LOG.info( "* Parser   = " + this.parser );

        final ParseContext context = new ParseContext();
        final Set<MediaType> supportedTypes = this.parser.getSupportedTypes( context );

        for ( final MediaType type : supportedTypes )
        {
            LOG.info( "* Media Type = " + type );
        }
    }

    public MediaInfo parseMediaInfo( final InputStream inputStream )
    {
        final ParseContext context = new ParseContext();
        final ContentHandler handler = new DefaultHandler();
        final Metadata metadata = new Metadata();

        try
        {
            parser.parse( inputStream, handler, metadata, context );
        }
        catch ( IOException | SAXException | TikaException e )
        {
            throw Exceptions.unchecked( e );
        }

        MediaInfo.Builder builder = MediaInfo.create();

        final String[] names = metadata.names();
        for ( String name : names )
        {
            String value = metadata.get( name );
            builder.addMetadata( name, value );
        }

        try
        {
            builder.mediaType( detector.detect( inputStream, metadata ) );
        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
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
