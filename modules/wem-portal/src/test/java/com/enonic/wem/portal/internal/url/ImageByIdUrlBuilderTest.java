package com.enonic.wem.portal.internal.url;


import org.junit.Test;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.portal.PortalUrlBuilder;
import com.enonic.wem.portal.url.ImageByIdUrlBuilder;

import static org.junit.Assert.*;

public class ImageByIdUrlBuilderTest
    extends BasePortalUrlBuilderTest
{

    @Test
    public void createImageUrlWithoutFilters()
    {
        final ImageByIdUrlBuilder urlBuilder = PortalUrlBuilder.createImageByIdUrl( baseUrl ).
            mode( "live" ).
            contentPath( "mypage" ).
            imageContent( ContentId.from( "abc" ) ).
            workspace( "test" ).
            resourcePath( "pop_08.jpg" );

        assertEquals( "/portal/live/test/mypage/_/image/id/abc", urlBuilder.toString() );
    }

    @Test
    public void createImageUrlWithFilters()
    {
        final ImageByIdUrlBuilder urlBuilder = PortalUrlBuilder.createImageByIdUrl( baseUrl ).
            contentPath( ContentPath.from( "bildearkiv/trampoliner/jumping-jack-pop" ) ).
            imageContent( ContentId.from( "abc" ) ).
            workspace( "stage" ).
            resourcePath( "pop_08.jpg" ).
            filter( "scalemax(120)" ).
            filter( "rounded(40)" ).
            filter( "block(3,3)", "sepia()" ).
            background( "00ff00" ).
            quality( 33 );

        assertEquals( "/portal/live/stage/bildearkiv/trampoliner/jumping-jack-pop/_/image/id/abc" +
                          "?filter=scalemax%28120%29%3Brounded%2840%29%3Bblock%283%2C3%29%3Bsepia%28%29&background=00ff00&quality=33",
                      urlBuilder.toString() );
    }

}
