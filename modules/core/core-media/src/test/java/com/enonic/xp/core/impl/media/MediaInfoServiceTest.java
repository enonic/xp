package com.enonic.xp.core.impl.media;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.extractor.ExtractedData;
import com.enonic.xp.media.MediaInfo;

import static org.junit.Assert.*;

public class MediaInfoServiceTest
{
    private MediaInfoServiceImpl service;

    @Before
    public void setup()
    {
        this.service = new MediaInfoServiceImpl();
        service.setBinaryExtractor( source -> {
            Map<String, List<String>> data = Maps.newHashMap();
            data.put( HttpHeaders.CONTENT_TYPE, Lists.newArrayList( "image/jpeg" ) );
            data.put( "myExtractedValue", Lists.newArrayList( "fisk" ) );

            return ExtractedData.create().
                metadata( data ).
                text( "myTextValue" ).
                imageOrientation( "1" ).
                build();
        } );
    }

    @Test
    public void createImmutableTextLine_generation()
    {
        final ByteSource byteSource = Resources.asByteSource( getClass().getResource( "NikonD100.jpg" ) );
        final MediaInfo mediaInfo = this.service.parseMediaInfo( byteSource );

        assertEquals( "image/jpeg", mediaInfo.getMediaType() );

        for ( Map.Entry<String, Collection<String>> entry : mediaInfo.getMetadata().asMap().entrySet() )
        {
            System.out.println( "addFormItem( createImmutableTextLine( \"" + entry.getKey() + "\" ).occurrences( 0, 1 ).build() )." );
        }
    }

    @Test
    public void multiple_colorSpace_entries()
    {
        final ByteSource byteSource = Resources.asByteSource( getClass().getResource( "Multiple-colorSpace-entries.jpg" ) );
        final MediaInfo mediaInfo = this.service.parseMediaInfo( byteSource );
    }

    @Test
    public void multiple_FNumber_entries()
    {
        final ByteSource byteSource = Resources.asByteSource( getClass().getResource( "Multiple-FNumber-entries.JPG" ) );
        final MediaInfo mediaInfo = this.service.parseMediaInfo( byteSource );
    }
}
