package com.enonic.xp.admin.event.impl.json;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class ObjectMapperHelper
{
    private ObjectMapperHelper()
    {
    }

    public static ObjectMapper create()
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) );
        mapper.disable( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS );
        mapper.disable( SerializationFeature.FAIL_ON_EMPTY_BEANS );
        mapper.enable( MapperFeature.SORT_PROPERTIES_ALPHABETICALLY );
        mapper.enable( SerializationFeature.WRITE_NULL_MAP_VALUES );
        mapper.setSerializationInclusion( JsonInclude.Include.ALWAYS );
        mapper.registerModule( new JavaTimeModule() );
        return mapper;
    }
}
