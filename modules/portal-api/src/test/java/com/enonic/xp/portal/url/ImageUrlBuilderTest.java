package com.enonic.xp.portal.url;

import org.junit.Test;

import static org.junit.Assert.*;

public class ImageUrlBuilderTest
    extends AbstractUrlBuilderTest
{
    @Test
    public void createUrlWithoutFilters()
    {
        final ImageUrlBuilder builder = this.builders.imageUrl().
            imageName( "myimage.png" );

        assertEquals( "/root/portal/live/stage/some/path/_/image/myimage.png", builder.toString() );
    }

    @Test
    public void createUrlWithFilters()
    {
        final ImageUrlBuilder builder = this.builders.imageUrl().
            imageName( "myimage.jpg" ).
            filters( "scalemax(120)", "rounded(40)", "block(3,3)", "sepia()" ).
            background( "00ff00" ).
            quality( 33 );

        assertEquals(
            "/root/portal/live/stage/some/path/_/image/myimage.jpg?filter=scalemax%28120%29%3Brounded%2840%29%3Bblock%283%2C3%29%3Bsepia%28%29&background=00ff00&quality=33",
            builder.toString() );
    }

    @Test
    public void createUrlByIdWithoutFilters()
    {
        final ImageUrlBuilder builder = this.builders.imageUrl().
            imageId( "abc" ).
            workspace( "test" );

        assertEquals( "/root/portal/live/test/some/path/_/image/id/abc", builder.toString() );
    }

    @Test
    public void createUrlByIdWithFilters()
    {
        final ImageUrlBuilder builder = this.builders.imageUrl().
            imageId( "abc" ).
            filters( "scalemax(120)", "rounded(40)", "block(3,3)", "sepia()" ).
            background( "00ff00" ).
            quality( 33 );

        assertEquals(
            "/root/portal/live/stage/some/path/_/image/id/abc?filter=scalemax%28120%29%3Brounded%2840%29%3Bblock%283%2C3%29%3Bsepia%28%29&background=00ff00&quality=33",
            builder.toString() );
    }
}
