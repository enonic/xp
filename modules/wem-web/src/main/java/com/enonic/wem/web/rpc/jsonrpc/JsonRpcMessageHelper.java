package com.enonic.wem.web.rpc.jsonrpc;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.rpc.WebRpcError;
import com.enonic.wem.web.rpc.WebRpcException;
import com.enonic.wem.web.rpc.controller.WebRpcMessageHelper;
import com.enonic.wem.web.rpc.processor.WebRpcRequest;
import com.enonic.wem.web.rpc.processor.WebRpcResponse;

final class JsonRpcMessageHelper
    extends WebRpcMessageHelper
{
    @Override
    protected void toJson( final WebRpcResponse res, final ObjectNode json )
    {
        json.put( "jsonrpc", "2.0" );
        json.put( "id", res.getId() );

        final WebRpcError error = res.getError();
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

    @Override
    protected void doParseSingle( final WebRpcRequest req, final ObjectNode json )
    {
        final JsonNode version = json.get( "jsonrpc" );
        final JsonNode id = json.get( "id" );
        final JsonNode method = json.get( "method" );
        final JsonNode params = json.get( "params" );

        if ( version == null )
        {
            req.setError( WebRpcError.invalidRequest( "Version field must be set" ) );
            return;
        }

        if ( !"2.0".equals( version.asText() ) )
        {
            req.setError( WebRpcError.invalidRequest( "Must be version 2.0" ) );
            return;
        }

        if ( method == null )
        {
            req.setError( WebRpcError.invalidRequest( "Method must be set" ) );
            return;
        }

        req.setId( id != null ? id.asText() : null );
        req.setMethod( method.asText() );

        try
        {
            req.setParams( findData( params ) );
        }
        catch ( final WebRpcException e )
        {
            req.setError( e.getError() );
        }
    }

    private ObjectNode findData( final JsonNode node )
        throws WebRpcException
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

        final WebRpcError error = WebRpcError.invalidRequest( "Only named parameters are supported" );
        throw new WebRpcException( error );
    }
}
