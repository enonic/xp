package com.enonic.xp.impl.server.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.status.StatusReporter;

@Path("/api/status")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class StatusResource
    implements JaxRsComponent
{
    private static final String SERVER_REPORTER_NAME = "server";

    private StatusReporter serverReporter;

    @GET
    @Path("server")
    public String server()
        throws Exception
    {
        final ByteArrayOutputStream response = new ByteArrayOutputStream();
        try
        {
            this.serverReporter.report( response );
        }
        catch ( IOException e )
        {
            final ObjectNode json = JsonNodeFactory.instance.objectNode();
            json.put( "status", 500 );
            json.put( "message", e.getMessage() );

            response.write( json.toString().getBytes( Charsets.UTF_8 ) );
        }

        return response.toString();
    }

    @SuppressWarnings("UnusedDeclaration")
    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
    public void setStatusReporter( final StatusReporter reporter )
    {
        if ( SERVER_REPORTER_NAME.equals( reporter.getName() ) )
        {
            this.serverReporter = reporter;
        }
    }
}
