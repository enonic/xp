package com.enonic.xp.portal.impl.mapper;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.impl.script.AbstractMapSerializableTest;

public class PortalRequestMapperTest
    extends AbstractMapSerializableTest
{
    private PortalRequest portalRequest;

    @Before
    public void setup()
    {
        this.portalRequest = new PortalRequest();
        this.portalRequest.setUri( "/portal/live/master/a/b" );
        this.portalRequest.setBaseUrl( "http://localhost.com/portal/live/master" );
        this.portalRequest.setServerUrl( "http://localhost.com" );
        this.portalRequest.setMethod( "GET" );
        this.portalRequest.getParams().put( "param1", "value1" );
        this.portalRequest.getParams().put( "param2", "value2" );
        this.portalRequest.getParams().put( "param3", "value3-A" );
        this.portalRequest.getParams().put( "param3", "value3-B" );

        this.portalRequest.getHeaders().put( "header1", "value1" );
        this.portalRequest.getHeaders().put( "header2", "value2" );
        this.portalRequest.getHeaders().put( "header3", "value3-A" );
        this.portalRequest.getHeaders().put( "header3", "value3-B" );

        this.portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        this.portalRequest.setContent( ContentFixtures.newContent() );
        this.portalRequest.setSite( ContentFixtures.newSite() );
        this.portalRequest.setPageDescriptor( ContentFixtures.newPageDescriptor() );
    }

    @Test
    public void testSimple()
        throws Exception
    {
        assertJson( "simple", new PortalRequestMapper( this.portalRequest ) );
    }

    @Test
    public void testCookies()
        throws Exception
    {
        this.portalRequest.getCookies().put( "a", "1" );
        this.portalRequest.getCookies().put( "b", "2" );

        assertJson( "cookies", new PortalRequestMapper( this.portalRequest ) );
    }
}
