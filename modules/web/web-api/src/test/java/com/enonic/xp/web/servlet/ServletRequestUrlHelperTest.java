package com.enonic.xp.web.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServletRequestUrlHelperTest
{
    private HttpServletRequest req;

    @BeforeEach
    void setup()
    {
        this.req = mock( HttpServletRequest.class );
    }

    @Test
    void createUri()
    {
        final String uri4 = ServletRequestUrlHelper.createUri( req, "/a/b" );
        assertEquals( "/a/b", uri4 );
    }

    @Test
    void createUriWithHost_http_port_80()
    {
        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        assertEquals( "http://localhost", ServletRequestUrlHelper.getServerUrl( req ) );
    }

    @Test
    void createUriWithHost_https_port_443()
    {
        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "https" );
        when( req.isSecure() ).thenReturn( true );
        when( req.getServerPort() ).thenReturn( 443 );

        assertEquals( "https://localhost", ServletRequestUrlHelper.getServerUrl( req ) );
    }

    @Test
    void createUriWithHost_http_port_8080()
    {
        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 8080 );

        assertEquals( "http://localhost:8080", ServletRequestUrlHelper.getServerUrl( req ) );
    }

    @Test
    void createUri_no_vhost()
    {
        VirtualHostHelper.setVirtualHost( this.req, null );

        final String uri = ServletRequestUrlHelper.createUri( req, "/path/to/page" );
        assertEquals( "/path/to/page", uri );
    }

    @Test
    void rewriteUri_vhost()
    {
        final VirtualHost vhost = mock( VirtualHost.class );
        when( req.getAttribute( VirtualHost.class.getName() ) ).thenReturn( vhost );

        when( vhost.getTarget() ).thenReturn( "/" );
        when( vhost.getSource() ).thenReturn( "/admin" );

        final UriRewritingResult rewritingResult = ServletRequestUrlHelper.rewriteUri( req, "/path/to/page" );
        assertEquals( "/admin/path/to/page", rewritingResult.getRewrittenUri() );
        assertFalse( rewritingResult.isOutOfScope() );

        when( vhost.getTarget() ).thenReturn( "/root/to/site" );
        final UriRewritingResult rewritingResult2 = ServletRequestUrlHelper.rewriteUri( req, "/path/to/page" );
        assertEquals( "/path/to/page", rewritingResult2.getRewrittenUri() );
        assertTrue( rewritingResult2.isOutOfScope() );

        when( vhost.getTarget() ).thenReturn( "/path/to" );
        final UriRewritingResult rewritingResult3 = ServletRequestUrlHelper.rewriteUri( req, "/path/to/page" );
        assertEquals( "/admin/page", rewritingResult3.getRewrittenUri() );
        assertFalse( rewritingResult3.isOutOfScope() );
    }

    @Test
    void rewriteUri_vhost_trivial()
    {
        final VirtualHost vhost = mock( VirtualHost.class );
        when( req.getAttribute( VirtualHost.class.getName() ) ).thenReturn( vhost );

        when( vhost.getTarget() ).thenReturn( "/" );
        when( vhost.getSource() ).thenReturn( "/" );

        final UriRewritingResult rewritingResult = ServletRequestUrlHelper.rewriteUri( req, "/path/to/page" );
        assertEquals( "/path/to/page", rewritingResult.getRewrittenUri() );
        assertFalse( rewritingResult.isOutOfScope() );

        when( vhost.getTarget() ).thenReturn( "/root/to/site" );
        final UriRewritingResult rewritingResult2 = ServletRequestUrlHelper.rewriteUri( req, "/path/to/page" );
        assertEquals( "/path/to/page", rewritingResult2.getRewrittenUri() );
        assertTrue( rewritingResult2.isOutOfScope() );

        when( vhost.getTarget() ).thenReturn( "/path/to" );
        final UriRewritingResult rewritingResult3 = ServletRequestUrlHelper.rewriteUri( req, "/path/to/page" );
        assertEquals( "/page", rewritingResult3.getRewrittenUri() );
        assertFalse( rewritingResult3.isOutOfScope() );
    }

    @Test
    void contentDispositionAttachment_filename_with_comma()
    {
        final String fileName = "Prisliste for pakker, stykk- og partigods nasjonalt 01.12.2015.pdf";
        assertEquals( "attachment; filename=\"Prisliste for pakker, stykk- og partigods nasjonalt 01.12.2015.pdf\"; " +
                          "filename*=UTF-8''Prisliste%20for%20pakker%2C%20stykk-%20og%20partigods%20nasjonalt%2001.12.2015.pdf",
                      ServletRequestUrlHelper.contentDispositionAttachment( fileName ) );
    }

    @Test
    void createWsUriWithHost_http_port_8080()
    {
        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "ws" );
        when( req.isSecure() ).thenReturn( false );
        when( req.getServerPort() ).thenReturn( 8080 );

        assertEquals( "ws://localhost:8080", ServletRequestUrlHelper.getServerUrl( req ) );
    }

    @Test
    void createWsUriWithHost_http_port_80()
    {
        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "ws" );
        when( req.isSecure() ).thenReturn( false );
        when( req.getServerPort() ).thenReturn( 80 );

        assertEquals( "ws://localhost", ServletRequestUrlHelper.getServerUrl( req ) );
    }

    @Test
    void createWssUriWithHost_https_port_8080()
    {
        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "wss" );
        when( req.isSecure() ).thenReturn( false );
        when( req.getServerPort() ).thenReturn( 8080 );

        assertEquals( "wss://localhost:8080", ServletRequestUrlHelper.getServerUrl( req ) );
    }

    @Test
    void createWssUriWithHost_https_port_443()
    {
        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "wss" );
        when( req.isSecure() ).thenReturn( true );
        when( req.getServerPort() ).thenReturn( 443 );

        assertEquals( "wss://localhost", ServletRequestUrlHelper.getServerUrl( req ) );
    }

    @Test
    void rewriteUri_vhost_outOfScope()
    {
        final VirtualHost vhost = mock( VirtualHost.class );
        when( req.getAttribute( VirtualHost.class.getName() ) ).thenReturn( vhost );

        when( vhost.getTarget() ).thenReturn( "/site/default/draft/enonic" );
        when( vhost.getSource() ).thenReturn( "/no" );

        UriRewritingResult rewritingResult = ServletRequestUrlHelper.rewriteUri( req, "/site/default/draft/enonic-en" );
        assertEquals( "/site/default/draft/enonic-en", rewritingResult.getRewrittenUri() );
        assertTrue( rewritingResult.isOutOfScope() );

        rewritingResult = ServletRequestUrlHelper.rewriteUri( req, "/site/default/draft/enonic" );
        assertEquals( "/no", rewritingResult.getRewrittenUri() );
        assertFalse( rewritingResult.isOutOfScope() );
    }

    @Test
    void rewriteUri_vhost_outOfScope_short()
    {
        final VirtualHost vhost = mock( VirtualHost.class );
        when( req.getAttribute( VirtualHost.class.getName() ) ).thenReturn( vhost );

        when( vhost.getTarget() ).thenReturn( "/site/default/draft/enonic" );
        when( vhost.getSource() ).thenReturn( "/no" );

        UriRewritingResult rewritingResult = ServletRequestUrlHelper.rewriteUri( req, "/site/default" );
        assertEquals( "/site/default", rewritingResult.getRewrittenUri() );
        assertTrue( rewritingResult.isOutOfScope() );
    }

    @Test
    void createUri_admin_queryString()
    {
        final VirtualHost vhost = mock( VirtualHost.class );
        when( req.getAttribute( VirtualHost.class.getName() ) ).thenReturn( vhost );

        when( vhost.getTarget() ).thenReturn( "/xp/admin" );
        when( vhost.getSource() ).thenReturn( "/admin" );

        String rewritingResult = ServletRequestUrlHelper.createUri( req, "/xp/admin/rest?a=b" );
        assertEquals( "/admin/rest?a=b", rewritingResult );
    }

    @Test
    void createUri_queryString()
    {
        final VirtualHost vhost = mock( VirtualHost.class );
        when( req.getAttribute( VirtualHost.class.getName() ) ).thenReturn( vhost );

        when( vhost.getSource() ).thenReturn( "/studio" );
        when( vhost.getTarget() ).thenReturn( "/admin" );
        final String uriRewritingResult = ServletRequestUrlHelper.createUri( req, "/admin?a=3" );

        assertEquals( "/studio?a=3", uriRewritingResult );
    }
}
