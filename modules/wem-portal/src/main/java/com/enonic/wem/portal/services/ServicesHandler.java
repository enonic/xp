package com.enonic.wem.portal.services;

import java.nio.file.Paths;
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

    public ServicesHandler()
    {
        this.context = new JsContext();
    }

    public void setControllerFactory( final JsControllerFactory value )
    {
        this.controllerFactory = value;
    }

    public void setMode( final String value )
    {
    }

    public void setContentPath( final ContentPath value )
    {

    }

    public void setModuleName( final ModuleName value )
    {
    }

    public void setServiceName( final String value )
    {
    }

    public void setHttpContext( final HttpContext value )
    {
        this.context.setRequest( new JsHttpRequest( value.getRequest() ) );
    }

    public Response handle()
    {
        final JsController controller = this.controllerFactory.newController(
            Paths.get( "/Users/srs/development/cms-homes/cms-5.0-home/modules/mymodule/service/weather" ) );

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
