package com.enonic.wem.core.entity.json;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.repo.Node;

public final class NodeJsonSerializer
{
    protected final ObjectMapper mapper;

    private NodeJsonSerializer( final ObjectMapper mapper )
    {
        this.mapper = mapper;
    }

    public String toString( final Node node )
    {
        try
        {
            return this.mapper.writeValueAsString( NodeJson.toJson( node ) );
        }
        catch ( final JsonProcessingException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    public Node toNode( final String serialized )
    {
        try
        {
            final NodeJson node = this.mapper.readValue( serialized, NodeJson.class );
            return node.fromJson();
        }
        catch ( final IOException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    public static NodeJsonSerializer create( final boolean indent )
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) );
        mapper.disable( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS );
        mapper.disable( SerializationFeature.FAIL_ON_EMPTY_BEANS );
        mapper.enable( MapperFeature.SORT_PROPERTIES_ALPHABETICALLY );
        mapper.enable( SerializationFeature.WRITE_NULL_MAP_VALUES );
        mapper.setSerializationInclusion( JsonInclude.Include.ALWAYS );
        mapper.registerModule( new JSR310Module() );

        if ( indent )
        {
            mapper.enable( SerializationFeature.INDENT_OUTPUT );
        }

        return new NodeJsonSerializer( mapper );
    }
}
