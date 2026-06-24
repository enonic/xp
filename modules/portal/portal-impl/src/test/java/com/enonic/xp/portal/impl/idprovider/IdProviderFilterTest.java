package com.enonic.xp.portal.impl.idprovider;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

class IdProviderFilterTest
{
    private IdProviderFilter idProviderFilter;

    private IdProviderControllerService idProviderControllerService;

    @BeforeEach
    void setup()
    {
        idProviderControllerService = Mockito.mock( IdProviderControllerService.class );
        idProviderFilter = new IdProviderFilter( idProviderControllerService );
    }

    @Test
    void testExecuteUnauthenticated()
        throws Exception
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );
        final FilterChain filterChain = Mockito.mock( FilterChain.class );

        // A vhost whose default id provider has the autologin flow enabled, so the filter runs autoLogin.
        final IdProviderKey idProviderKey = IdProviderKey.from( "myidprovider" );
        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        Mockito.when( virtualHost.getDefaultIdProviderKey() ).thenReturn( idProviderKey );
        Mockito.when( virtualHost.getIdProviderFlows( idProviderKey ) ).thenReturn( Set.of( IdProviderFlow.AUTOLOGIN ) );
        Mockito.when( httpServletRequest.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );
        VirtualHostHelper.setVirtualHost( httpServletRequest, virtualHost );

        idProviderFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );
        Mockito.verify( idProviderControllerService ).executeResponse( Mockito.any() );
    }

    @Test
    void testExecuteAuthenticated()
    {
        final User user = User.create().
            key( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) ).
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

                idProviderFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );
                Mockito.verify( idProviderControllerService, Mockito.times( 0 ) ).executeResponse( Mockito.any() );
                return null;
            } );

    }
}
