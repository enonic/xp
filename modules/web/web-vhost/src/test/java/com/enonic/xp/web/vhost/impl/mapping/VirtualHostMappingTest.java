package com.enonic.xp.web.vhost.impl.mapping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class VirtualHostMappingTest
{
    private VirtualHostMapping virtualHostMapping;

    @BeforeEach
    public void setup()
    {
        this.virtualHostMapping = new VirtualHostMapping( "mymapping" );
    }

    @Test
    public void testGetName()
    {
        assertEquals( "mymapping", this.virtualHostMapping.getName() );
    }

    @Test
    public void testGetHost()
    {
        assertEquals( "localhost", this.virtualHostMapping.getHost() );

        this.virtualHostMapping.setHost( "foo.no" );
        assertEquals( "foo.no", this.virtualHostMapping.getHost() );

        this.virtualHostMapping.setHost( null );
        assertEquals( "localhost", this.virtualHostMapping.getHost() );
    }

    @Test
    public void testGetSource()
    {
        assertEquals( "/", this.virtualHostMapping.getSource() );

        this.virtualHostMapping.setSource( null );
        assertEquals( "/", this.virtualHostMapping.getSource() );

        this.virtualHostMapping.setSource( "a/b" );
        assertEquals( "/a/b", this.virtualHostMapping.getSource() );

        this.virtualHostMapping.setSource( "/a/b" );
        assertEquals( "/a/b", this.virtualHostMapping.getSource() );

        this.virtualHostMapping.setSource( "a/b/" );
        assertEquals( "/a/b", this.virtualHostMapping.getSource() );

        this.virtualHostMapping.setSource( "/a/b/" );
        assertEquals( "/a/b", this.virtualHostMapping.getSource() );

        this.virtualHostMapping.setSource( "/a/b//" );
        assertEquals( "/a/b/", this.virtualHostMapping.getSource() );

        this.virtualHostMapping.setSource( "//a/b/" );
        assertEquals( "//a/b", this.virtualHostMapping.getSource() );
    }

    @Test
    public void testGetTarget()
    {
        assertEquals( "/", this.virtualHostMapping.getTarget() );

        this.virtualHostMapping.setTarget( null );
        assertEquals( "/", this.virtualHostMapping.getTarget() );

        this.virtualHostMapping.setTarget( "a/b" );
        assertEquals( "/a/b", this.virtualHostMapping.getTarget() );

        this.virtualHostMapping.setTarget( "/a/b" );
        assertEquals( "/a/b", this.virtualHostMapping.getTarget() );

        this.virtualHostMapping.setTarget( "a/b/" );
        assertEquals( "/a/b", this.virtualHostMapping.getTarget() );

        this.virtualHostMapping.setTarget( "/a/b/" );
        assertEquals( "/a/b", this.virtualHostMapping.getTarget() );

        this.virtualHostMapping.setTarget( "/a/b//" );
        assertEquals( "/a/b/", this.virtualHostMapping.getTarget() );

        this.virtualHostMapping.setTarget( "//a/b/" );
        assertEquals( "//a/b", this.virtualHostMapping.getTarget() );
    }

    @Test
    public void testGetIdProviderKey()
    {
        assertNull( this.virtualHostMapping.getDefaultIdProviderKey() );

        this.virtualHostMapping.setVirtualHostIdProvidersMapping( VirtualHostIdProvidersMapping.create().
            setDefaultIdProvider( IdProviderKey.system() ).
            build() );

        assertEquals( IdProviderKey.system(), this.virtualHostMapping.getDefaultIdProviderKey() );
    }

}
