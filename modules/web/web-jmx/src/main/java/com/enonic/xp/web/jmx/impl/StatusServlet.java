package com.enonic.xp.web.jmx.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.common.net.MediaType;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = Servlet.class,
    property = {"osgi.http.whiteboard.servlet.pattern=/status", "osgi.http.whiteboard.servlet.pattern=/status/*"})
public final class StatusServlet
    extends HttpServlet
{
    private final static String PATH_PREFIX = "/status";

    private final static String PATH_PREFIX_SLASH = PATH_PREFIX + "/";

    private final Map<String, StatusReporter> reporters;

    public StatusServlet()
    {
        this.reporters = Maps.newConcurrentMap();
    }

    private JsonNode getRootInfo()
    {
        final ArrayNode json = JsonNodeFactory.instance.arrayNode();
        this.reporters.keySet().forEach( json::add );
        return json;
    }

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        final String path = req.getRequestURI();
        if ( path.equals( PATH_PREFIX ) || path.equals( PATH_PREFIX_SLASH ) )
        {
            reportMainInfo( res );
            return;
        }

        final String name = path.substring( PATH_PREFIX_SLASH.length() ).trim();
        reportFromReporter( res, name );
    }

    private void reportMainInfo( final HttpServletResponse res )
        throws IOException
    {
        serializeJson( res, 200, getRootInfo() );
    }

    private void reportFromReporter( final HttpServletResponse res, final String name )
        throws IOException
    {
        final StatusReporter reporter = this.reporters.get( name );
        if ( reporter == null )
        {
            serializeError( res, 404, "Reporter [%s] not found", name );
            return;
        }

        try
        {
            serializeJson( res, 200, reporter.getReport() );
        }
        catch ( final IOException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            serializeError( res, 500, e.getMessage() );
        }
    }

    private void serializeJson( final HttpServletResponse res, final int status, final JsonNode json )
        throws IOException
    {
        res.setStatus( status );
        res.setContentType( MediaType.JSON_UTF_8.toString() );

        final PrintWriter out = res.getWriter();
        out.println( json.toString() );
        out.close();
    }

    private void serializeError( final HttpServletResponse res, final int status, final String message, final Object... args )
        throws IOException
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "status", status );
        json.put( "message", args.length > 0 ? String.format( message, args ) : message );

        serializeJson( res, status, json );
    }

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
    public void addReporter( final StatusReporter reporter )
    {
        this.reporters.put( reporter.getName(), reporter );
    }

    public void removeReporter( final StatusReporter reporter )
    {
        if ( reporter != null )
        {
            this.reporters.remove( reporter.getName() );
        }
    }
}
