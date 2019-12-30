package com.enonic.xp.extractor.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.extractor.ExtractedData;

class ExtractorResultFactory
{
    private final static Logger LOG = LoggerFactory.getLogger( ExtractorResultFactory.class );

    static ExtractedData create( final Metadata metadata, final BodyContentHandler handler )
    {
        String contentText = handler.toString();
        try
        {
            contentText = ExtractedTextCleaner.clean( contentText );
        }
        catch ( Throwable t )
        {
            LOG.warn( "Error cleaning up extracted text", t );
        }

        return ExtractedData.create().
            metadata( toMap( metadata ) ).
            text( contentText ).
            imageOrientation( metadata.get( Metadata.ORIENTATION ) ).
            build();
    }

    private static Map<String, List<String>> toMap( final Metadata metadata )
    {
        Map<String, List<String>> values = new HashMap<>();

        for ( String name : metadata.names() )
        {
            values.put( name, new ArrayList<>( Arrays.asList( metadata.getValues( name ) ) ) );
        }

        return values;
    }
}
