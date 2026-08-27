package com.enonic.xp.portal.impl.idprovider;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IdProviderFilterTest
{
    private IdProviderFilter idProviderFilter;

    private IdProviderControllerService idProviderControllerService;

    @BeforeEach
    void setup()
    {
        idProviderControllerService = Mockito.mock( IdProviderControllerService.class );
        Mockito.when( idProviderControllerService.hasFunction( Mockito.any(), Mockito.any() ) ).thenReturn( true );
        idProviderFilter = new IdProviderFilter( idProviderControllerService );
    }

    @Test
    void testExecuteUnauthenticated()
        throws Exception
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );
        final FilterChain filterChain = Mockito.mock( FilterChain.class );

        idProviderFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );
        Mockito.verify( idProviderControllerService ).execute( Mockito.any() );
        Mockito.verify( filterChain ).doFilter( Mockito.any(), Mockito.any() );
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
                Mockito.verify( idProviderControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
                return null;
            } );

    }

    @Test
    void testAutoLogin_gatedByFlow()
        throws Exception
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );
        final FilterChain filterChain = Mockito.mock( FilterChain.class );

        mockVirtualHost( httpServletRequest, IdProviderKey.system(), Map.of( IdProviderKey.system(), Set.of( IdProviderFlow.LOGIN ) ) );

        idProviderFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );

        // the default id provider does not have the autologin flow, so autoLogin must not run
        Mockito.verify( idProviderControllerService, Mockito.times( 0 ) ).execute( Mockito.any() );
        Mockito.verify( filterChain ).doFilter( Mockito.any(), Mockito.any() );
    }

    @Test
    void testAutoLogin_fallsBackToEnabledIdProvider()
        throws Exception
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );
        final FilterChain filterChain = Mockito.mock( FilterChain.class );

        final IdProviderKey otherIdProvider = IdProviderKey.from( "other" );
        mockVirtualHost( httpServletRequest, IdProviderKey.system(),
                         Map.of( IdProviderKey.system(), Set.of( IdProviderFlow.LOGIN ), otherIdProvider,
                                 Set.of( IdProviderFlow.AUTOLOGIN ) ) );

        idProviderFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );

        final ArgumentCaptor<IdProviderControllerExecutionParams> paramsCaptor =
            ArgumentCaptor.forClass( IdProviderControllerExecutionParams.class );
        Mockito.verify( idProviderControllerService ).execute( paramsCaptor.capture() );
        assertEquals( otherIdProvider, paramsCaptor.getValue().getIdProviderKey() );
        assertEquals( "autoLogin", paramsCaptor.getValue().getFunctionName() );
    }

    @Test
    void testAutoLogin_firstImplementingIdProviderTakesControl()
        throws Exception
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );
        final FilterChain filterChain = Mockito.mock( FilterChain.class );

        final IdProviderKey otherIdProvider = IdProviderKey.from( "other" );
        mockVirtualHost( httpServletRequest, IdProviderKey.system(),
                         Map.of( IdProviderKey.system(), Set.of( IdProviderFlow.AUTOLOGIN ), otherIdProvider,
                                 Set.of( IdProviderFlow.AUTOLOGIN ) ) );

        // the default id provider does not implement autoLogin, so the next enabled one takes control
        Mockito.when( idProviderControllerService.hasFunction( IdProviderKey.system(), "autoLogin" ) ).thenReturn( false );

        idProviderFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );

        final ArgumentCaptor<IdProviderControllerExecutionParams> paramsCaptor =
            ArgumentCaptor.forClass( IdProviderControllerExecutionParams.class );
        Mockito.verify( idProviderControllerService, Mockito.times( 1 ) ).execute( paramsCaptor.capture() );
        assertEquals( otherIdProvider, paramsCaptor.getValue().getIdProviderKey() );
        Mockito.verify( filterChain ).doFilter( Mockito.any(), Mockito.any() );
    }

    @Test
    void testAutoLogin_defaultIdProviderFirst()
        throws Exception
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );
        final FilterChain filterChain = Mockito.mock( FilterChain.class );

        final IdProviderKey otherIdProvider = IdProviderKey.from( "other" );
        mockVirtualHost( httpServletRequest, IdProviderKey.system(),
                         Map.of( IdProviderKey.system(), Set.of( IdProviderFlow.AUTOLOGIN ), otherIdProvider,
                                 Set.of( IdProviderFlow.AUTOLOGIN ) ) );

        // every id provider implements autoLogin: only the default one is executed
        idProviderFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );

        final ArgumentCaptor<IdProviderControllerExecutionParams> paramsCaptor =
            ArgumentCaptor.forClass( IdProviderControllerExecutionParams.class );
        Mockito.verify( idProviderControllerService, Mockito.times( 1 ) ).execute( paramsCaptor.capture() );
        assertEquals( IdProviderKey.system(), paramsCaptor.getValue().getIdProviderKey() );
        Mockito.verify( filterChain ).doFilter( Mockito.any(), Mockito.any() );
    }

    @Test
    void testForcedAuthentication_unauthenticated()
        throws Exception
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );
        final FilterChain filterChain = Mockito.mock( FilterChain.class );

        mockVirtualHost( httpServletRequest, IdProviderKey.system(),
                         Map.of( IdProviderKey.system(), Set.of( IdProviderFlow.AUTOLOGIN, IdProviderFlow.FORCED ) ) );

        idProviderFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );

        Mockito.verify( httpServletResponse ).sendError( HttpServletResponse.SC_UNAUTHORIZED );
        Mockito.verify( filterChain, Mockito.times( 0 ) ).doFilter( Mockito.any(), Mockito.any() );
    }

    @Test
    void testForcedAuthentication_authenticated()
    {
        final User user =
            User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) ).displayName( "User 1" ).login( "user1" ).build();
        final AuthenticationInfo authenticationInfo =
            AuthenticationInfo.create().user( user ).principals( RoleKeys.ADMIN_LOGIN ).build();
        ContextBuilder.create().authInfo( authenticationInfo ).build().callWith( () -> {
            final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
            final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );
            final FilterChain filterChain = Mockito.mock( FilterChain.class );

            mockVirtualHost( httpServletRequest, IdProviderKey.system(),
                             Map.of( IdProviderKey.system(), Set.of( IdProviderFlow.AUTOLOGIN, IdProviderFlow.FORCED ) ) );

            idProviderFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );

            Mockito.verify( httpServletResponse, Mockito.times( 0 ) ).sendError( Mockito.anyInt() );
            Mockito.verify( filterChain ).doFilter( Mockito.any(), Mockito.any() );
            return null;
        } );
    }

    @Test
    void testForcedAuthentication_idProviderEndpointExempt()
        throws Exception
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );
        final FilterChain filterChain = Mockito.mock( FilterChain.class );

        Mockito.when( httpServletRequest.getPathInfo() ).thenReturn( "/site/_/idprovider/system/login" );
        mockVirtualHost( httpServletRequest, IdProviderKey.system(),
                         Map.of( IdProviderKey.system(), Set.of( IdProviderFlow.AUTOLOGIN, IdProviderFlow.FORCED ) ) );

        idProviderFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );

        Mockito.verify( httpServletResponse, Mockito.times( 0 ) ).sendError( Mockito.anyInt() );
        Mockito.verify( filterChain ).doFilter( Mockito.any(), Mockito.any() );
    }

    private static void mockVirtualHost( final HttpServletRequest request, final IdProviderKey defaultIdProvider,
                                         final Map<IdProviderKey, Set<IdProviderFlow>> flows )
    {
        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        Mockito.when( virtualHost.getDefaultIdProviderKey() ).thenReturn( defaultIdProvider );
        Mockito.when( virtualHost.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( flows.keySet() ) );
        Mockito.when( virtualHost.getIdProviderFlows( Mockito.any() ) )
            .thenAnswer( invocation -> flows.getOrDefault( invocation.getArgument( 0 ), Set.of() ) );
        Mockito.when( request.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );
    }
}
