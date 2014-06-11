package com.enonic.wem.portal.underscore;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.InjectParam;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsController;
import com.enonic.wem.portal.controller.JsControllerFactory;
import com.enonic.wem.portal.controller.JsHttpRequest;
import com.enonic.wem.portal.controller.JsHttpResponseSerializer;
import com.enonic.wem.portal.script.lib.PortalUrlScriptBean;

@Path("{mode}/{path:.+}/_/service/{module}/{service}")
public final class OldServicesResource
    extends OldUnderscoreResource
{
    public final class Request
    {
        @PathParam("mode")
        public String mode;

        @PathParam("path")
        public String contentPath;

        @PathParam("module")
        public String moduleName;

        @PathParam("service")
        public String serviceName;
    }

    @Context
    protected HttpContext httpContext;

    @Inject
    protected JsControllerFactory controllerFactory;

    @GET
    public Response handleGet( @InjectParam Request request )
        throws Exception
    {
        return doHandle( request );
    }

    @POST
    public Response handlePost( @InjectParam Request request )
        throws Exception
    {
        return doHandle( request );
    }

    @OPTIONS
    public Response handleOptions( @InjectParam Request request )
        throws Exception
    {
        return doHandle( request );
    }

    private Response doHandle( final Request request )
        throws Exception
    {
        final ModuleKey moduleKey = resolveModule( request.contentPath, request.moduleName );
        final JsContext context = new JsContext();

        final JsHttpRequest jsRequest = new JsHttpRequest();
        jsRequest.setMode( request.mode );
        jsRequest.setMethod( this.httpContext.getRequest().getMethod() );
        jsRequest.addParams( this.httpContext.getUriInfo().getQueryParameters() );
        context.setRequest( jsRequest );

        final PortalUrlScriptBean portalUrlScriptBean = new PortalUrlScriptBean();
        context.setPortalUrlScriptBean( portalUrlScriptBean );

        final JsController controller = this.controllerFactory.newController();

        controller.scriptDir( ModuleResourceKey.from( moduleKey, "service/" + request.serviceName ) );
        controller.context( context );
        controller.execute();

        return new JsHttpResponseSerializer( context.getResponse() ).serialize();
    }
}
