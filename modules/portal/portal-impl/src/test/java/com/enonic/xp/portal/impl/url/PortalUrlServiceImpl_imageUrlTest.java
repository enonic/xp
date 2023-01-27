package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.Media;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.url.ContextPathType;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class PortalUrlServiceImpl_imageUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl()
    {
        this.portalRequest.setContent( createContent() );

        final ImageUrlParams params = new ImageUrlParams().
            portalRequest( this.portalRequest ).
            scale( "max(300)" ).
            validate();

        final String url = this.service.imageUrl( params );
        assertEquals( "/site/default/draft/a/b/mycontent/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent", url );
    }

    @Test
    public void createUrl_withoutContentPath()
    {
        this.portalRequest.setContent( createContent() );

        final ImageUrlParams params = new ImageUrlParams().
            portalRequest( this.portalRequest ).
            contextPathType( ContextPathType.VHOST.getValue() ).
            scale( "max(300)" ).
            validate();

        final String url = this.service.imageUrl( params );
        assertEquals( "/site/default/draft/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent", url );
    }

    @Test
    public void createUrl_withFormat()
    {
        this.portalRequest.setContent( createContent() );

        final ImageUrlParams params = new ImageUrlParams().
            format( "png" ).
            portalRequest( this.portalRequest ).
            scale( "max(300)" ).
            validate();

        final String url = this.service.imageUrl( params );
        assertEquals( "/site/default/draft/a/b/mycontent/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent.png",
                      url );
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
            scale( "max(300)" ).
            validate();

        final String url = this.service.imageUrl( params );
        assertEquals( "/site/default/draft/a/b/mycontent/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent.jpg?" +
                          "quality=90&background=00ff00&filter=scale%2810%2C10%29", url );
    }

    @Test
    public void createUrl_withId()
    {
        createContent();

        final ImageUrlParams params = new ImageUrlParams().
            id( "123456" ).
            portalRequest( this.portalRequest ).
            scale( "max(300)" ).
            validate();

        final String url = this.service.imageUrl( params );
        assertEquals( "/site/default/draft/context/path/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent", url );
    }

    @Test
    public void createUrl_withPath()
    {
        createContent();

        final ImageUrlParams params = new ImageUrlParams().
            path( "/a/b/mycontent" ).
            portalRequest( this.portalRequest ).
            scale( "max(300)" ).
            validate();

        final String url = this.service.imageUrl( params );
        assertEquals( "/site/default/draft/context/path/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent", url );
    }

    @Test
    public void createUrl_withId_notFound()
    {
        createContentNotFound();

        final ImageUrlParams params = new ImageUrlParams().
            id( "123456" ).
            portalRequest( this.portalRequest ).
            scale( "max(300)" ).
            validate();

        final String url = this.service.imageUrl( params );
        assertEquals( "/site/default/draft/context/path/_/error/500?message=Image+%5B123456%5D+not+found", url );
    }

    @Test
    public void createUrl_withNonMediaContent()
    {
        this.portalRequest.setContent( createContent( "non-media", false ) );

        final ImageUrlParams params = new ImageUrlParams().
            format( "png" ).
            portalRequest( this.portalRequest ).
            scale( "max(300)" ).
            validate();

        assertEquals( "/site/default/draft/a/b/mycontent/_/error/500?message=Image+with+%5B123456%5D+id+not+found",
                      this.service.imageUrl( params ) );
    }

    @Test
    public void createUrl_absolute()
    {
        this.portalRequest.setContent( createContent() );

        final ImageUrlParams params = new ImageUrlParams().
            type( UrlTypeConstants.ABSOLUTE ).
            portalRequest( this.portalRequest ).
            scale( "max(300)" ).
            validate();

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final String url = this.service.imageUrl( params );
        assertEquals(
            "http://localhost/site/default/draft/a/b/mycontent/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent",
            url );
    }

    @Test
    public void createUrl_withSpacesInName()
    {
        this.portalRequest.setContent( createContent( "name with spaces(and-others).png", true ) );

        final ImageUrlParams params = new ImageUrlParams().
            format( "png" ).
            portalRequest( this.portalRequest ).
            scale( "max(300)" ).
            validate();

        final String url = this.service.imageUrl( params );
        assertEquals(
            "/site/default/draft/a/b/name%20with%20spaces(and-others).png/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/name%20with%20spaces(and-others).png",
            url );
    }

    private Content createContent()
    {
        return createContent( null, true );
    }

    private Content createContent( final String name, final boolean isMedia )
    {
        Content content;
        if ( isMedia )
        {
            Media media = ContentFixtures.newMedia();
            if ( name != null )
            {
                media = Media.create( media ).name( name ).build();
            }
            content = media;
            Mockito.when( this.contentService.getBinaryKey( media.getId(), media.getMediaAttachment().getBinaryReference() ) )
                .thenReturn( "binaryHash" );
        }
        else
        {
            content = ContentFixtures.newContent();
        }
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );

        return content;
    }

    private Content createContentNotFound()
    {
        final Content content = ContentFixtures.newContent();
        Mockito.when( this.contentService.getByPath( content.getPath() ) )
            .thenThrow( new ContentNotFoundException( content.getPath(), Branch.from( "draft" ) ) );
        Mockito.when( this.contentService.getById( content.getId() ) )
            .thenThrow( new ContentNotFoundException( content.getId(), Branch.from( "draft" ) ) );
        return content;
    }
}

