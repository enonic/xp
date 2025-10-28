package com.enonic.xp.web.vhost.impl.mapping;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VirtualHostMappingTest
{
    private VirtualHostMapping virtualHostMapping;

    @Test
    void testName()
    {
        this.virtualHostMapping = new VirtualHostMapping( "mymapping", "foo.no", "/", "/", VirtualHostIdProvidersMapping.create().build(), 0 );
        assertEquals( "mymapping", this.virtualHostMapping.getName() );
    }

    @Test
    void testHost()
    {
        this.virtualHostMapping = new VirtualHostMapping( "mymapping", "foo.no", "/", "/", VirtualHostIdProvidersMapping.create().build(), 0 );
        assertEquals( "foo.no", this.virtualHostMapping.getHost() );
    }

    @Test
    void testSource()
    {
        this.virtualHostMapping = new VirtualHostMapping( "mymapping", "foo.no", "/", "/", VirtualHostIdProvidersMapping.create().build(), 0 );
        assertEquals( "/", this.virtualHostMapping.getSource() );

        this.virtualHostMapping =
            new VirtualHostMapping( "mymapping", "foo.no", "a/b", "/", VirtualHostIdProvidersMapping.create().build(), 0 );
        assertEquals( "a/b", this.virtualHostMapping.getSource() );

        assertThrows( NullPointerException.class,
                      () -> new VirtualHostMapping( "mymapping", "foo.no", null, "/", VirtualHostIdProvidersMapping.create().build(), 0 ) );
    }

    @Test
    void testTarget()
    {
        this.virtualHostMapping = new VirtualHostMapping( "mymapping", "foo.no", "/", "/", VirtualHostIdProvidersMapping.create().build(), 0 );
        assertEquals( "/", this.virtualHostMapping.getTarget() );

        this.virtualHostMapping =
            new VirtualHostMapping( "mymapping", "foo.no", "/", "a/b", VirtualHostIdProvidersMapping.create().build(), 0 );
        assertEquals( "a/b", this.virtualHostMapping.getTarget() );

        assertThrows( NullPointerException.class,
                      () -> new VirtualHostMapping( "mymapping", "foo.no", "/", null, VirtualHostIdProvidersMapping.create().build(), 0 ) );
    }

    @Test
    void testIdProviderKey()
    {
        assertThrows( NullPointerException.class,
                      () -> new VirtualHostMapping( "mymapping", "foo.no", "/", null, VirtualHostIdProvidersMapping.create().
                          setDefaultIdProvider( IdProviderKey.system() ).
                          build(), 0 ) );

        this.virtualHostMapping = new VirtualHostMapping( "mymapping", "foo.no", "/", "/", VirtualHostIdProvidersMapping.create().
            setDefaultIdProvider( IdProviderKey.system() ).
            build(), 0 );
        assertEquals( IdProviderKey.system(), this.virtualHostMapping.getDefaultIdProviderKey() );
    }

}
