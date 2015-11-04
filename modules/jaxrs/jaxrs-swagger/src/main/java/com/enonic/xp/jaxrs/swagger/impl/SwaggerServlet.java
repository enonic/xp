package com.enonic.xp.jaxrs.swagger.impl;

import java.io.IOException;
import java.net.URL;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.Resources;
import com.google.common.net.MediaType;

import io.swagger.models.Swagger;

import com.enonic.xp.jaxrs.JaxRsService;
import com.enonic.xp.util.MediaTypes;
import com.enonic.xp.web.HttpMethod;

@Component(immediate = true, service = Servlet.class,
    property = {"osgi.http.whiteboard.servlet.pattern=/swagger", "osgi.http.whiteboard.servlet.pattern=/swagger/*"})
public final class SwaggerServlet
    extends HttpServlet
{
    private final static String PREFIX = "/swagger";

    private final static String PREFIX_SLASH = PREFIX + "/";

    private final ObjectMapper mapper;

    private JaxRsService jaxRsService;

    public SwaggerServlet()
    {
        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion( JsonInclude.Include.NON_NULL );
        this.mapper.registerModule( new JavaTimeModule() );
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        final HttpMethod method = HttpMethod.valueOf( req.getMethod() );
        if ( !( method == HttpMethod.GET || method == HttpMethod.HEAD ) )
        {
            res.sendError( HttpServletResponse.SC_METHOD_NOT_ALLOWED );
            return;
        }

        final String uri = req.getRequestURI();
        if ( uri.equals( PREFIX ) )
        {
            redirectToIndex( res );
            return;
        }

        if ( uri.equals( PREFIX_SLASH ) )
        {
            redirectToIndex( res );
            return;
        }

        if ( uri.equals( PREFIX_SLASH + "swagger.json" ) )
        {
            serveApiModel( res );
            return;
        }

        serveResource( res, uri.substring( PREFIX_SLASH.length() ) );
    }

    private void redirectToIndex( final HttpServletResponse res )
        throws IOException
    {
        res.sendRedirect( PREFIX_SLASH + "index.html" );
    }

    private void serveApiModel( final HttpServletResponse res )
        throws IOException
    {
        res.setContentType( MediaType.JSON_UTF_8.toString() );
        this.mapper.writeValue( res.getWriter(), readModel() );
    }

    private Swagger readModel()
    {
        final SwaggerModelReader modelReader = new SwaggerModelReader( this.jaxRsService );
        return modelReader.generate();
    }

    private URL findResource( final String path )
    {
        final URL url = getClass().getResource( "/web/" + path );
        if ( url != null )
        {
            return url;
        }

        return getClass().getResource( "/META-INF/resources/webjars/swagger-ui/2.1.2/" + path );
    }

    private void serveResource( final HttpServletResponse res, final String path )
        throws IOException
    {
        final URL url = findResource( path );
        if ( url == null )
        {
            res.sendError( HttpServletResponse.SC_NOT_FOUND );
            return;
        }

        final MediaType type = MediaTypes.instance().fromFile( path );
        res.setContentType( type.toString() );
        Resources.copy( url, res.getOutputStream() );
    }

    @Reference
    public void setJaxRsService( final JaxRsService jaxRsService )
    {
        this.jaxRsService = jaxRsService;
    }
}
