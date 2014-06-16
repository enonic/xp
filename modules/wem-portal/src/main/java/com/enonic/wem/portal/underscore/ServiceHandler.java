package com.enonic.wem.portal.underscore;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.core.InjectParam;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.portal.base.ModuleBaseHandler;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsController;
import com.enonic.wem.portal.controller.JsControllerFactory;
import com.enonic.wem.portal.controller.JsHttpRequest;
import com.enonic.wem.portal.controller.JsHttpResponseSerializer;
import com.enonic.wem.portal.rendering.RenderResult;
import com.enonic.wem.portal.script.lib.PortalUrlScriptBean;

@Path("{mode}/{content:.+}/_/service/{module}/{service}")
public final class ServiceHandler
    extends ModuleBaseHandler
{
    public final static class Params
    {
        @PathParam("mode")
        public String mode;

        @PathParam("content")
        public String content;

        @PathParam("module")
        public String module;

        @PathParam("service")
        public String service;

        @Context
        public Request request;

        @Context
        public UriInfo uriInfo;
    }

    @Inject
    protected JsControllerFactory controllerFactory;

    @GET
    public Response handleGet( @InjectParam Params params )
        throws Exception
    {
        return doHandle( params );
    }

    @POST
    public Response handlePost( @InjectParam Params params )
        throws Exception
    {
        return doHandle( params );
    }

    private Response doHandle( final Params params )
        throws Exception
    {
        final ModuleKey moduleKey = resolveModule( params.content, params.module );
        final JsContext context = new JsContext();

        final JsHttpRequest jsRequest = new JsHttpRequest();
        jsRequest.setMode( params.mode );
        jsRequest.setMethod( params.request.getMethod() );
        jsRequest.addParams( params.uriInfo.getQueryParameters() );
        context.setRequest( jsRequest );

        final PortalUrlScriptBean portalUrlScriptBean = new PortalUrlScriptBean();
        context.setPortalUrlScriptBean( portalUrlScriptBean );

        final JsController controller = this.controllerFactory.newController();

        controller.scriptDir( ModuleResourceKey.from( moduleKey, "service/" + params.service ) );
        controller.context( context );
        controller.execute();

        final RenderResult result = new JsHttpResponseSerializer( context.getResponse() ).serialize();
        return toResponse( result );
    }
}
