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
        final JsonMapper.Builder mapper = JsonMapper.builder();
        mapper.defaultDateFormat( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) );
        mapper.disable( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS );
        mapper.disable( SerializationFeature.FAIL_ON_EMPTY_BEANS );
        mapper.enable( MapperFeature.SORT_PROPERTIES_ALPHABETICALLY );
        mapper.serializationInclusion( JsonInclude.Include.ALWAYS );
        mapper.addModule( new JavaTimeModule() );
        return mapper.build();
    }
}
