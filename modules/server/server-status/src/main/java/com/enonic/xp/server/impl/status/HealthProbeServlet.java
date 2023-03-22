package com.enonic.xp.server.impl.status;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.MediaType;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.status.health.HealthCheck;
import com.enonic.xp.status.health.HealthCheckResult;

@Component(immediate = true, service = Servlet.class, property = {"connector=status"})
@Order(-250)
@WebServlet({"/healthz"})
public final class HealthProbeServlet
    extends HttpServlet
{
    private static final Logger LOG = LoggerFactory.getLogger( HealthProbeServlet.class );

    private final List<HealthCheck> healthChecks = new CopyOnWriteArrayList<>();

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        LOG.debug( "Health probe has started" );

        final List<String> errorMessages = healthChecks.stream()
            .map( HealthCheck::isHealthy )
            .filter( HealthCheckResult::isNotHealthy )
            .flatMap( healthCheckResult -> healthCheckResult.getErrorMessages().stream() )
            .collect( Collectors.toList() );

        serializeJson( res, errorMessages );

        LOG.debug( "Health probe has finished" );
    }

    private void serializeJson( final HttpServletResponse res, final List<String> errorMessages )
        throws IOException
    {

        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        String message;
        if ( errorMessages.isEmpty() )
        {
            res.setStatus( 200 );
            message = "XP is healthy!";
        }
        else
        {
            res.setStatus( 500 );
            message = String.format( "XP is not healthy: [%s]", String.join( ", ", errorMessages ) );
        }
        res.setContentType( MediaType.JSON_UTF_8.toString() );

        final PrintWriter writer = res.getWriter();

        writer.println( json.put( "message", message ).toString() );
        writer.close();
    }

    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, policy = ReferencePolicy.DYNAMIC)
    public void addHealthCheck( final HealthCheck healthCheck )
    {
        this.healthChecks.add( healthCheck );
    }

    public void removeHealthCheck( final HealthCheck healthCheck )
    {
        this.healthChecks.remove( healthCheck );
    }
}
