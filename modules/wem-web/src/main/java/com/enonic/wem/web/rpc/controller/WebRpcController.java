package com.enonic.wem.web.rpc.controller;

import java.util.List;

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

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import com.enonic.wem.web.rpc.WebRpcException;
import com.enonic.wem.web.rpc.processor.WebRpcProcessor;
import com.enonic.wem.web.rpc.processor.WebRpcRequest;
import com.enonic.wem.web.rpc.processor.WebRpcResponse;

@Produces(MediaType.APPLICATION_JSON)
public abstract class WebRpcController
{
    private final WebRpcMessageHelper helper;

    private WebRpcProcessor processor;

    public WebRpcController( final WebRpcMessageHelper helper )
    {
        this.helper = helper;
    }

    @GET
    @Path("{method}")
    public final Response handleGet( @PathParam("method") final String method, @Context final UriInfo uriInfo )
    {
        final WebRpcRequest req = this.helper.createRequest( method, uriInfo.getQueryParameters() );
        final WebRpcResponse res = doProcessSingle( req );
        return this.helper.toResponse( res );
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public final Response handlePost( final String json )
    {
        try
        {
            return doHandlePost( json );
        }
        catch ( final WebRpcException e )
        {
            return this.helper.toResponse( e );
        }
    }

    private Response doHandlePost( final String json )
        throws WebRpcException
    {
        final List<WebRpcRequest> list = this.helper.parseJson( json );
        final List<WebRpcResponse> result = doProcessList( list );
        return this.helper.toResponse( result );
    }

    private List<WebRpcResponse> doProcessList( final List<WebRpcRequest> list )
    {
        final List<WebRpcResponse> result = Lists.newArrayList();
        for ( final WebRpcRequest req : list )
        {
            result.add( doProcessSingle( req ) );
        }

        return result;
    }

    private WebRpcResponse doProcessSingle( final WebRpcRequest req )
    {
        if ( req.hasError() )
        {
            return WebRpcResponse.from( req );
        }

        return this.processor.process( req );
    }

    @Autowired
    public final void setProcessor( final WebRpcProcessor processor )
    {
        this.processor = processor;
    }
}
