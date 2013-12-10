package com.enonic.wem.portal.services;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.core.HttpContext;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.portal.controller.JsControllerFactory;

@Path("{mode}/{path:.+}/_/services/{module}/{service}")
public final class ServicesResource
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

    @Inject
    protected SystemConfig systemConfig;

    @GET
    public Response handleGet()
    {
        return doHandle();
    }

    @POST
    public Response handlePost()
    {
        return doHandle();
    }

    @OPTIONS
    public Response handleOptions()
    {
        return doHandle();
    }

    private Response doHandle()
    {
        final ServicesHandler handler = new ServicesHandler();
        handler.setModulesDir( this.systemConfig.getModulesDir() );
        handler.setMode( this.mode );
        handler.setContentPath( ContentPath.from( this.contentPath ) );
        handler.setModuleName( ModuleName.from( this.moduleName ) );
        handler.setServiceName( this.serviceName );
        handler.setHttpContext( this.httpContext );
        handler.setControllerFactory( this.controllerFactory );
        return handler.handle();
    }
}
