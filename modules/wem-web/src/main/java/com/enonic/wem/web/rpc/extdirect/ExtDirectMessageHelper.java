package com.enonic.wem.web.rpc.extdirect;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.rpc.WebRpcError;
import com.enonic.wem.web.rpc.WebRpcException;
import com.enonic.wem.web.rpc.controller.WebRpcMessageHelper;
import com.enonic.wem.web.rpc.processor.WebRpcRequest;
import com.enonic.wem.web.rpc.processor.WebRpcResponse;

final class ExtDirectMessageHelper
    extends WebRpcMessageHelper
{
    @Override
    protected void toJson( final WebRpcResponse res, final ObjectNode json )
    {
        json.put( "tid", res.getId() );
        json.put( "method", res.getMethod() );
        json.put( "action", res.getAction() );

        final WebRpcError error = res.getError();
        if ( error != null )
        {
            json.put( "type", "exception" );
            json.put( "message", error.getMessage() );
        }
        else
        {
            json.put( "type", "rpc" );
            json.put( "result", res.getResult() );
        }
    }

    @Override
    protected void doParseSingle( final WebRpcRequest req, final ObjectNode json )
    {
        final JsonNode tid = json.get( "tid" );
        final JsonNode type = json.get( "type" );
        final JsonNode method = json.get( "method" );
        final JsonNode data = json.get( "data" );
        final JsonNode action = json.get( "action" );

        if ( type == null )
        {
            req.setError( WebRpcError.invalidRequest( "Type field must be set" ) );
            return;
        }

        if ( !"rpc".equals( type.asText() ) )
        {
            req.setError( WebRpcError.invalidRequest( "Rpc is the only supported type" ) );
            return;
        }

        if ( method == null )
        {
            req.setError( WebRpcError.invalidRequest( "Method must be set" ) );
            return;
        }

        req.setId( tid != null ? tid.asText() : null );
        req.setMethod( method.asText() );
        req.setAction( action != null ? action.asText() : null );

        try
        {
            req.setParams( findData( data ) );
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

        if ( node instanceof ArrayNode )
        {
            final ArrayNode array = (ArrayNode) node;
            if ( array.size() == 0 )
            {
                return null;
            }

            if ( array.size() == 1 )
            {
                final JsonNode first = array.get( 0 );
                if ( first instanceof ObjectNode )
                {
                    return (ObjectNode) first;
                }
            }
        }

        final WebRpcError error = WebRpcError.invalidRequest( "Only named parameters are supported" );
        throw new WebRpcException( error );
    }
}
