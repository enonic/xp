package com.enonic.wem.portal.internal.mapper;

import org.junit.Test;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.portal.internal.controller.PortalContextImpl;
import com.enonic.wem.script.AbstractMapSerializableTest;
import com.enonic.wem.script.mapper.ContentFixtures;

public class PortalContextMapperTest
    extends AbstractMapSerializableTest
{
    @Test
    public void testSimple()
        throws Exception
    {
        final PortalContextImpl context = new PortalContextImpl();
        context.setUri( "/portal/live/prod/a/b" );
        context.setMethod( "GET" );
        context.addParam( "param1", "value1" );
        context.addParam( "param2", "value2" );
        context.addParam( "param3", "value3-A" );
        context.addParam( "param3", "value3-B" );

        context.addHeader( "header1", "value1" );
        context.addHeader( "header2", "value2" );
        context.addHeader( "header3", "value3-A" );
        context.addHeader( "header3", "value3-B" );

        context.setModule( ModuleKey.from( "mymodule" ) );
        context.setContent( ContentFixtures.newContent() );
        context.setSite( ContentFixtures.newSite() );
        context.setPageDescriptor( ContentFixtures.newPageDescriptor() );

        assertJson( "simple", new PortalRequestMapper( context ) );
    }
}
