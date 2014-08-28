package com.enonic.wem.portal.internal.script.lib;


import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.portal.url.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ImageUrlBuilderTest
    extends BasePortalUrlBuilderTest
{
    @Test
    public void createImageUrlWithoutFilters()
    {
        final ImageUrlBuilder urlBuilder = ImageUrlBuilder.createImageUrl( baseUrl ).
            contentPath( "bildearkiv/trampoliner/jumping-jack-pop/pop_08.jpg" ).
            workspace( "stage" ).
            resourcePath( "pop_08.jpg" );

        assertEquals( "/portal/live/stage/bildearkiv/trampoliner/jumping-jack-pop/pop_08.jpg/_/image/pop_08.jpg", urlBuilder.toString() );
    }

    @Test
    public void createImageUrlWithFilters()
    {
        final ImageUrlBuilder urlBuilder = ImageUrlBuilder.createImageUrl( baseUrl ).
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
        final ImageUrlBuilder urlBuilder = ImageUrlBuilder.createImageUrl( baseUrl ).
            contentPath( "mypage" ).
            imageContent( ContentId.from( "abc" ) ).
            workspace( "test" ).
            resourcePath( "pop_08.jpg" );

        assertEquals( "/portal/live/test/mypage/_/image/id/abc", urlBuilder.toString() );
    }

    @Test
    public void createImageUrlByIdWithFilters()
    {
        final ImageUrlBuilder urlBuilder = ImageUrlBuilder.createImageUrl( baseUrl ).
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
