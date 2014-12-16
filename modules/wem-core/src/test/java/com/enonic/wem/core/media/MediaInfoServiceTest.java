package com.enonic.wem.core.media;

import java.io.File;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.parser.AutoDetectParser;
import org.junit.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

public class MediaInfoServiceTest
{
    @Test
    public void todo()
    {
        ByteSource inputStream = Files.asByteSource( new File( this.getClass().getResource( "NikonD100.jpg" ).getFile() ) );
        MediaInfoService service = new MediaInfoService();
        service.setDetector( new DefaultDetector() );
        service.setParser( new AutoDetectParser() );

        MediaInfo mediaInfo = service.parseMediaInfo( inputStream );
        System.out.println( mediaInfo );
    }
}
