package com.enonic.wem.web.rest.converter;

import java.text.SimpleDateFormat;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

public final class JsonHttpMessageConverter
    extends MappingJacksonHttpMessageConverter
{
    public JsonHttpMessageConverter()
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        mapper.getSerializationConfig().with( SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS );
        mapper.getSerializationConfig().withSerializationInclusion( JsonSerialize.Inclusion.NON_NULL );
        setObjectMapper( mapper );
    }
}
