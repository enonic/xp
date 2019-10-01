package com.enonic.xp.web.vhost.impl.mapping;

import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class VirtualHostMappingsTest
{
    private VirtualHostMappings mappings;

    @BeforeEach
    public void setup()
    {
        this.mappings = new VirtualHostMappings();
    }

    @Test
    public void testOrder()
    {
        addMapping( "a", "localhost", "/a", "/other/a" );
        addMapping( "b", "localhost", "/b", "/other/b" );
        addMapping( "c", "localhost", "/a/c", "/other/a/c" );
        addMapping( "d", "enonic.com", "/d", "/other/d" );

        final Iterator<VirtualHostMapping> it = this.mappings.iterator();
        assertEquals( "d", it.next().getName() );
        assertEquals( "c", it.next().getName() );
        assertEquals( "a", it.next().getName() );
        assertEquals( "b", it.next().getName() );
    }

    @Test
    public void testResolve()
    {
        addMapping( "a", "localhost", "/", "/other/a" );
        addMapping( "b", "enonic.com", "/", "/other/d" );

        final MockHttpServletRequest req = new MockHttpServletRequest();
        req.setServerName( "enonic.com" );

        final VirtualHostMapping mapping = this.mappings.resolve( req );
        assertNotNull( mapping );
        assertEquals( "b", mapping.getName() );
    }

    @Test
    public void testResolve_notFound()
    {
        addMapping( "a", "localhost", "/", "/other/a" );
        addMapping( "b", "enonic.com", "/", "/other/d" );

        final MockHttpServletRequest req = new MockHttpServletRequest();
        req.setServerName( "foo.no" );

        final VirtualHostMapping mapping = this.mappings.resolve( req );
        assertNull( mapping );
    }

    private void addMapping( final String name, final String host, final String source, final String target )
    {
        final VirtualHostMapping mapping = new VirtualHostMapping( name );
        mapping.setHost( host );
        mapping.setSource( source );
        mapping.setTarget( target );
        this.mappings.add( mapping );
    }
}
