package com.enonic.wem.portal.internal.mapper;

import org.junit.Test;

import com.enonic.wem.portal.internal.controller.PortalContextImpl;
import com.enonic.wem.portal.internal.controller.PortalRequestImpl;
import com.enonic.wem.script.AbstractMapSerializableTest;

public class PortalContextMapperTest
    extends AbstractMapSerializableTest
{
    @Test
    public void testSimple()
        throws Exception
    {
        final PortalRequestImpl request = new PortalRequestImpl();
        request.setMethod( "GET" );

        final PortalContextImpl context = new PortalContextImpl();
        context.setRequest( request );

        assertJson( "simple", new PortalContextMapper( context ) );
    }
}
