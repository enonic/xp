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
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsController;
import com.enonic.wem.portal.controller.JsControllerFactory;
import com.enonic.wem.portal.controller.JsHttpRequest;

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
        context.setRequest( new JsHttpRequest( this.httpContext.getRequest() ) );

        final JsController controller = this.controllerFactory.newController();

        final ResourcePath localPath = ResourcePath.from( "service/" + this.serviceName );
        controller.scriptDir( new ModuleResourceKey( moduleKey, localPath ) );
        controller.context( context );

        return controller.execute();
    }
}
