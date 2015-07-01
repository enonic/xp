package com.enonic.xp.toolbox.repo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

final class RequestJsonSerializer
{
    private final ObjectMapper mapper;

    public RequestJsonSerializer()
    {
        this.mapper = new ObjectMapper();
        this.mapper.enable( MapperFeature.SORT_PROPERTIES_ALPHABETICALLY );
        this.mapper.setSerializationInclusion( JsonInclude.Include.ALWAYS );
    }

    public String serialize( final JsonRequest value )
        throws JsonProcessingException
    {
        return this.mapper.writeValueAsString( value );
    }

}
