package com.enonic.xp.server.impl.status;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.MediaType;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.server.impl.status.check.OSGIStateCheck;

public abstract class ProbeServlet
    extends HttpServlet
{
    private final OSGIStateCheck osgiStateCheck;

    public ProbeServlet( final OSGIStateCheck osgiStateCheck )
    {
        this.osgiStateCheck = osgiStateCheck;
    }

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws IOException
    {
        final List<String> errorMessages = osgiStateCheck.check().getErrorMessages();
        serializeJson( res, errorMessages );
    }

    private void serializeJson( final HttpServletResponse res, final List<String> errorMessages )
        throws IOException
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        res.setContentType( MediaType.JSON_UTF_8.toString() );
        final PrintWriter writer = res.getWriter();

        if ( errorMessages.isEmpty() )
        {
            res.setStatus( 200 );

            writer.print( json );
        }
        else
        {
            res.setStatus( 503 );

            final ArrayNode errors = json.putArray( "errors" );
            errorMessages.forEach( errors::add );

            writer.print( json );

        }
        writer.close();
    }

    void deactivate()
    {
        this.osgiStateCheck.deactivate();
    }

}
