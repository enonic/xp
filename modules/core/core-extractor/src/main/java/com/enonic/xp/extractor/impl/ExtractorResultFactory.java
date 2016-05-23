package com.enonic.xp.extractor.impl;

import java.util.List;
import java.util.Map;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.BodyContentHandler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.xp.extractor.ExtractedData;

public class ExtractorResultFactory
{
    public static ExtractedData create( final Metadata metadata, final BodyContentHandler handler )
    {
        return ExtractedData.create().
            metadata( toMap( metadata ) ).
            text( ExtractedTextCleaner.clean( handler.toString() ) ).
            imageOrientation( metadata.get( Metadata.ORIENTATION ) ).
            build();
    }

    private final static Map<String, List<String>> toMap( final Metadata metadata )
    {
        Map<String, List<String>> values = Maps.newHashMap();

        for ( String name : metadata.names() )
        {
            values.put( name, Lists.newArrayList( metadata.getValues( name ) ) );
        }

        return values;
    }
}
