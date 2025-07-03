package com.enonic.xp.extractor.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.tika.metadata.Metadata;

import com.enonic.xp.extractor.ExtractedData;

class ExtractorResultFactory
{
    static ExtractedData create( final Metadata metadata, final String contentText )
    {
        return ExtractedData.create()
            .metadata( toMap( metadata ) )
            .text( ExtractedTextCleaner.clean( contentText ) )
            .imageOrientation( metadata.get( Metadata.ORIENTATION ) )
            .build();
    }

    private static Map<String, List<String>> toMap( final Metadata metadata )
    {
        return Arrays.stream( metadata.names() )
            .collect( Collectors.toUnmodifiableMap( Function.identity(), name -> List.of( metadata.getValues( name ) ) ) );
    }
}
