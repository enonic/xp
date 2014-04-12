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

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsController;
import com.enonic.wem.portal.controller.JsControllerFactory;
import com.enonic.wem.portal.controller.JsHttpRequest;
import com.enonic.wem.portal.script.lib.PortalUrlScriptBean;

import static com.enonic.wem.api.rendering.RenderingMode.LIVE;

@Path("{mode}/{path:.+}/_/service/{module}/{service}")
public final class ServicesResource
    extends UnderscoreResource
{
    @PathParam("mode")
    protected String mode;

    @PathParam("path")
    protected String contentPath;

    @PathParam("module")
    protected String moduleName;

    @PathParam("service")
    protected String serviceName;

    @Context
    protected HttpContext httpContext;

    @Inject
    protected JsControllerFactory controllerFactory;

    @GET
    public Response handleGet()
        throws Exception
    {
        return doHandle();
    }

    @POST
    public Response handlePost()
        throws Exception
    {
        return doHandle();
    }

    @OPTIONS
    public Response handleOptions()
        throws Exception
    {
        return doHandle();
    }

    private Response doHandle()
        throws Exception
    {
        final ModuleKey moduleKey = resolveModule( this.contentPath, this.moduleName );
        final JsContext context = new JsContext();

        final JsHttpRequest request = new JsHttpRequest( this.httpContext.getRequest() );
        request.setMode( RenderingMode.from( this.mode, LIVE ) );
        context.setRequest( request );

        final PortalUrlScriptBean portalUrlScriptBean = new PortalUrlScriptBean();
        context.setPortalUrlScriptBean( portalUrlScriptBean );

        final JsController controller = this.controllerFactory.newController();

        controller.scriptDir( ResourceKey.from( moduleKey, "service/" + this.serviceName ) );
        controller.context( context );

        return controller.execute();
    }
}
