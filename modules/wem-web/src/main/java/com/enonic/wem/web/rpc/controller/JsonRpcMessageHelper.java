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
import com.enonic.wem.web.rpc.JsonRpcError;
import com.enonic.wem.web.rpc.JsonRpcException;
import com.enonic.wem.web.rpc.processor.JsonRpcRequest;
import com.enonic.wem.web.rpc.processor.JsonRpcResponse;

final class JsonRpcMessageHelper
{
    private final ObjectMapper mapper;

    private final AtomicLong counter;

    public JsonRpcMessageHelper()
    {
        this.mapper = ObjectMapperFactory.create();
        this.counter = new AtomicLong( 0L );
    }

    public final Response toResponse( final JsonRpcException ex )
    {
        final JsonRpcResponse res = new JsonRpcResponse();
        res.setError( ex.getError() );

        return toResponse( res );
    }

    public final Response toResponse( final JsonRpcResponse response )
    {
        final ObjectNode json = toJson( response );
        final JsonRpcError error = response.getError();

        if ( error != null )
        {
            return Response.status( error.getHttpStatus() ).entity( json ).build();
        }
        else
        {
            return Response.ok().entity( json ).build();
        }
    }

    public final Response toResponse( final List<JsonRpcResponse> list )
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
        for ( final JsonRpcResponse item : list )
        {
            result.add( toJson( item ) );
        }

        return Response.ok().entity( result ).build();
    }

    public final JsonRpcRequest createRequest( final String method, final MultivaluedMap<String, String> params )
    {
        final JsonRpcRequest req = new JsonRpcRequest();
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

    public final List<JsonRpcRequest> parseJson( final String json )
        throws JsonRpcException
    {
        try
        {
            return doParseJson( this.mapper.readTree( json ) );
        }
        catch ( final Exception e )
        {
            final JsonRpcError error = JsonRpcError.parseError( e.getMessage() );
            throw new JsonRpcException( error );
        }
    }

    private List<JsonRpcRequest> doParseJson( final JsonNode json )
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

    private List<JsonRpcRequest> doParseArray( final ArrayNode json )
    {
        final List<JsonRpcRequest> list = Lists.newArrayList();
        for ( final JsonNode node : json )
        {
            list.add( doParseSingle( node ) );
        }

        return list;
    }

    private JsonRpcRequest doParseSingle( final JsonNode json )
    {
        final JsonRpcRequest req = new JsonRpcRequest();

        if ( json instanceof ObjectNode )
        {
            doParseSingle( req, (ObjectNode) json );
        }
        else
        {
            req.setError( JsonRpcError.invalidRequest( "Expected json object" ) );
        }

        return req;
    }

    private ObjectNode toJson(final JsonRpcResponse res)
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        toJson( res, json );
        return json;
    }

    private void toJson( final JsonRpcResponse res, final ObjectNode json )
    {
        json.put( "jsonrpc", "2.0" );
        json.put( "id", res.getId() );

        final JsonRpcError error = res.getError();
        if ( error != null )
        {
            final ObjectNode errorJson = json.putObject( "error" );
            errorJson.put( "code", error.getCode() );
            errorJson.put( "message", error.getMessage() );
        }
        else
        {
            json.put( "result", res.getResult() );
        }
    }

    private void doParseSingle( final JsonRpcRequest req, final ObjectNode json )
    {
        final JsonNode version = json.get( "jsonrpc" );
        final JsonNode id = json.get( "id" );
        final JsonNode method = json.get( "method" );
        final JsonNode params = json.get( "params" );

        if ( version == null )
        {
            req.setError( JsonRpcError.invalidRequest( "Version field must be set" ) );
            return;
        }

        if ( !"2.0".equals( version.asText() ) )
        {
            req.setError( JsonRpcError.invalidRequest( "Must be version 2.0" ) );
            return;
        }

        if ( method == null )
        {
            req.setError( JsonRpcError.invalidRequest( "Method must be set" ) );
            return;
        }

        req.setId( id != null ? id.asText() : null );
        req.setMethod( method.asText() );

        try
        {
            req.setParams( findData( params ) );
        }
        catch ( final JsonRpcException e )
        {
            req.setError( e.getError() );
        }
    }

    private ObjectNode findData( final JsonNode node )
        throws JsonRpcException
    {
        if ( node == null )
        {
            return null;
        }

        if ( node.isNull() )
        {
            return null;
        }

        if ( node instanceof ObjectNode )
        {
            return (ObjectNode) node;
        }

        final JsonRpcError error = JsonRpcError.invalidRequest( "Only named parameters are supported" );
        throw new JsonRpcException( error );
    }
}
