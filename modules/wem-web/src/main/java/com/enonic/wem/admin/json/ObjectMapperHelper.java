package com.enonic.wem.admin.json;

import java.text.SimpleDateFormat;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public final class ObjectMapperHelper
{
    public static ObjectMapper create()
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) );
        mapper.disable( SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS );
        mapper.disable( SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS );
        mapper.enable( SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY );
        mapper.setSerializationInclusion( JsonSerialize.Inclusion.NON_NULL );
        return mapper;
    }
}
