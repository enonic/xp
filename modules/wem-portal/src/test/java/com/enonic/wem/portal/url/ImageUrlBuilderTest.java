package com.enonic.wem.portal.url;


import org.junit.Test;

import static org.junit.Assert.*;

public class ImageUrlBuilderTest
{
    @Test
    public void createImageUrlWithoutFilters()
    {
        final ImageUrlBuilder urlBuilder = new ImageUrlBuilder().
            contentPath( "path/to/content" ).
            workspace( "stage" ).
            imageName( "myimage.jpg" );

        assertEquals( "/portal/live/stage/path/to/content/_/image/myimage.jpg", urlBuilder.toString() );
    }

    @Test
    public void createImageUrlWithFilters()
    {
        final ImageUrlBuilder urlBuilder = new ImageUrlBuilder().
            contentPath( "path/to/content" ).
            workspace( "stage" ).
            imageName( "myimage.jpg" ).
            filter( "scalemax(120)", "rounded(40)", "block(3,3)", "sepia()" ).
            background( "00ff00" ).
            quality( 33 );

        assertEquals( "/portal/live/stage/path/to/content/_/image/myimage.jpg" +
                          "?filter=scalemax%28120%29%3Brounded%2840%29%3Bblock%283%2C3%29%3Bsepia%28%29&background=00ff00&quality=33",
                      urlBuilder.toString() );
    }

    @Test
    public void createImageUrlByIdWithoutFilters()
    {
        final ImageUrlBuilder urlBuilder = new ImageUrlBuilder().
            contentPath( "mypage" ).
            imageId( "abc" ).
            workspace( "test" );

        assertEquals( "/portal/live/test/mypage/_/image/id/abc", urlBuilder.toString() );
    }

    @Test
    public void createImageUrlByIdWithFilters()
    {
        final ImageUrlBuilder urlBuilder = new ImageUrlBuilder().
            contentPath( "path/to/content" ).
            imageId( "abc" ).
            workspace( "stage" ).
            filter( "scalemax(120)", "rounded(40)", "block(3,3)", "sepia()" ).
            background( "00ff00" ).
            quality( 33 );

        assertEquals( "/portal/live/stage/path/to/content/_/image/id/abc" +
                          "?filter=scalemax%28120%29%3Brounded%2840%29%3Bblock%283%2C3%29%3Bsepia%28%29&background=00ff00&quality=33",
                      urlBuilder.toString() );
    }
}
