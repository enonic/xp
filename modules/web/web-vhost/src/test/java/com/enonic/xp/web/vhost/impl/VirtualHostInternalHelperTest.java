package com.enonic.xp.web.vhost.impl;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VirtualHostInternalHelperTest
{

    @Test
    public void testGetFullTargetPath()
    {
        final VirtualHostMapping virtualHostMapping = new VirtualHostMapping( "host" );

        virtualHostMapping.setHost( "foo.no" );
        virtualHostMapping.setSource( "/a" );
        virtualHostMapping.setTarget( "/b/c" );

        final MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI( "/a/other/service" );

        final String fullTarget = VirtualHostInternalHelper.getFullTargetPath( virtualHostMapping, req );

        assertEquals( fullTarget, "/b/c/other/service" );
    }

}
