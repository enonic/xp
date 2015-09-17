package com.enonic.xp.web.vhost.impl.mapping;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.*;

public class VirtualHostMappingTest
{
    private VirtualHostMapping mapping;

    @Before
    public void setup()
    {
        this.mapping = new VirtualHostMapping( "mymapping" );
    }

    @Test
    public void testGetName()
    {
        assertEquals( "mymapping", this.mapping.getName() );
    }

    @Test
    public void testGetHost()
    {
        assertEquals( "localhost", this.mapping.getHost() );

        this.mapping.setHost( "foo.no" );
        assertEquals( "foo.no", this.mapping.getHost() );

        this.mapping.setHost( null );
        assertEquals( "localhost", this.mapping.getHost() );
    }

    @Test
    public void testGetSource()
    {
        assertEquals( "/", this.mapping.getSource() );

        this.mapping.setSource( "a/b" );
        assertEquals( "/a/b", this.mapping.getSource() );

        this.mapping.setSource( "//a/b/" );
        assertEquals( "/a/b", this.mapping.getSource() );

        this.mapping.setSource( null );
        assertEquals( "/", this.mapping.getSource() );
    }

    @Test
    public void testGetTarget()
    {
        assertEquals( "/", this.mapping.getTarget() );

        this.mapping.setTarget( "a/b" );
        assertEquals( "/a/b", this.mapping.getTarget() );

        this.mapping.setTarget( "//a/b/" );
        assertEquals( "/a/b", this.mapping.getTarget() );

        this.mapping.setTarget( null );
        assertEquals( "/", this.mapping.getTarget() );
    }

    @Test
    public void testGetFullTargetPath()
    {
        this.mapping.setHost( "foo.no" );
        this.mapping.setSource( "/a" );
        this.mapping.setTarget( "/b/c" );

        final MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI( "/a/other/service" );

        final String fullTarget = this.mapping.getFullTargetPath( req );
        assertEquals( fullTarget, "/b/c/other/service" );
    }

    @Test
    public void testMatches_wrongHost()
    {
        this.mapping.setHost( "foo.no" );
        this.mapping.setSource( "/" );
        this.mapping.setTarget( "/a" );

        final MockHttpServletRequest req = new MockHttpServletRequest();
        req.setServerName( "localhost" );
        req.setRequestURI( "/a/b" );

        assertEquals( false, this.mapping.matches( req ) );
    }

    @Test
    public void testMatches_wrongSource()
    {
        this.mapping.setHost( "foo.no" );
        this.mapping.setSource( "/b" );
        this.mapping.setTarget( "/a" );

        final MockHttpServletRequest req = new MockHttpServletRequest();
        req.setServerName( "foo.no" );
        req.setRequestURI( "/a" );

        assertEquals( false, this.mapping.matches( req ) );
    }

    @Test
    public void testMatches_host()
    {
        this.mapping.setHost( "foo.no" );
        this.mapping.setSource( "/" );
        this.mapping.setTarget( "/a" );

        final MockHttpServletRequest req = new MockHttpServletRequest();
        req.setServerName( "foo.no" );
        req.setRequestURI( "/a/b" );

        assertEquals( true, this.mapping.matches( req ) );
    }
}
