package com.enonic.xp.core.internal.json;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class ObjectMapperHelper
{
    private ObjectMapperHelper()
    {
    }

    public static ObjectMapper create()
    {
        return JsonMapper.builder()
            .defaultDateFormat( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) )
            .disable( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS )
            .disable( SerializationFeature.FAIL_ON_EMPTY_BEANS )
            .enable( MapperFeature.SORT_PROPERTIES_ALPHABETICALLY )
            .defaultPropertyInclusion( JsonInclude.Value.construct( JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL ) )
            .addModule( new JavaTimeModule() )
            .build();
    }
}
