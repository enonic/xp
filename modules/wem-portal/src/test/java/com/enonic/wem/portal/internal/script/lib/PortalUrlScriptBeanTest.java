package com.enonic.wem.portal.internal.script.lib;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.core.web.servlet.ServletRequestHolder;
import com.enonic.wem.portal.url2.GeneralUrlBuilder;
import com.enonic.wem.portal.url2.ImageUrlBuilder;
import com.enonic.wem.portal.url2.PublicUrlBuilder;
import com.enonic.wem.portal.url2.ServiceUrlBuilder;

import static org.junit.Assert.*;

public class PortalUrlScriptBeanTest
{
    @Before
    public void setup()
    {
        final HttpServletRequest request = Mockito.mock( HttpServletRequest.class );
        ServletRequestHolder.setRequest( request );

        Mockito.when( request.getScheme() ).thenReturn( "http" );
        Mockito.when( request.getServerName() ).thenReturn( "localhost" );
        Mockito.when( request.getLocalPort() ).thenReturn( 8080 );
        Mockito.when( request.getContextPath() ).thenReturn( null );
    }

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

        final PublicUrlBuilder urlBuilder = bean.createResourceUrl( "some/path" );

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

        final ServiceUrlBuilder urlBuilder = bean.createServiceUrl( "myservice" );

        assertEquals( "/portal/live/test/a/content/_/service/mymodule-1.0.0/myservice", urlBuilder.toString() );
    }
}
