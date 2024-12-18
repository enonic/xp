package com.enonic.xp.web.impl.auth;

import java.util.Base64;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.net.HttpHeaders;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviders;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BasicAuthFilterTest
{
    private BasicAuthFilter filter;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private FilterChain chain;

    private SecurityService securityService;

    @BeforeEach
    public void setup()
    {
        ContextAccessor.current().getLocalScope().setSession( new SessionMock() );
        this.request = Mockito.mock( HttpServletRequest.class );
        this.response = Mockito.mock( HttpServletResponse.class );
        this.chain = Mockito.mock( FilterChain.class );
        this.securityService = Mockito.mock( SecurityService.class );

        this.filter = new BasicAuthFilter( this.securityService );

        final IdProviderKey idProviderKey = IdProviderKey.from( "store" );
        final IdProvider idProvider = IdProvider.create().key( idProviderKey ).build();
        final IdProviders idProviders = IdProviders.from( idProvider );
        when( this.securityService.getIdProviders() ).thenReturn( idProviders );
    }

    @AfterEach
    public void tearDown()
    {
        ContextAccessor.current().getLocalScope().setSession( null );
    }

    private AuthenticationInfo goodAuthenticationInfo()
    {
        final User user = User.create().login( "user" ).key( PrincipalKey.ofUser( IdProviderKey.from( "store" ), "user" ) ).build();
        return AuthenticationInfo.create().user( user ).build();
    }

    private void rightAuthentication()
    {
        when( this.securityService.authenticate( any() ) ).thenReturn( goodAuthenticationInfo() );
    }

    private void wrongAuthentication()
    {
        when( this.securityService.authenticate( any() ) ).thenReturn( AuthenticationInfo.unAuthenticated() );
    }

    private void doFilter()
        throws Exception
    {
        this.filter.doFilter( this.request, this.response, this.chain );
    }

    private void verifyChain()
        throws Exception
    {
        verify( this.chain, Mockito.times( 1 ) ).doFilter( this.request, this.response );
    }

    private void setAuthHeader( final String value )
    {
        when( request.getHeader( HttpHeaders.AUTHORIZATION ) ).thenReturn( value );
    }

    private static void verifyAuthenticated( boolean yes )
    {
        assertEquals( yes, ContextAccessor.current().getAuthInfo().isAuthenticated() );
    }

    private String base64( final String value )
    {
        return Base64.getEncoder().encodeToString( value.getBytes() );
    }

    @Test
    public void noHeader()
        throws Exception
    {
        doFilter();
        verifyChain();
        verifyAuthenticated( false );
    }

    @Test
    public void header_wrongFormat()
        throws Exception
    {
        setAuthHeader( "some-value" );
        doFilter();
        verifyChain();
        verifyAuthenticated( false );
    }

    @Test
    public void header_noCredentials()
        throws Exception
    {
        setAuthHeader( "BASIC" );
        doFilter();
        verifyChain();
        verifyAuthenticated( false );
    }

    @Test
    public void header_noPassword()
        throws Exception
    {
        setAuthHeader( "BASIC " + base64( "user" ) );
        doFilter();
        verifyChain();
        verifyAuthenticated( false );
    }

    @Test
    public void header_defaultIdProvider_noAccess()
        throws Exception
    {
        wrongAuthentication();
        setAuthHeader( "BASIC " + base64( "user:wrong" ) );
        doFilter();
        verifyChain();
        verifyAuthenticated( false );
    }

    @Test
    public void header_defaultIdProvider_authenticated()
        throws Exception
    {
        rightAuthentication();
        setAuthHeader( "BASIC " + base64( "user:password" ) );
        doFilter();
        verifyChain();
        verifyAuthenticated( true );
    }

    @Test
    public void header_defaultIdProvider_authenticated_colon_allowed()
        throws Exception
    {
        rightAuthentication();
        setAuthHeader( "BASIC " + base64( "user:pass:word" ) );
        doFilter();
        verifyChain();
        verifyAuthenticated( true );
    }

    @Test
    public void preAuthenticated()
        throws Exception
    {
        setAuthHeader( "BASIC " + base64( "user:password" ) );
        ContextAccessor.current().getLocalScope().setAttribute( goodAuthenticationInfo() );
        doFilter();
        verifyChain();
        verifyAuthenticated( true );
        verify( this.securityService, never() ).authenticate( any() );
    }
}
