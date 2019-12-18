package com.enonic.xp.web.impl.auth;

import java.util.Base64;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.google.common.net.HttpHeaders;

import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviders;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class BasicAuthFilterTest
{
    private BasicAuthFilter filter;

    private MockHttpServletRequest request;

    private HttpServletResponse response;

    private FilterChain chain;

    private SecurityService securityService;

    @BeforeEach
    public void setup()
    {
        this.request = new MockHttpServletRequest();
        this.response = Mockito.mock( HttpServletResponse.class );
        this.chain = Mockito.mock( FilterChain.class );
        this.securityService = Mockito.mock( SecurityService.class );

        this.filter = new BasicAuthFilter();
        this.filter.setSecurityService( this.securityService );

        final IdProviderKey idProviderKey = IdProviderKey.from( "store" );
        final IdProvider idProvider = IdProvider.create().key( idProviderKey ).build();
        final IdProviders idProviders = IdProviders.from( idProvider );
        Mockito.when( this.securityService.getIdProviders() ).thenReturn( idProviders );
    }

    private void rightAuthentication()
    {
        final User user = User.create().login( "user" ).key( PrincipalKey.ofUser( IdProviderKey.from( "store" ), "user" ) ).build();
        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( AuthenticationInfo.create().user( user ).build() );
    }

    private void wrongAuthentication()
    {
        Mockito.when( this.securityService.authenticate( Mockito.any() ) ).thenReturn( AuthenticationInfo.unAuthenticated() );
    }

    private void doFilter()
        throws Exception
    {
        this.filter.doFilter( this.request, this.response, this.chain );
    }

    private void verifyChain()
        throws Exception
    {
        Mockito.verify( this.chain, Mockito.times( 1 ) ).doFilter( this.request, this.response );
    }

    private void setAuthHeader( final String value )
    {
        this.request.addHeader( HttpHeaders.AUTHORIZATION, value );
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
    }

    @Test
    public void header_wrongFormat()
        throws Exception
    {
        setAuthHeader( "some-value" );
        doFilter();
        verifyChain();
    }

    @Test
    public void header_noCredentials()
        throws Exception
    {
        setAuthHeader( "BASIC" );
        doFilter();
        verifyChain();
    }

    @Test
    public void header_noPassword()
        throws Exception
    {
        setAuthHeader( "BASIC " + base64( "user" ) );
        doFilter();
        verifyChain();
    }

    @Test
    public void header_defaultIdProvider_noAccess()
        throws Exception
    {
        wrongAuthentication();
        setAuthHeader( "BASIC " + base64( "user:wrong" ) );
        doFilter();
        verifyChain();
    }

    @Test
    public void header_defaultIdProvider_authenticated()
        throws Exception
    {
        rightAuthentication();
        setAuthHeader( "BASIC " + base64( "user:password" ) );
        doFilter();
        verifyChain();
    }
}
