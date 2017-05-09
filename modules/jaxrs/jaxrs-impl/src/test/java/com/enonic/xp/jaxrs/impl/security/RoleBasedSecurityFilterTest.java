package com.enonic.xp.jaxrs.impl.security;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RoleBasedSecurityFilterTest
{
    private ContainerRequestContext request;

    @Before
    public void setup()
    {
        this.request = Mockito.mock( ContainerRequestContext.class );
    }

    private RoleBasedSecurityFilter newFilter( final boolean denyAll, final boolean permitAll, final String... rolesAllowed )
    {
        return new RoleBasedSecurityFilter( rolesAllowed, denyAll, permitAll );
    }

    private void mockSecurityContext( final String role )
    {
        final SecurityContext context = Mockito.mock( SecurityContext.class );
        Mockito.when( context.isUserInRole( role ) ).thenReturn( true );

        Mockito.when( this.request.getSecurityContext() ).thenReturn( context );
    }

    @Test(expected = ForbiddenException.class)
    public void denyAll()
        throws Exception
    {
        newFilter( true, false ).filter( this.request );
    }

    @Test
    public void permitAll()
        throws Exception
    {
        newFilter( false, true ).filter( this.request );
    }

    @Test
    public void noRoles()
        throws Exception
    {
        newFilter( false, false ).filter( this.request );
    }

    @Test
    public void roles_no_context()
        throws Exception
    {
        newFilter( false, false, "admin" ).filter( this.request );
    }

    @Test(expected = ForbiddenException.class)
    public void roles_not_in_roles()
        throws Exception
    {
        mockSecurityContext( "other" );
        newFilter( false, false, "admin" ).filter( this.request );
    }

    @Test
    public void roles_ok()
        throws Exception
    {
        mockSecurityContext( "admin" );
        newFilter( false, false, "admin" ).filter( this.request );
    }
}
