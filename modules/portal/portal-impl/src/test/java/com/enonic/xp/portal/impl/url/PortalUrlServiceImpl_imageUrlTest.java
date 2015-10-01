package com.enonic.xp.portal.impl.url;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.Media;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

public class PortalUrlServiceImpl_imageUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl()
    {
        this.portalRequest.setContent( createContent() );

        final ImageUrlParams params = new ImageUrlParams().
            portalRequest( this.portalRequest ).
            scale( "max(300)" );

        final String url = this.service.imageUrl( params );
        assertEquals( "/portal/draft/a/b/mycontent/_/image/123456:binaryHash/max-300/mycontent", url );
    }

    @Test
    public void createUrl_withFormat()
    {
        this.portalRequest.setContent( createContent() );

        final ImageUrlParams params = new ImageUrlParams().
            format( "png" ).
            portalRequest( this.portalRequest ).
            scale( "max(300)" );

        final String url = this.service.imageUrl( params );
        assertEquals( "/portal/draft/a/b/mycontent/_/image/123456:binaryHash/max-300/mycontent.png", url );
    }

    @Test
    public void createUrl_allOptions()
    {
        this.portalRequest.setContent( createContent() );

        final ImageUrlParams params = new ImageUrlParams().
            quality( 90 ).
            background( "00ff00" ).
            filter( "scale(10,10)" ).
            format( "jpg" ).
            portalRequest( this.portalRequest ).
            scale( "max(300)" );

        final String url = this.service.imageUrl( params );
        assertEquals(
            "/portal/draft/a/b/mycontent/_/image/123456:binaryHash/max-300/mycontent.jpg?filter=scale%2810%2C10%29&background=00ff00&quality=90",
            url );
    }

    @Test
    public void createUrl_withId()
    {
        createContent();

        final ImageUrlParams params = new ImageUrlParams().
            id( "123456" ).
            portalRequest( this.portalRequest ).
            scale( "max(300)" );

        final String url = this.service.imageUrl( params );
        assertEquals( "/portal/draft/context/path/_/image/123456:binaryHash/max-300/mycontent", url );
    }

    @Test
    public void createUrl_withPath()
    {
        createContent();

        final ImageUrlParams params = new ImageUrlParams().
            path( "/a/b/mycontent" ).
            portalRequest( this.portalRequest ).
            scale( "max(300)" );

        final String url = this.service.imageUrl( params );
        assertEquals( "/portal/draft/context/path/_/image/123456:binaryHash/max-300/mycontent", url );
    }

    @Test
    public void createUrl_withId_notFound()
    {
        createContentNotFound();

        final ImageUrlParams params = new ImageUrlParams().
            id( "123456" ).
            portalRequest( this.portalRequest ).
            scale( "max(300)" );

        final String url = this.service.imageUrl( params );
        assertEquals( "/portal/draft/context/path/_/error/404?message=Content+with+id+%5B123456%5D+was+not+found+in+branch+%5Bdraft%5D",
                      url );
    }

    @Test
    public void createUrl_absolute()
    {
        this.portalRequest.setContent( createContent() );

        final ImageUrlParams params = new ImageUrlParams().
            type( UrlTypeConstants.ABSOLUTE ).
            portalRequest( this.portalRequest ).
            scale( "max(300)" );

        MockHttpServletRequest req = new MockHttpServletRequest();
        ServletRequestHolder.setRequest( req );

        final String url = this.service.imageUrl( params );
        assertEquals( "http://localhost/portal/draft/a/b/mycontent/_/image/123456:binaryHash/max-300/mycontent", url );
    }

    private Content createContent()
    {
        final Media media = ContentFixtures.newMedia();
        Mockito.when( this.contentService.getByPath( media.getPath() ) ).thenReturn( media );
        Mockito.when( this.contentService.getById( media.getId() ) ).thenReturn( media );
        Mockito.when( this.contentService.getBinaryKey( media.getId(), media.getMediaAttachment().getBinaryReference() ) ).thenReturn(
            "binaryHash" );
        return media;
    }

    private Content createContentNotFound()
    {
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenThrow(
            new ContentNotFoundException( content.getPath(), Branch.from( "draft" ) ) );
        Mockito.when( this.contentService.getById( content.getId() ) ).thenThrow(
            new ContentNotFoundException( content.getId(), Branch.from( "draft" ) ) );
        return content;
    }
}

