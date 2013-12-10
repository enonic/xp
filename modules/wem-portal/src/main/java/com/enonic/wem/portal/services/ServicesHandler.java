package com.enonic.wem.portal.services;

import java.nio.file.Path;
import java.util.Set;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;

import com.google.common.base.Joiner;
import com.sun.jersey.api.core.HttpContext;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsController;
import com.enonic.wem.portal.controller.JsControllerFactory;
import com.enonic.wem.portal.controller.JsHttpRequest;
import com.enonic.wem.portal.controller.JsHttpResponse;
import com.enonic.wem.portal.controller.JsHttpResponseSerializer;
import com.enonic.wem.portal.exception.MethodNotAllowedException;

final class ServicesHandler
{
    private JsControllerFactory controllerFactory;

    private final JsContext context;

    private Path modulesDir;

    private ModuleName moduleName;

    private String serviceName;

    public ServicesHandler()
    {
        this.context = new JsContext();
    }

    public void setControllerFactory( final JsControllerFactory value )
    {
        this.controllerFactory = value;
    }

    public void setModulesDir( final Path modulesDir )
    {
        this.modulesDir = modulesDir;
    }

    public void setMode( final String value )
    {
    }

    public void setContentPath( final ContentPath value )
    {

    }

    public void setModuleName( final ModuleName value )
    {
        this.moduleName = value;
    }

    public void setServiceName( final String value )
    {
        this.serviceName = value;
    }

    public void setHttpContext( final HttpContext value )
    {
        this.context.setRequest( new JsHttpRequest( value.getRequest() ) );
    }

    private Path resolveControllerDir()
    {
        final Path path = this.modulesDir.resolve( this.moduleName.toString() ).resolve( "service" ).resolve( serviceName );
        if ( !path.toFile().isDirectory() )
        {
            throw new IllegalArgumentException( "No such service [" + this.serviceName + "] in module [" + this.moduleName + "]" );
        }

        return path;
    }

    public Response handle()
    {
        final Path path = resolveControllerDir();
        final JsController controller = this.controllerFactory.newController( path );

        if ( controller.execute( this.context ) )
        {
            return handleResponse( this.context.getResponse() );
        }

        if ( this.context.getRequest().getMethod().equals( HttpMethod.OPTIONS ) )
        {
            return handleOptions( controller.getMethods() );
        }

        throw new MethodNotAllowedException();
    }

    private Response handleResponse( final JsHttpResponse other )
    {
        return new JsHttpResponseSerializer( other ).serialize();
    }

    private Response handleOptions( final Set<String> methods )
    {
        return Response.noContent().header( "Allow", Joiner.on( "," ).join( methods ) ).build();
    }
}
