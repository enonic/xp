package com.enonic.xp.server.impl.status;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.MediaType;

import com.enonic.xp.server.impl.status.check.StateCheck;

public abstract class ProbeServlet
    extends HttpServlet
{
    private final StateCheck stateCheck;

    @Activate
    public ProbeServlet( final StateCheck stateCheck )
    {
        this.stateCheck = stateCheck;
    }

    abstract String getSuccessMessage();

    abstract String getFailedMessage( final List<String> errorMessages );

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws IOException
    {
        final List<String> errorMessages = stateCheck.check().getErrorMessages();
        serializeJson( res, errorMessages );
    }

    private void serializeJson( final HttpServletResponse res, final List<String> errorMessages )
        throws IOException
    {

        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        String message;
        if ( errorMessages.isEmpty() )
        {
            res.setStatus( 200 );
            message = getSuccessMessage();
        }
        else
        {
            res.setStatus( 500 );
            message = getFailedMessage( errorMessages );
        }
        res.setContentType( MediaType.JSON_UTF_8.toString() );

        final PrintWriter writer = res.getWriter();

        writer.println( json.put( "message", message ).toString() );
        writer.close();
    }

    void deactivate()
    {
        this.stateCheck.deactivate();
    }

}
