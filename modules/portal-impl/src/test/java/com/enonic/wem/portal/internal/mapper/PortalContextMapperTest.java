package com.enonic.wem.portal.internal.mapper;

import org.junit.Test;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.script.AbstractMapSerializableTest;
import com.enonic.wem.script.mapper.ContentFixtures;
import com.enonic.xp.portal.PortalContext;

public class PortalContextMapperTest
    extends AbstractMapSerializableTest
{
    @Test
    public void testSimple()
        throws Exception
    {
        final PortalContext context = new PortalContext();
        context.setUri( "/portal/live/online/a/b" );
        context.setMethod( "GET" );
        context.getParams().put( "param1", "value1" );
        context.getParams().put( "param2", "value2" );
        context.getParams().put( "param3", "value3-A" );
        context.getParams().put( "param3", "value3-B" );

        context.getHeaders().put( "header1", "value1" );
        context.getHeaders().put( "header2", "value2" );
        context.getHeaders().put( "header3", "value3-A" );
        context.getHeaders().put( "header3", "value3-B" );

        context.setModule( ModuleKey.from( "mymodule" ) );
        context.setContent( ContentFixtures.newContent() );
        context.setSite( ContentFixtures.newSite() );
        context.setPageDescriptor( ContentFixtures.newPageDescriptor() );

        assertJson( "simple", new PortalRequestMapper( context ) );
    }
}
