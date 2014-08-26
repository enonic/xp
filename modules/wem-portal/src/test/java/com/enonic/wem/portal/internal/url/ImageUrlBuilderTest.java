package com.enonic.wem.portal.internal.url;


import org.junit.Test;

import com.enonic.wem.portal.PortalUrlBuilder;
import com.enonic.wem.portal.url.ImageUrlBuilder;

import static org.junit.Assert.*;

public class ImageUrlBuilderTest
    extends BasePortalUrlBuilderTest
{

    @Test
    public void createImageUrlWithoutFilters()
    {
        final ImageUrlBuilder urlBuilder = PortalUrlBuilder.createImageUrl( baseUrl ).
            contentPath( "bildearkiv/trampoliner/jumping-jack-pop/pop_08.jpg" ).
            workspace( "stage" ).
            resourcePath( "pop_08.jpg" );

        assertEquals( "/portal/live/stage/bildearkiv/trampoliner/jumping-jack-pop/pop_08.jpg/_/image/pop_08.jpg", urlBuilder.toString() );
    }

    @Test
    public void createImageUrlWithFilters()
    {
        final ImageUrlBuilder urlBuilder = PortalUrlBuilder.createImageUrl( baseUrl ).
            contentPath( "bildearkiv/trampoliner/jumping-jack-pop/pop_08.jpg" ).
            workspace( "stage" ).
            resourcePath( "pop_08.jpg" ).
            filter( "scalemax(120)" ).
            filter( "rounded(40)" ).
            filter( "block(3,3)", "sepia()" ).
            background( "00ff00" ).
            quality( 33 );

        assertEquals( "/portal/live/stage/bildearkiv/trampoliner/jumping-jack-pop/pop_08.jpg/_/image/pop_08.jpg" +
                          "?filter=scalemax%28120%29%3Brounded%2840%29%3Bblock%283%2C3%29%3Bsepia%28%29&background=00ff00&quality=33",
                      urlBuilder.toString() );
    }

}
