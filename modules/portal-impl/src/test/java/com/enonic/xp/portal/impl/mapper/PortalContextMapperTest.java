package com.enonic.xp.portal.impl.mapper;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.impl.script.AbstractMapSerializableTest;

public class PortalContextMapperTest
    extends AbstractMapSerializableTest
{
    private PortalContext context;

    @Before
    public void setup()
    {
        this.context = new PortalContext();
        this.context.setUri( "/portal/live/master/a/b" );
        this.context.setMethod( "GET" );
        this.context.getParams().put( "param1", "value1" );
        this.context.getParams().put( "param2", "value2" );
        this.context.getParams().put( "param3", "value3-A" );
        this.context.getParams().put( "param3", "value3-B" );

        this.context.getHeaders().put( "header1", "value1" );
        this.context.getHeaders().put( "header2", "value2" );
        this.context.getHeaders().put( "header3", "value3-A" );
        this.context.getHeaders().put( "header3", "value3-B" );

        this.context.setModule( ModuleKey.from( "mymodule" ) );
        this.context.setContent( ContentFixtures.newContent() );
        this.context.setSite( ContentFixtures.newSite() );
        this.context.setPageDescriptor( ContentFixtures.newPageDescriptor() );
    }

    @Test
    public void testSimple()
        throws Exception
    {
        assertJson( "simple", new PortalRequestMapper( this.context ) );
    }

    @Test
    public void testCookies()
        throws Exception
    {
        this.context.getCookies().put( "a", "1" );
        this.context.getCookies().put( "b", "2" );

        assertJson( "cookies", new PortalRequestMapper( this.context ) );
    }
}
