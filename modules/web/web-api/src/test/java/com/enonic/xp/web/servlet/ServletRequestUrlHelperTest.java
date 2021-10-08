package com.enonic.xp.web.servlet;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServletRequestUrlHelperTest
{
    private HttpServletRequest req;

    @BeforeEach
    public void setup()
    {
        this.req = mock( HttpServletRequest.class );
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
        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        assertEquals( "http", ServletRequestUrlHelper.getScheme() );
        assertEquals( "localhost", ServletRequestUrlHelper.getHost() );
        assertEquals( 80, ServletRequestUrlHelper.getPort() );
        assertEquals( "http://localhost", ServletRequestUrlHelper.getServerUrl() );
    }

    @Test
    public void createUriWithHost_https_port_443()
    {
        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "https" );
        when( req.getServerPort() ).thenReturn( 443 );

        assertEquals( "https", ServletRequestUrlHelper.getScheme() );
        assertEquals( "localhost", ServletRequestUrlHelper.getHost() );
        assertEquals( 443, ServletRequestUrlHelper.getPort() );
        assertEquals( "https://localhost", ServletRequestUrlHelper.getServerUrl() );
    }

    @Test
    public void createUriWithHost_http_port_8080()
    {
        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 8080 );

        assertEquals( "http", ServletRequestUrlHelper.getScheme() );
        assertEquals( "localhost", ServletRequestUrlHelper.getHost() );
        assertEquals( 8080, ServletRequestUrlHelper.getPort() );
        assertEquals( "http://localhost:8080", ServletRequestUrlHelper.getServerUrl() );
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
        final VirtualHost vhost = mock( VirtualHost.class );
        when( req.getAttribute( VirtualHost.class.getName() ) ).thenReturn( vhost );

        when( vhost.getTarget() ).thenReturn( "/" );
        when( vhost.getSource() ).thenReturn( "/admin" );

        final UriRewritingResult rewritingResult = ServletRequestUrlHelper.rewriteUri( "/path/to/page" );
        assertEquals( "/admin/path/to/page", rewritingResult.getRewrittenUri() );
        assertFalse( rewritingResult.isOutOfScope() );

        when( vhost.getTarget() ).thenReturn( "/root/to/site" );
        final UriRewritingResult rewritingResult2 = ServletRequestUrlHelper.rewriteUri( "/path/to/page" );
        assertEquals( "/path/to/page", rewritingResult2.getRewrittenUri() );
        assertTrue( rewritingResult2.isOutOfScope() );

        when( vhost.getTarget() ).thenReturn( "/path/to" );
        final UriRewritingResult rewritingResult3 = ServletRequestUrlHelper.rewriteUri( "/path/to/page" );
        assertEquals( "/admin/page", rewritingResult3.getRewrittenUri() );
        assertFalse( rewritingResult3.isOutOfScope() );
    }

    @Test
    public void getRemoteAddress()
    {
        when( req.getRemoteAddr() ).thenReturn( "127.0.0.1" );

        assertEquals( "127.0.0.1", ServletRequestUrlHelper.getRemoteAddress( this.req ) );
    }

    @Test
    public void contentDispositionAttachment_filename_with_comma()
    {
        final String fileName = "Prisliste for pakker, stykk- og partigods nasjonalt 01.12.2015.pdf";
        assertEquals( "attachment; filename=\"Prisliste for pakker, stykk- og partigods nasjonalt 01.12.2015.pdf\"; " +
                          "filename*=UTF-8''Prisliste%20for%20pakker%2C%20stykk-%20og%20partigods%20nasjonalt%2001.12.2015.pdf",
                      ServletRequestUrlHelper.contentDispositionAttachment( fileName ) );
    }

}
