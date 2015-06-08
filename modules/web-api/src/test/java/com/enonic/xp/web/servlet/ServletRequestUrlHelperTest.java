package com.enonic.xp.web.servlet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static org.junit.Assert.*;

public class ServletRequestUrlHelperTest
{
    private MockHttpServletRequest req;

    @Before
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

        final String uri = ServletRequestUrlHelper.createUriWithHost( "/a/b" );
        assertEquals( "http://localhost/a/b", uri );
    }

    @Test
    public void createUriWithHost_https_port_443()
    {
        this.req.setServerName( "localhost" );
        this.req.setScheme( "https" );
        this.req.setServerPort( 443 );

        final String uri = ServletRequestUrlHelper.createUriWithHost( "/a/b" );
        assertEquals( "https://localhost/a/b", uri );
    }

    @Test
    public void createUriWithHost_http_port_8080()
    {
        this.req.setServerName( "localhost" );
        this.req.setScheme( "http" );
        this.req.setServerPort( 8080 );

        final String uri = ServletRequestUrlHelper.createUriWithHost( "/a/b" );
        assertEquals( "http://localhost:8080/a/b", uri );
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

        final String uri1 = ServletRequestUrlHelper.rewriteUri( "/path/to/page" ).getRewrittenUri();
        assertEquals( "/admin/path/to/page", uri1 );

        Mockito.when( vhost.getTarget() ).thenReturn( "/root/to/site" );

        final String uri2 = ServletRequestUrlHelper.rewriteUri( "/path/to/page" ).getRewrittenUri();
        assertEquals( "/path/to/page", uri2 );

        Mockito.when( vhost.getTarget() ).thenReturn( "/path/to" );

        final String uri3 = ServletRequestUrlHelper.rewriteUri( "/path/to/page" ).getRewrittenUri();
        assertEquals( "/admin/page", uri3 );
    }
}
