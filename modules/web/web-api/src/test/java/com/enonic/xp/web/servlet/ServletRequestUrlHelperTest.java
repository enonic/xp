package com.enonic.xp.web.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServletRequestUrlHelperTest
{
    private MockHttpServletRequest req;

    @BeforeEach
    public void setup()
    {
        this.req = new MockHttpServletRequest();
        ServletRequestHolder.setRequest( this.req );
    }

    @Test
    public void createUri()
    {
        final String uri1 = ServletRequestUrlHelper.createUri( null );
        assertEquals( "/", uri1 );

        final String uri2 = ServletRequestUrlHelper.createUri( "" );
        assertEquals( "/", uri2 );

        final String uri3 = ServletRequestUrlHelper.createUri( "a/b" );
        assertEquals( "/a/b", uri3 );

        final String uri4 = ServletRequestUrlHelper.createUri( "/a/b" );
        assertEquals( "/a/b", uri4 );
    }

    @Test
    public void createUriWithHost_http_port_80()
    {
        this.req.setServerName( "localhost" );
        this.req.setScheme( "http" );
        this.req.setServerPort( 80 );

        assertEquals( "http", ServletRequestUrlHelper.getScheme() );
        assertEquals( "localhost", ServletRequestUrlHelper.getHost() );
        assertEquals( 80, ServletRequestUrlHelper.getPort() );
        assertEquals( "http://localhost", ServletRequestUrlHelper.getServerUrl() );
    }

    @Test
    public void createUriWithHost_https_port_443()
    {
        this.req.setServerName( "localhost" );
        this.req.setScheme( "https" );
        this.req.setServerPort( 443 );

        assertEquals( "https", ServletRequestUrlHelper.getScheme() );
        assertEquals( "localhost", ServletRequestUrlHelper.getHost() );
        assertEquals( 443, ServletRequestUrlHelper.getPort() );
        assertEquals( "https://localhost", ServletRequestUrlHelper.getServerUrl() );
    }

    @Test
    public void createUriWithHost_http_port_8080()
    {
        this.req.setServerName( "localhost" );
        this.req.setScheme( "http" );
        this.req.setServerPort( 8080 );

        assertEquals( "http", ServletRequestUrlHelper.getScheme() );
        assertEquals( "localhost", ServletRequestUrlHelper.getHost() );
        assertEquals( 8080, ServletRequestUrlHelper.getPort() );
        assertEquals( "http://localhost:8080", ServletRequestUrlHelper.getServerUrl() );
    }

    @Test
    public void createServerUrl_x_forwarded_headers()
    {
        this.req.setServerName( "localhost" );
        this.req.setScheme( "http" );
        this.req.setServerPort( 8080 );
        this.req.addHeader( ServletRequestUrlHelper.X_FORWARDED_PROTO, "https" );
        this.req.addHeader( ServletRequestUrlHelper.X_FORWARDED_HOST, "127.0.0.1:123" );

        assertEquals( "https", ServletRequestUrlHelper.getScheme() );
        assertEquals( "127.0.0.1", ServletRequestUrlHelper.getHost() );
        assertEquals( 123, ServletRequestUrlHelper.getPort() );
        assertEquals( "https://127.0.0.1:123", ServletRequestUrlHelper.getServerUrl() );
    }

    @Test
    public void createServerUrl_x_forwarded_headers_no_port()
    {
        this.req.setServerName( "localhost" );
        this.req.setScheme( "http" );
        this.req.setServerPort( 8080 );
        this.req.addHeader( ServletRequestUrlHelper.X_FORWARDED_PROTO, "https" );
        this.req.addHeader( ServletRequestUrlHelper.X_FORWARDED_HOST, "127.0.0.1" );

        assertEquals( "https", ServletRequestUrlHelper.getScheme() );
        assertEquals( "127.0.0.1", ServletRequestUrlHelper.getHost() );
        assertEquals( -1, ServletRequestUrlHelper.getPort() );
        assertEquals( "https://127.0.0.1", ServletRequestUrlHelper.getServerUrl() );
    }

    @Test
    public void createServerUrl_x_forwarded_host_header_multi()
    {
        this.req.setServerName( "localhost" );
        this.req.setServerPort( 8080 );
        this.req.addHeader( ServletRequestUrlHelper.X_FORWARDED_HOST, "host1:4000, host2" );

        assertEquals( "host1", ServletRequestUrlHelper.getHost( this.req ) );
        assertEquals( 4000, ServletRequestUrlHelper.getPort( this.req ) );
        assertEquals( "http://host1:4000", ServletRequestUrlHelper.getServerUrl() );
    }

    @Test
    public void createServerUrl_various_x_forwarded_headers()
    {
        this.req.setServerName( "localhost" );
        this.req.setServerPort( 8080 );
        this.req.addHeader( ServletRequestUrlHelper.X_FORWARDED_PROTO, "https" );
        this.req.addHeader( ServletRequestUrlHelper.X_FORWARDED_HOST, "host1, host2" );
        this.req.addHeader( ServletRequestUrlHelper.X_FORWARDED_PORT, "4200, 9090" );

        assertEquals( "host1", ServletRequestUrlHelper.getHost( this.req ) );
        assertEquals( 4200, ServletRequestUrlHelper.getPort( this.req ) );
        assertEquals( "https://host1:4200", ServletRequestUrlHelper.getServerUrl() );
    }

    @Test
    public void rewriteUri_no_vhost()
    {
        VirtualHostHelper.setVirtualHost( this.req, null );

        final String uri = ServletRequestUrlHelper.rewriteUri( "/path/to/page" ).getRewrittenUri();
        assertEquals( "/path/to/page", uri );
    }

    @Test
    public void rewriteUri_vhost()
    {
        final VirtualHost vhost = Mockito.mock( VirtualHost.class );
        VirtualHostHelper.setVirtualHost( this.req, vhost );

        Mockito.when( vhost.getTarget() ).thenReturn( "/" );
        Mockito.when( vhost.getSource() ).thenReturn( "/admin" );

        final UriRewritingResult rewritingResult = ServletRequestUrlHelper.rewriteUri( "/path/to/page" );
        assertEquals( "/admin/path/to/page", rewritingResult.getRewrittenUri() );
        assertFalse( rewritingResult.isOutOfScope() );

        Mockito.when( vhost.getTarget() ).thenReturn( "/root/to/site" );
        final UriRewritingResult rewritingResult2 = ServletRequestUrlHelper.rewriteUri( "/path/to/page" );
        assertEquals( "/path/to/page", rewritingResult2.getRewrittenUri() );
        assertTrue( rewritingResult2.isOutOfScope() );

        Mockito.when( vhost.getTarget() ).thenReturn( "/path/to" );
        final UriRewritingResult rewritingResult3 = ServletRequestUrlHelper.rewriteUri( "/path/to/page" );
        assertEquals( "/admin/page", rewritingResult3.getRewrittenUri() );
        assertFalse( rewritingResult3.isOutOfScope() );
    }

    @Test
    public void getRemoteAddress()
    {
        this.req.setRemoteAddr( "127.0.0.1" );

        assertEquals( "127.0.0.1", ServletRequestUrlHelper.getRemoteAddress( this.req ) );
    }

    @Test
    public void getRemoteAddress_x_forwarded_for_header()
    {
        this.req.setRemoteAddr( "127.0.0.1" );
        this.req.addHeader( ServletRequestUrlHelper.X_FORWARDED_FOR, "10.0.0.1" );

        assertEquals( "10.0.0.1", ServletRequestUrlHelper.getRemoteAddress( this.req ) );
    }

    @Test
    public void contentDispositionAttachment_filename_with_comma()
    {
        final String fileName = "Prisliste for pakker, stykk- og partigods nasjonalt 01.12.2015.pdf";
        assertEquals( "attachment; filename=\"Prisliste for pakker, stykk- og partigods nasjonalt 01.12.2015.pdf\"; " +
                          "filename*=UTF-8''Prisliste%20for%20pakker%2c%20stykk-%20og%20partigods%20nasjonalt%2001.12.2015.pdf",
                      ServletRequestUrlHelper.contentDispositionAttachment( fileName ) );
    }

}
