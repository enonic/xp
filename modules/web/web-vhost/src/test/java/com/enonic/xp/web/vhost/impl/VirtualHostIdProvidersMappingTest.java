package com.enonic.xp.web.vhost.impl;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.vhost.impl.mapping.VirtualHostIdProvidersMapping;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VirtualHostIdProvidersMappingTest
{
    @Test
    void testGetFullTargetPath()
    {
        final VirtualHostMapping virtualHostMapping =
            new VirtualHostMapping( "host", "foo.no", "/a", "/b/c", VirtualHostIdProvidersMapping.create().build(), 0 );

        final String fullTarget = VirtualHostRequestWrapper.applyVhostMapping( virtualHostMapping, "/a/other/service" );

        assertEquals( "/b/c/other/service", fullTarget );
    }

    @Test
    void testGetFullTargetPath_root_target()
    {
        final VirtualHostMapping virtualHostMapping =
            new VirtualHostMapping( "host", "foo.no", "/a", "/", VirtualHostIdProvidersMapping.create().build(), 0 );

        final String fullTarget = VirtualHostRequestWrapper.applyVhostMapping( virtualHostMapping, "/a/other/service" );

        assertEquals( "/other/service", fullTarget );
    }

    @Test
    void testGetFullTargetPath_source_target_match()
    {
        final VirtualHostMapping virtualHostMapping =
            new VirtualHostMapping( "host", "foo.no", "/a", "/a", VirtualHostIdProvidersMapping.create().build(), 0 );

        final String fullTarget = VirtualHostRequestWrapper.applyVhostMapping( virtualHostMapping, "/a/other/service" );

        assertEquals( "/a/other/service", fullTarget );
    }


    @Test
    void testGetFullTargetPath_source_equals_request()
    {
        final VirtualHostMapping virtualHostMapping =
            new VirtualHostMapping( "host", "foo.no", "/a", "/", VirtualHostIdProvidersMapping.create().build(), 0 );

        final String fullTarget = VirtualHostRequestWrapper.applyVhostMapping( virtualHostMapping, "/a" );

        assertEquals( "/", fullTarget );
    }

    @Test
    void testGetFullTargetPath_source_equals_request_2()
    {
        final VirtualHostMapping virtualHostMapping =
            new VirtualHostMapping( "host", "foo.no", "/a", "/foo", VirtualHostIdProvidersMapping.create().build(), 0 );

        final String fullTarget = VirtualHostRequestWrapper.applyVhostMapping( virtualHostMapping, "/a" );

        assertEquals( "/foo", fullTarget );
    }

    @Test
    void testGetFullTargetPathWithTrailingSlash()
    {
        final VirtualHostMapping virtualHostMapping =
            new VirtualHostMapping( "host", "foo.no", "/a", "/b/c", VirtualHostIdProvidersMapping.create().build(), 0 );

        final String fullTarget = VirtualHostRequestWrapper.applyVhostMapping( virtualHostMapping, "/a/other/service/" );

        assertEquals( "/b/c/other/service/", fullTarget );
    }


    @Test
    void testGetFullTargetPath_root_source()
    {
        final VirtualHostMapping virtualHostMapping =
            new VirtualHostMapping( "host", "foo.no", "/", "/b/c", VirtualHostIdProvidersMapping.create().build(), 0 );

        final String fullTarget = VirtualHostRequestWrapper.applyVhostMapping( virtualHostMapping, "/" );

        assertEquals( "/b/c/", fullTarget );
    }

    @Test
    void testGetFullTargetPath_root_source_2()
    {
        final VirtualHostMapping virtualHostMapping =
            new VirtualHostMapping( "host", "foo.no", "/", "/b/c", VirtualHostIdProvidersMapping.create().build(), 0 );

        final String fullTarget = VirtualHostRequestWrapper.applyVhostMapping( virtualHostMapping, "/d/e" );

        assertEquals( "/b/c/d/e", fullTarget );
    }

    @Test
    void testGetFullTargetPath_root_source_and_target()
    {
        final VirtualHostMapping virtualHostMapping =
            new VirtualHostMapping( "host", "foo.no", "/", "/", VirtualHostIdProvidersMapping.create().build(), 0 );

        final String fullTarget = VirtualHostRequestWrapper.applyVhostMapping( virtualHostMapping, "/b/c" );

        assertEquals( "/b/c", fullTarget );
    }
}
