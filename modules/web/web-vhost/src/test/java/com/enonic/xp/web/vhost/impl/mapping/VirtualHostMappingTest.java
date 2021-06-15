package com.enonic.xp.web.vhost.impl.mapping;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VirtualHostMappingTest
{
    private VirtualHostMapping virtualHostMapping;

    @Test
    public void testName()
    {
        this.virtualHostMapping = new VirtualHostMapping( "mymapping", "foo.no", "/", "/", VirtualHostIdProvidersMapping.create().build() );
        assertEquals( "mymapping", this.virtualHostMapping.getName() );
    }

    @Test
    public void testHost()
    {
        this.virtualHostMapping = new VirtualHostMapping( "mymapping", "foo.no", "/", "/", VirtualHostIdProvidersMapping.create().build() );
        assertEquals( "foo.no", this.virtualHostMapping.getHost() );
    }

    @Test
    public void testSource()
    {
        this.virtualHostMapping = new VirtualHostMapping( "mymapping", "foo.no", "/", "/", VirtualHostIdProvidersMapping.create().build() );
        assertEquals( "/", this.virtualHostMapping.getSource() );

        this.virtualHostMapping =
            new VirtualHostMapping( "mymapping", "foo.no", "a/b", "/", VirtualHostIdProvidersMapping.create().build() );
        assertEquals( "a/b", this.virtualHostMapping.getSource() );

        assertThrows( NullPointerException.class,
                      () -> new VirtualHostMapping( "mymapping", "foo.no", null, "/", VirtualHostIdProvidersMapping.create().build() ) );
    }

    @Test
    public void testTarget()
    {
        this.virtualHostMapping = new VirtualHostMapping( "mymapping", "foo.no", "/", "/", VirtualHostIdProvidersMapping.create().build() );
        assertEquals( "/", this.virtualHostMapping.getTarget() );

        this.virtualHostMapping =
            new VirtualHostMapping( "mymapping", "foo.no", "/", "a/b", VirtualHostIdProvidersMapping.create().build() );
        assertEquals( "a/b", this.virtualHostMapping.getTarget() );

        assertThrows( NullPointerException.class,
                      () -> new VirtualHostMapping( "mymapping", "foo.no", "/", null, VirtualHostIdProvidersMapping.create().build() ) );
    }

    @Test
    public void testIdProviderKey()
    {
        assertThrows( NullPointerException.class,
                      () -> new VirtualHostMapping( "mymapping", "foo.no", "/", null, VirtualHostIdProvidersMapping.create().
                          setDefaultIdProvider( IdProviderKey.system() ).
                          build() ) );

        this.virtualHostMapping = new VirtualHostMapping( "mymapping", "foo.no", "/", "/", VirtualHostIdProvidersMapping.create().
            setDefaultIdProvider( IdProviderKey.system() ).
            build() );
        assertEquals( IdProviderKey.system(), this.virtualHostMapping.getDefaultIdProviderKey() );
    }

}
