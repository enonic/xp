package com.enonic.wem.core.media;

import java.util.Map;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.parser.DefaultParser;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

import static org.junit.Assert.*;

public class MediaInfoServiceTest
{
    private MediaInfoService service;

    @Before
    public void setup()
    {
        this.service = new MediaInfoService();
        this.service.setDetector( new DefaultDetector() );
        this.service.setParser( new DefaultParser() );
    }

    @Test
    public void createImmutableTextLine_generation()
    {
        final ByteSource byteSource = Resources.asByteSource( getClass().getResource( "NikonD100.jpg" ) );
        final MediaInfo mediaInfo = this.service.parseMediaInfo( byteSource );

        assertEquals( "image/jpeg", mediaInfo.getMediaType() );

        for ( final Map.Entry<String, String> entry : mediaInfo.getMetadata().entrySet() )
        {
            System.out.println( "addFormItem( createImmutableTextLine( \"" + entry.getKey() + "\" ).occurrences( 0, 1 ).build() )." );
        }
    }
}
