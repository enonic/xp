package com.enonic.xp.web.vhost.impl;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VirtualHostInternalHelperTest
{

    @Test
    public void testGetFullTargetPath()
    {
        final VirtualHostMapping virtualHostMapping = new VirtualHostMapping( "host" );

        virtualHostMapping.setHost( "foo.no" );
        virtualHostMapping.setSource( "/a" );
        virtualHostMapping.setTarget( "/b/c" );

        final HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getRequestURI() ).thenReturn( "/a/other/service" );

        final String fullTarget = VirtualHostInternalHelper.getFullTargetPath( virtualHostMapping, req );

        assertEquals( fullTarget, "/b/c/other/service" );
    }

}
