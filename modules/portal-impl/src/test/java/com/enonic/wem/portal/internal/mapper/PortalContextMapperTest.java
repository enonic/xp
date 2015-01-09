package com.enonic.wem.portal.internal.mapper;

import org.junit.Test;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.portal.internal.controller.PortalContextImpl;
import com.enonic.wem.portal.internal.controller.PortalRequestImpl;
import com.enonic.wem.script.AbstractMapSerializableTest;
import com.enonic.wem.script.mapper.ContentFixtures;

public class PortalContextMapperTest
    extends AbstractMapSerializableTest
{
    @Test
    public void testSimple()
        throws Exception
    {

        final PortalRequestImpl request = new PortalRequestImpl();
        request.setUri( "/portal/live/prod/a/b" );
        request.setMethod( "GET" );
        request.addParam( "param1", "value1" );
        request.addParam( "param2", "value2" );
        request.addParam( "param3", "value3-A" );
        request.addParam( "param3", "value3-B" );

        request.addHeader( "header1", "value1" );
        request.addHeader( "header2", "value2" );
        request.addHeader( "header3", "value3-A" );
        request.addHeader( "header3", "value3-B" );

        final PortalContextImpl context = new PortalContextImpl();
        context.setModule( ModuleKey.from( "mymodule" ) );
        context.setRequest( request );
        context.setContent( ContentFixtures.newContent() );
        context.setSite( ContentFixtures.newSite() );
        context.setPageDescriptor( ContentFixtures.newPageDescriptor() );

        assertJson( "simple", new PortalRequestMapper( context ) );
    }
}
