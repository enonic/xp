package com.enonic.wem.admin.json.rpc.controller;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.google.common.collect.Lists;

import com.enonic.wem.admin.json.rpc.JsonRpcException;
import com.enonic.wem.admin.json.rpc.processor.JsonRpcProcessor;
import com.enonic.wem.admin.json.rpc.processor.JsonRpcRequest;
import com.enonic.wem.admin.json.rpc.processor.JsonRpcResponse;


@Path("jsonrpc")
@Produces(MediaType.APPLICATION_JSON)
public final class JsonRpcController
{
    private final JsonRpcMessageHelper helper;

    private JsonRpcProcessor processor;

    public JsonRpcController()
    {
        this.helper = new JsonRpcMessageHelper();
    }

    @GET
    @Path("{method}")
    public Response handleGet( @PathParam("method") final String method, @Context final UriInfo uriInfo )
    {
        final JsonRpcRequest req = this.helper.createRequest( method, uriInfo.getQueryParameters() );
        final JsonRpcResponse res = doProcessSingle( req );
        return this.helper.toResponse( res );
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handlePost( final String json )
    {
        try
        {
            return doHandlePost( json );
        }
        catch ( final JsonRpcException e )
        {
            return this.helper.toResponse( e );
        }
    }

    private Response doHandlePost( final String json )
        throws JsonRpcException
    {
        final List<JsonRpcRequest> list = this.helper.parseJson( json );
        final List<JsonRpcResponse> result = doProcessList( list );
        return this.helper.toResponse( result );
    }

    private List<JsonRpcResponse> doProcessList( final List<JsonRpcRequest> list )
    {
        final List<JsonRpcResponse> result = Lists.newArrayList();
        for ( final JsonRpcRequest req : list )
        {
            result.add( doProcessSingle( req ) );
        }

        return result;
    }

    private JsonRpcResponse doProcessSingle( final JsonRpcRequest req )
    {
        if ( req.hasError() )
        {
            return JsonRpcResponse.from( req );
        }

        return this.processor.process( req );
    }

    @Inject
    public void setProcessor( final JsonRpcProcessor processor )
    {
        this.processor = processor;
    }
}
