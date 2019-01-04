package com.enonic.xp.portal.impl.auth;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.auth.IdProviderControllerService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class AuthFilterTest
{
    private AuthFilter authFilter;

    private IdProviderControllerService idProviderControllerService;

    @Before
    public void setup()
    {
        idProviderControllerService = Mockito.mock( IdProviderControllerService.class );
        authFilter = new AuthFilter();
        authFilter.setIdProviderControllerService( idProviderControllerService );
    }

    @Test
    public void testExecuteUnauthenticated()
        throws Exception
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );
        final FilterChain filterChain = Mockito.mock( FilterChain.class );

        authFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );
        Mockito.verify( idProviderControllerService ).execute( Mockito.any() );
    }

    @Test
    public void testExecuteAuthenticated()
        throws Exception
    {
        final User user = User.create().
            key( PrincipalKey.ofUser( UserStoreKey.system(), "user1" ) ).
            displayName( "User 1" ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();
        final AuthenticationInfo authenticationInfo = AuthenticationInfo.create().
            user( user ).
            principals( RoleKeys.ADMIN_LOGIN ).
            build();
        ContextBuilder.create().
            authInfo( authenticationInfo ).
            build().
            callWith( () -> {
                final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
                final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );
                final FilterChain filterChain = Mockito.mock( FilterChain.class );

                authFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );
                Mockito.verify( idProviderControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
                return null;
            } );

    }
}
