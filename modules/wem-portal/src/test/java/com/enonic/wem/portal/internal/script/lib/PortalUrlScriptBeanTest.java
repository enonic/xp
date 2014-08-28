package com.enonic.wem.portal.internal.script.lib;

import org.junit.Test;

import com.enonic.wem.api.content.ContentId;

import static org.junit.Assert.*;

public class PortalUrlScriptBeanTest
    extends BasePortalUrlBuilderTest
{
    @Test
    public void getBaseUrl()
    {
        final PortalUrlScriptBean bean = new PortalUrlScriptBean();
        assertEquals( "", bean.getBaseUrl() );
    }

    @Test
    public void createUrl()
    {
        final PortalUrlScriptBean bean = new PortalUrlScriptBean();
        bean.setMode( "edit" );
        bean.setWorkspace( "test" );

        final GeneralUrlBuilder urlBuilder = bean.createUrl( "some/path" );

        assertEquals( "/portal/edit/test/some/path", urlBuilder.toString() );
    }

    @Test
    public void createResourceUrl()
    {
        final PortalUrlScriptBean bean = new PortalUrlScriptBean();
        bean.setWorkspace( "test" );
        bean.setContentPath( "a/content" );
        bean.setModule( "mymodule-1.0.0" );

        final GeneralUrlBuilder urlBuilder = bean.createResourceUrl( "some/path" );

        assertEquals( "/portal/live/test/a/content/_/public/mymodule-1.0.0/some/path", urlBuilder.toString() );
    }

    @Test
    public void createImageUrl()
    {
        final PortalUrlScriptBean bean = new PortalUrlScriptBean();
        bean.setWorkspace( "test" );
        bean.setContentPath( "a/content" );

        final ImageUrlBuilder urlBuilder = bean.createImageUrl( "myimage" );

        assertEquals( "/portal/live/test/a/content/_/image/myimage", urlBuilder.toString() );
    }

    @Test
    public void createImageByIdUrl()
    {
        final PortalUrlScriptBean bean = new PortalUrlScriptBean();
        bean.setWorkspace( "test" );
        bean.setContentPath( "a/content" );

        final ImageUrlBuilder urlBuilder = bean.createImageByIdUrl( ContentId.from( "123" ) );

        assertEquals( "/portal/live/test/a/content/_/image/id/123", urlBuilder.toString() );
    }

    @Test
    public void createServiceUrl()
    {
        final PortalUrlScriptBean bean = new PortalUrlScriptBean();
        bean.setWorkspace( "test" );
        bean.setContentPath( "a/content" );
        bean.setModule( "mymodule-1.0.0" );

        final GeneralUrlBuilder urlBuilder = bean.createServiceUrl( "myservice" );

        assertEquals( "/portal/live/test/a/content/_/service/mymodule-1.0.0/myservice", urlBuilder.toString() );
    }
}
