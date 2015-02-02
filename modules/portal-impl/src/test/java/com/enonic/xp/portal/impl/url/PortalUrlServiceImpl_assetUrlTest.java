package com.enonic.xp.portal.impl.url;

import org.junit.Test;

import com.enonic.xp.portal.url.AssetUrlParams;

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
        assertEquals( "/portal/draft/context/path/_/asset/mymodule/css/my.css", url );
    }

    @Test
    public void createUrl_withModule()
    {
        final AssetUrlParams params = new AssetUrlParams().
            context( this.context ).
            module( "othermodule" ).
            path( "css/my.css" );

        final String url = this.service.assetUrl( params );
        assertEquals( "/portal/draft/context/path/_/asset/othermodule/css/my.css", url );
    }
}
