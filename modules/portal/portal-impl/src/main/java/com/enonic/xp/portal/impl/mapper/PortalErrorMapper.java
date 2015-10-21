package com.enonic.xp.portal.impl.mapper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.enonic.xp.portal.PortalError;
import com.enonic.xp.script.serializer.JsonMapGenerator;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.util.Exceptions;

public final class PortalErrorMapper
    implements MapSerializable
{
    private final PortalError error;

    public PortalErrorMapper( final PortalError error )
    {
        this.error = error;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        final PortalRequestMapper requestMapper = new PortalRequestMapper( this.error.getRequest() );
        final JsonMapGenerator generator = new JsonMapGenerator();
        requestMapper.serialize( generator );
        final JsonNode requestJson = (JsonNode) generator.getRoot();
        final ObjectMapper mapper = new ObjectMapper();
        mapper.enable( SerializationFeature.WRITE_NULL_MAP_VALUES );

        try
        {
            final String requestStr = mapper.writeValueAsString( requestJson );
            gen.value( "status", this.error.getStatus() );
            gen.value( "message", this.error.getMessage() );
            gen.value( "exception", this.error.getException() );
            gen.value( "request", requestStr );
        }
        catch ( final JsonProcessingException e )
        {
            throw Exceptions.unchecked( e );
        }
    }
}
