package com.enonic.wem.portal.internal.script.lib;


import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.core.web.servlet.ServletRequestHolder;
import com.enonic.wem.core.web.servlet.ServletRequestUrlHelper;
import com.enonic.wem.portal.internal.script.lib.PortalImageUrlBuilder;

import static org.junit.Assert.*;

public class PortalImageUrlBuilderTest
{
    private HttpServletRequest request;

    private String baseUrl;

    @Before
    public void setup()
    {
        this.request = Mockito.mock( HttpServletRequest.class );
        ServletRequestHolder.setRequest( this.request );
        setupRequest( "http", "localhost", 8080, null );
        this.baseUrl = ServletRequestUrlHelper.createUri( "" );
    }

    @Test
    public void createImageUrlWithoutFilters()
    {
        final PortalImageUrlBuilder urlBuilder = PortalImageUrlBuilder.createImageUrl( baseUrl ).
            contentPath( "bildearkiv/trampoliner/jumping-jack-pop/pop_08.jpg" ).
            workspace( "stage" ).
            resourcePath( "pop_08.jpg" );

        assertEquals( "/portal/live/stage/bildearkiv/trampoliner/jumping-jack-pop/pop_08.jpg/_/image/pop_08.jpg", urlBuilder.toString() );
    }

    @Test
    public void createImageUrlWithFilters()
    {
        final PortalImageUrlBuilder urlBuilder = PortalImageUrlBuilder.createImageUrl( baseUrl ).
            contentPath( "bildearkiv/trampoliner/jumping-jack-pop/pop_08.jpg" ).
            workspace( "stage" ).
            resourcePath( "pop_08.jpg" ).
            filter( "scalemax(120)" ).
            filter( Lists.newArrayList( "rounded(40)" ) ).
            filter( "block(3,3)", "sepia()" ).
            background( "00ff00" ).
            quality( 33 );

        assertEquals( "/portal/live/stage/bildearkiv/trampoliner/jumping-jack-pop/pop_08.jpg/_/image/pop_08.jpg" +
                          "?filter=scalemax%28120%29%3Brounded%2840%29%3Bblock%283%2C3%29%3Bsepia%28%29&background=00ff00&quality=33",
                      urlBuilder.toString() );
    }

    @Test
    public void createImageUrlByIdWithoutFilters()
    {
        final PortalImageUrlBuilder urlBuilder = PortalImageUrlBuilder.createImageUrl( baseUrl ).
            contentPath( "mypage" ).
            imageContent( ContentId.from( "abc" ) ).
            workspace( "test" ).
            resourcePath( "pop_08.jpg" );

        assertEquals( "/portal/live/test/mypage/_/image/id/abc", urlBuilder.toString() );
    }

    private void setupRequest( final String scheme, final String host, final int port, final String contextPath )
    {
        Mockito.when( this.request.getScheme() ).thenReturn( scheme );
        Mockito.when( this.request.getServerName() ).thenReturn( host );
        Mockito.when( this.request.getLocalPort() ).thenReturn( port );
        Mockito.when( this.request.getContextPath() ).thenReturn( contextPath );
    }
}
