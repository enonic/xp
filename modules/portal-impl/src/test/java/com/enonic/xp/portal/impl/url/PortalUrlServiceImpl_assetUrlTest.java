package com.enonic.xp.portal.impl.url;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.web.servlet.ServletRequestHolder;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static org.junit.Assert.*;

public class PortalUrlServiceImpl_assetUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl()
    {
        final AssetUrlParams params = new AssetUrlParams().
            context( this.context ).
            path( "css/my.css" );

        final String url = this.service.assetUrl( params );
//        assertEquals( "/portal/draft/context/path/_/asset/mymodule/css/my.css", url );
        assertEquals( "/portal/draft/_/asset/mymodule/css/my.css", url );
    }

    @Test
    public void createUrl_withModule()
    {
        final AssetUrlParams params = new AssetUrlParams().
            context( this.context ).
            module( "othermodule" ).
            path( "css/my.css" );

        final String url = this.service.assetUrl( params );
        assertEquals( "/portal/draft/_/asset/othermodule/css/my.css", url );
    }

    @Test
    public void createUrl_withVirtualHost()
    {
        final AssetUrlParams params = new AssetUrlParams().
            context( this.context ).
            path( "css/my.css" );

        //Mocks a virtual host and the HTTP request
        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        MockHttpServletRequest req = new MockHttpServletRequest();
        ServletRequestHolder.setRequest( req );

        VirtualHostHelper.setVirtualHost( req, virtualHost );

        //Calls the method with a virtual mapping /main -> /
        Mockito.when( virtualHost.getSource() ).thenReturn( "/main" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/" );
        String url = this.service.assetUrl( params );
        assertEquals( "/main/portal/draft/_/asset/mymodule/css/my.css", url );

        //Calls the method with a virtual mapping /main -> /portal/draft/context
        Mockito.when( virtualHost.getSource() ).thenReturn( "/main" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/portal/draft" );
        url = this.service.assetUrl( params );
        assertEquals( "/main/_/asset/mymodule/css/my.css", url );

        //Calls the method with a virtual mapping /main -> /portal/draft/context
        Mockito.when( virtualHost.getSource() ).thenReturn( "/" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/portal/draft/context" );
        url = this.service.assetUrl( params );
        assertEquals( "/_/asset/mymodule/css/my.css", url );

        //Post treatment
        ServletRequestHolder.setRequest( null );
    }
}
