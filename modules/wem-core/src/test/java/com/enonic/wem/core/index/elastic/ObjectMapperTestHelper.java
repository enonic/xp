package com.enonic.wem.core.index.elastic;

import java.text.SimpleDateFormat;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class ObjectMapperTestHelper
{

    public static ObjectMapper create()
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) );
        mapper.disable( SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS );
        mapper.setSerializationInclusion( JsonSerialize.Inclusion.NON_NULL );
        return mapper;
    }
}
