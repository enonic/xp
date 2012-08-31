package com.enonic.wem.web.rpc.controller;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.wem.web.rest2.provider.ObjectMapperFactory;
import com.enonic.wem.web.rpc.WebRpcError;
import com.enonic.wem.web.rpc.WebRpcException;
import com.enonic.wem.web.rpc.processor.WebRpcRequest;
import com.enonic.wem.web.rpc.processor.WebRpcResponse;

public abstract class WebRpcMessageHelper
{
    private final ObjectMapper mapper;

    private final AtomicLong counter;

    public WebRpcMessageHelper()
    {
        this.mapper = ObjectMapperFactory.create();
        this.counter = new AtomicLong( 0L );
    }

    public final Response toResponse( final WebRpcException ex )
    {
        final WebRpcResponse res = new WebRpcResponse();
        res.setError( ex.getError() );

        return toResponse( res );
    }

    public final Response toResponse( final WebRpcResponse response )
    {
        final ObjectNode json = toJson( response );
        final WebRpcError error = response.getError();

        if ( error != null )
        {
            return Response.status( error.getHttpStatus() ).entity( json ).build();
        }
        else
        {
            return Response.ok().entity( json ).build();
        }
    }

    public final Response toResponse( final List<WebRpcResponse> list )
    {
        if ( list.isEmpty() )
        {
            final ArrayNode json = JsonNodeFactory.instance.arrayNode();
            return Response.ok().entity( json ).build();
        }

        if ( list.size() == 1 )
        {
            return toResponse( list.get( 0 ) );
        }

        final ArrayNode result = JsonNodeFactory.instance.arrayNode();
        for ( final WebRpcResponse item : list )
        {
            result.add( toJson( item ) );
        }

        return Response.ok().entity( result ).build();
    }

    public final WebRpcRequest createRequest( final String method, final MultivaluedMap<String, String> params )
    {
        final WebRpcRequest req = new WebRpcRequest();
        req.setId( String.valueOf( this.counter.incrementAndGet() ) );
        req.setMethod( method );
        req.setParams( createParams( params ) );
        return req;
    }

    private ObjectNode createParams( final MultivaluedMap<String, String> params )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        for ( final String key : params.keySet() )
        {
            json.put( key, createValue( params.get( key ) ) );
        }

        return json;
    }

    private JsonNode createValue( final List<String> values )
    {
        if ( values == null )
        {
            return JsonNodeFactory.instance.nullNode();
        }

        if ( values.isEmpty() )
        {
            return JsonNodeFactory.instance.nullNode();
        }

        if ( values.size() == 1 )
        {
            return JsonNodeFactory.instance.textNode( values.get( 0 ) );
        }

        final ArrayNode array = JsonNodeFactory.instance.arrayNode();
        for ( final String value : values )
        {
            array.add( value );
        }

        return array;
    }

    public final List<WebRpcRequest> parseJson( final String json )
        throws WebRpcException
    {
        try
        {
            return doParseJson( this.mapper.readTree( json ) );
        }
        catch ( final Exception e )
        {
            final WebRpcError error = WebRpcError.parseError( e.getMessage() );
            throw new WebRpcException( error );
        }
    }

    private List<WebRpcRequest> doParseJson( final JsonNode json )
    {
        if ( json instanceof ArrayNode )
        {
            return doParseArray( (ArrayNode) json );
        }
        else
        {
            return ImmutableList.of( doParseSingle( json ) );
        }
    }

    private List<WebRpcRequest> doParseArray( final ArrayNode json )
    {
        final List<WebRpcRequest> list = Lists.newArrayList();
        for ( final JsonNode node : json )
        {
            list.add( doParseSingle( node ) );
        }

        return list;
    }

    private WebRpcRequest doParseSingle( final JsonNode json )
    {
        final WebRpcRequest req = new WebRpcRequest();

        if ( json instanceof ObjectNode )
        {
            doParseSingle( req, (ObjectNode) json );
        }
        else
        {
            req.setError( WebRpcError.invalidRequest( "Expected json object" ) );
        }

        return req;
    }

    private ObjectNode toJson(final WebRpcResponse res)
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        toJson( res, json );
        return json;
    }

    protected abstract void toJson( final WebRpcResponse res, final ObjectNode json );

    protected abstract void doParseSingle( final WebRpcRequest req, final ObjectNode json );
}
