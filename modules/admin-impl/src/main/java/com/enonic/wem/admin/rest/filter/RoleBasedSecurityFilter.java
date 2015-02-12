package com.enonic.wem.admin.rest.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;

@Priority(Priorities.AUTHORIZATION)
final class RoleBasedSecurityFilter
    implements ContainerRequestFilter
{
    protected String[] rolesAllowed;

    protected boolean denyAll;

    protected boolean permitAll;

    public RoleBasedSecurityFilter( final String[] rolesAllowed, final boolean denyAll, final boolean permitAll )
    {
        this.rolesAllowed = rolesAllowed;
        this.denyAll = denyAll;
        this.permitAll = permitAll;
    }

    @Override
    public void filter( final ContainerRequestContext req )
        throws IOException
    {
        if ( this.denyAll )
        {
            throw new ForbiddenException();
        }
        if ( this.permitAll )
        {
            return;
        }
        if ( this.rolesAllowed != null )
        {
            final SecurityContext context = req.getSecurityContext();
            if ( context != null )
            {
                for ( final String role : this.rolesAllowed )
                {
                    if ( context.isUserInRole( role ) )
                    {
                        return;
                    }
                }

                throw new ForbiddenException();
            }
        }
    }
}
