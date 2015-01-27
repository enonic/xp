package com.enonic.xp.portal.impl.url;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.script.mapper.ContentFixtures;
import com.enonic.xp.portal.url.ImageUrlParams;

import static org.junit.Assert.*;

public class PortalUrlServiceImpl_imageUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl()
    {
        this.context.setContent( createContent() );

        final ImageUrlParams params = new ImageUrlParams().
            context( this.context );

        final String url = this.service.imageUrl( params );
        assertEquals( "/portal/stage/a/b/mycontent/_/image/123456/mycontent", url );
    }

    @Test
    public void createUrl_withFormat()
    {
        this.context.setContent( createContent() );

        final ImageUrlParams params = new ImageUrlParams().
            format( "png" ).
            context( this.context );

        final String url = this.service.imageUrl( params );
        assertEquals( "/portal/stage/a/b/mycontent/_/image/123456/mycontent.png", url );
    }

    @Test
    public void createUrl_allOptions()
    {
        this.context.setContent( createContent() );

        final ImageUrlParams params = new ImageUrlParams().
            quality( 90 ).
            background( "00ff00" ).
            filter( "scale(10,10)" ).
            format( "jpg" ).
            context( this.context );

        final String url = this.service.imageUrl( params );
        assertEquals( "/portal/stage/a/b/mycontent/_/image/123456/mycontent.jpg?filter=scale%2810%2C10%29&background=00ff00&quality=90",
                      url );
    }

    @Test
    public void createUrl_withId()
    {
        createContent();

        final ImageUrlParams params = new ImageUrlParams().
            id( "123456" ).
            context( this.context );

        final String url = this.service.imageUrl( params );
        assertEquals( "/portal/stage/context/path/_/image/123456/mycontent", url );
    }

    @Test
    public void createUrl_withPath()
    {
        createContent();

        final ImageUrlParams params = new ImageUrlParams().
            path( "/a/b/mycontent" ).
            context( this.context );

        final String url = this.service.imageUrl( params );
        assertEquals( "/portal/stage/context/path/_/image/123456/mycontent", url );
    }

    private Content createContent()
    {
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );
        return content;
    }
}
