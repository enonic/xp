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

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.idprovider.IdProviderControllerExecutionParams;
import com.enonic.xp.portal.idprovider.IdProviderControllerService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
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
        idProviderFilter = new IdProviderFilter( idProviderControllerService );
    }

    @Test
    void testExecuteUnauthenticated()
        throws Exception
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );
        final FilterChain filterChain = Mockito.mock( FilterChain.class );

        mockVirtualHost( httpServletRequest, IdProviderKey.system(), Map.of( IdProviderKey.system(), Set.of( IdProviderFlow.AUTOLOGIN ) ) );

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

                mockVirtualHost( httpServletRequest, IdProviderKey.system(),
                                 Map.of( IdProviderKey.system(), Set.of( IdProviderFlow.AUTOLOGIN ) ) );

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
    void testAutoLogin_noFlowRestriction()
        throws Exception
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );
        final FilterChain filterChain = Mockito.mock( FilterChain.class );

        final VirtualHost virtualHost = mockVirtualHost( httpServletRequest, IdProviderKey.system(),
                                                         Map.of( IdProviderKey.system(), Set.of( IdProviderFlow.LOGIN ) ) );
        Mockito.when( virtualHost.getIdProviderFlows( IdProviderKey.system() ) ).thenReturn( Set.of() );

        idProviderFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );

        final ArgumentCaptor<IdProviderControllerExecutionParams> paramsCaptor =
            ArgumentCaptor.forClass( IdProviderControllerExecutionParams.class );
        Mockito.verify( idProviderControllerService ).execute( paramsCaptor.capture() );
        assertEquals( IdProviderKey.system(), paramsCaptor.getValue().getIdProviderKey() );
        assertEquals( "autoLogin", paramsCaptor.getValue().getFunctionName() );
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

        // execute returns null for the default id provider (autoLogin not implemented), so the
        // next enabled one takes control
        Mockito.when( idProviderControllerService.execute( Mockito.any() ) ).thenAnswer( invocation -> {
            final IdProviderControllerExecutionParams params = invocation.getArgument( 0 );
            return otherIdProvider.equals( params.getIdProviderKey() ) ? PortalResponse.create().build() : null;
        } );

        idProviderFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );

        final ArgumentCaptor<IdProviderControllerExecutionParams> paramsCaptor =
            ArgumentCaptor.forClass( IdProviderControllerExecutionParams.class );
        Mockito.verify( idProviderControllerService, Mockito.times( 2 ) ).execute( paramsCaptor.capture() );
        assertEquals( IdProviderKey.system(), paramsCaptor.getAllValues().get( 0 ).getIdProviderKey() );
        assertEquals( otherIdProvider, paramsCaptor.getAllValues().get( 1 ).getIdProviderKey() );
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
        Mockito.when( idProviderControllerService.execute( Mockito.any() ) ).thenReturn( PortalResponse.create().build() );

        idProviderFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );

        final ArgumentCaptor<IdProviderControllerExecutionParams> paramsCaptor =
            ArgumentCaptor.forClass( IdProviderControllerExecutionParams.class );
        Mockito.verify( idProviderControllerService, Mockito.times( 1 ) ).execute( paramsCaptor.capture() );
        assertEquals( IdProviderKey.system(), paramsCaptor.getValue().getIdProviderKey() );
        Mockito.verify( filterChain ).doFilter( Mockito.any(), Mockito.any() );
    }

    @Test
    void testAutoLogin_stopsWhenAuthenticatedWithoutResponse()
        throws Exception
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );
        final FilterChain filterChain = Mockito.mock( FilterChain.class );

        final IdProviderKey otherIdProvider = IdProviderKey.from( "other" );
        mockVirtualHost( httpServletRequest, IdProviderKey.system(),
                         Map.of( IdProviderKey.system(), Set.of( IdProviderFlow.AUTOLOGIN ), otherIdProvider,
                                 Set.of( IdProviderFlow.AUTOLOGIN ) ) );

        final User user =
            User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) ).displayName( "User 1" ).login( "user1" ).build();
        final AuthenticationInfo authenticationInfo =
            AuthenticationInfo.create().user( user ).principals( RoleKeys.ADMIN_LOGIN ).build();

        // some id providers (e.g. the OIDC id provider) authenticate without returning a response
        Mockito.when( idProviderControllerService.execute( Mockito.any() ) ).thenAnswer( invocation -> {
            ContextAccessor.current().getLocalScope().setAttribute( authenticationInfo );
            return null;
        } );

        ContextBuilder.create().build().callWith( () -> {
            idProviderFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );

            final ArgumentCaptor<IdProviderControllerExecutionParams> paramsCaptor =
                ArgumentCaptor.forClass( IdProviderControllerExecutionParams.class );
            Mockito.verify( idProviderControllerService, Mockito.times( 1 ) ).execute( paramsCaptor.capture() );
            assertEquals( IdProviderKey.system(), paramsCaptor.getValue().getIdProviderKey() );
            Mockito.verify( filterChain ).doFilter( Mockito.any(), Mockito.any() );
            return null;
        } );
    }

    @Test
    void testAllowedPrincipals_unauthenticated()
        throws Exception
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );
        final FilterChain filterChain = Mockito.mock( FilterChain.class );

        final VirtualHost virtualHost = mockVirtualHost( httpServletRequest, IdProviderKey.system(),
                                                         Map.of( IdProviderKey.system(), Set.of( IdProviderFlow.AUTOLOGIN ) ) );
        Mockito.when( virtualHost.getAllowedPrincipals() ).thenReturn( PrincipalKeys.from( RoleKeys.ADMIN_LOGIN ) );

        idProviderFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );

        Mockito.verify( httpServletResponse ).sendError( HttpServletResponse.SC_UNAUTHORIZED );
        Mockito.verify( filterChain, Mockito.times( 0 ) ).doFilter( Mockito.any(), Mockito.any() );
    }

    @Test
    void testAllowedPrincipals_allowed()
    {
        callAuthenticated( ( httpServletRequest, httpServletResponse, filterChain ) -> {
            final VirtualHost virtualHost = mockVirtualHost( httpServletRequest, IdProviderKey.system(),
                                                             Map.of( IdProviderKey.system(), Set.of( IdProviderFlow.AUTOLOGIN ) ) );
            Mockito.when( virtualHost.getAllowedPrincipals() ).thenReturn( PrincipalKeys.from( RoleKeys.ADMIN_LOGIN ) );

            idProviderFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );

            Mockito.verify( httpServletResponse, Mockito.times( 0 ) ).sendError( Mockito.anyInt() );
            Mockito.verify( filterChain ).doFilter( Mockito.any(), Mockito.any() );
        } );
    }

    @Test
    void testAllowedPrincipals_forbidden()
    {
        callAuthenticated( ( httpServletRequest, httpServletResponse, filterChain ) -> {
            final VirtualHost virtualHost = mockVirtualHost( httpServletRequest, IdProviderKey.system(),
                                                             Map.of( IdProviderKey.system(), Set.of( IdProviderFlow.AUTOLOGIN ) ) );
            Mockito.when( virtualHost.getAllowedPrincipals() ).thenReturn( PrincipalKeys.from( RoleKeys.ADMIN ) );

            idProviderFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );

            Mockito.verify( httpServletResponse ).sendError( HttpServletResponse.SC_FORBIDDEN );
            Mockito.verify( filterChain, Mockito.times( 0 ) ).doFilter( Mockito.any(), Mockito.any() );
        } );
    }

    @Test
    void testAllowedPrincipals_idProviderEndpointExempt()
        throws Exception
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );
        final FilterChain filterChain = Mockito.mock( FilterChain.class );

        Mockito.when( httpServletRequest.getPathInfo() ).thenReturn( "/site/_/idprovider/system/login" );
        final VirtualHost virtualHost = mockVirtualHost( httpServletRequest, IdProviderKey.system(),
                                                         Map.of( IdProviderKey.system(), Set.of( IdProviderFlow.AUTOLOGIN ) ) );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/site" );
        Mockito.when( virtualHost.getAllowedPrincipals() ).thenReturn( PrincipalKeys.from( RoleKeys.ADMIN_LOGIN ) );

        idProviderFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );

        Mockito.verify( httpServletResponse, Mockito.times( 0 ) ).sendError( Mockito.anyInt() );
        Mockito.verify( filterChain ).doFilter( Mockito.any(), Mockito.any() );
    }

    @Test
    void testAllowedPrincipals_idProviderEndpointNotOnTargetNotExempt()
        throws Exception
    {
        final HttpServletRequest httpServletRequest = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse httpServletResponse = Mockito.mock( HttpServletResponse.class );
        final FilterChain filterChain = Mockito.mock( FilterChain.class );

        Mockito.when( httpServletRequest.getPathInfo() ).thenReturn( "/site/sub/_/idprovider/system/login" );
        final VirtualHost virtualHost = mockVirtualHost( httpServletRequest, IdProviderKey.system(),
                                                         Map.of( IdProviderKey.system(), Set.of( IdProviderFlow.AUTOLOGIN ) ) );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/site" );
        Mockito.when( virtualHost.getAllowedPrincipals() ).thenReturn( PrincipalKeys.from( RoleKeys.ADMIN_LOGIN ) );

        idProviderFilter.doHandle( httpServletRequest, httpServletResponse, filterChain );

        Mockito.verify( httpServletResponse ).sendError( HttpServletResponse.SC_UNAUTHORIZED );
        Mockito.verify( filterChain, Mockito.times( 0 ) ).doFilter( Mockito.any(), Mockito.any() );
    }

    private interface FilterCall
    {
        void run( HttpServletRequest req, HttpServletResponse res, FilterChain chain )
            throws Exception;
    }

    // Runs the call as a user of the system id provider with the admin.login role.
    private static void callAuthenticated( final FilterCall call )
    {
        final User user =
            User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) ).displayName( "User 1" ).login( "user1" ).build();
        final AuthenticationInfo authenticationInfo =
            AuthenticationInfo.create().user( user ).principals( RoleKeys.ADMIN_LOGIN ).build();
        ContextBuilder.create().authInfo( authenticationInfo ).build().callWith( () -> {
            call.run( Mockito.mock( HttpServletRequest.class ), Mockito.mock( HttpServletResponse.class ),
                      Mockito.mock( FilterChain.class ) );
            return null;
        } );
    }

    private static VirtualHost mockVirtualHost( final HttpServletRequest request, final IdProviderKey defaultIdProvider,
                                                final Map<IdProviderKey, Set<String>> flows )
    {
        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/" );
        Mockito.when( virtualHost.getDefaultIdProviderKey() ).thenReturn( defaultIdProvider );
        Mockito.when( virtualHost.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( flows.keySet() ) );
        Mockito.when( virtualHost.getIdProviderFlows( Mockito.any() ) )
            .thenAnswer( invocation -> flows.getOrDefault( invocation.getArgument( 0 ), Set.of() ) );
        Mockito.when( virtualHost.getAllowedPrincipals() ).thenReturn( PrincipalKeys.empty() );
        Mockito.when( request.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );
        return virtualHost;
    }
}
