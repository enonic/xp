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

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.jaxrs.JaxRsExceptions;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.util.Exceptions;

@Path("/status")
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
    {
        final ByteArrayOutputStream response = new ByteArrayOutputStream();
        try
        {
            StatusReporter serverReporter = this.serverReporter;
            if (serverReporter == null) {
                throw JaxRsExceptions.notFound( "Server reporter not found" );
            }
            serverReporter.report( response );
        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
        }

        return response.toString();
    }

    @SuppressWarnings("UnusedDeclaration")
    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
    public void addStatusReporter( final StatusReporter reporter )
    {
        if ( SERVER_REPORTER_NAME.equals( reporter.getName() ) )
        {
            this.serverReporter = reporter;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void removeStatusReporter( final StatusReporter reporter )
    {
        if ( SERVER_REPORTER_NAME.equals( reporter.getName() ) )
        {
            this.serverReporter = null;
        }
    }
}
