package com.enonic.wem.core.media;

import java.io.InputStream;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.parser.AutoDetectParser;
import org.junit.Test;

public class MediaInfoServiceTest
{
    @Test
    public void todo()
    {
        InputStream inputStream = this.getClass().getResourceAsStream( "NikonD100.jpg" );
        MediaInfoService service = new MediaInfoService();
        service.setDetector( new DefaultDetector() );
        service.setParser( new AutoDetectParser() );

        MediaInfo mediaInfo = service.parseMediaInfo( inputStream );
        System.out.println( mediaInfo );
    }
}
