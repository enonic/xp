package com.enonic.wem.core.image;

import java.util.Set;

import org.apache.tika.detect.Detector;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Just a sample service showing the use of Tika auto-detector parser.
 */
public final class TikaUsageService
{
    private final static Logger LOG = LoggerFactory.getLogger( TikaUsageService.class );

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

    public void setParser( final Parser parser )
    {
        this.parser = parser;
    }

    public void setDetector( final Detector detector )
    {
        this.detector = detector;
    }
}
