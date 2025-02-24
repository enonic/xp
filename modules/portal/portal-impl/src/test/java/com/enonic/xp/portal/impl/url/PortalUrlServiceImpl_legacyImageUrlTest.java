package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.Media;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.portal.url.ContextPathType;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.repository.RepositoryId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PortalUrlServiceImpl_legacyImageUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl()
    {
        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.legacy_imageService_enabled() ).thenReturn( true );
        this.service.activate( portalConfig );

        this.portalRequest.setContent( createContent() );

        final ImageUrlParams params = new ImageUrlParams().portalRequest( this.portalRequest ).scale( "max(300)" ).validate();

        final String url = this.service.imageUrl( params );
        assertEquals( "/site/myproject/draft/a/b/mycontent/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent",
                      url );
    }

    @Test
    public void createUrl_withoutContentPath()
    {
        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.legacy_imageService_enabled() ).thenReturn( true );
        this.service.activate( portalConfig );

        this.portalRequest.setContent( createContent() );

        final ImageUrlParams params = new ImageUrlParams().portalRequest( this.portalRequest )
            .contextPathType( ContextPathType.VHOST.getValue() )
            .scale( "max(300)" )
            .validate();

        final String url = this.service.imageUrl( params );
        assertEquals( "/site/myproject/draft/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent", url );
    }

    @Test
    public void createUrl_withFormat()
    {
        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.legacy_imageService_enabled() ).thenReturn( true );
        this.service.activate( portalConfig );

        this.portalRequest.setContent( createContent() );

        final ImageUrlParams params =
            new ImageUrlParams().format( "png" ).portalRequest( this.portalRequest ).scale( "max(300)" ).validate();

        final String url = this.service.imageUrl( params );
        assertEquals( "/site/myproject/draft/a/b/mycontent/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent.png",
                      url );
    }

    @Test
    public void createUrl_allOptions()
    {
        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.legacy_imageService_enabled() ).thenReturn( true );
        this.service.activate( portalConfig );

        this.portalRequest.setContent( createContent() );

        final ImageUrlParams params = new ImageUrlParams().quality( 90 )
            .background( "00ff00" )
            .filter( "scale(10,10)" )
            .format( "jpg" )
            .portalRequest( this.portalRequest )
            .scale( "max(300)" )
            .validate();

        final String url = this.service.imageUrl( params );
        assertEquals( "/site/myproject/draft/a/b/mycontent/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent.jpg?" +
                          "quality=90&background=00ff00&filter=scale%2810%2C10%29", url );
    }

    @Test
    public void createUrl_withId()
    {
        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.legacy_imageService_enabled() ).thenReturn( true );
        this.service.activate( portalConfig );

        createContent();

        final ImageUrlParams params =
            new ImageUrlParams().id( "123456" ).portalRequest( this.portalRequest ).scale( "max(300)" ).validate();

        final String url = this.service.imageUrl( params );
        assertEquals( "/site/myproject/draft/context/path/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent", url );
    }

    @Test
    public void createUrl_withPath()
    {
        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.legacy_imageService_enabled() ).thenReturn( true );
        this.service.activate( portalConfig );

        createContent();

        final ImageUrlParams params =
            new ImageUrlParams().path( "/a/b/mycontent" ).portalRequest( this.portalRequest ).scale( "max(300)" ).validate();

        final String url = this.service.imageUrl( params );
        assertEquals( "/site/myproject/draft/context/path/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent", url );
    }

    @Test
    public void createUrl_withId_notFound()
    {
        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.legacy_imageService_enabled() ).thenReturn( true );
        this.service.activate( portalConfig );

        createContentNotFound();

        final ImageUrlParams params =
            new ImageUrlParams().id( "123456" ).portalRequest( this.portalRequest ).scale( "max(300)" ).validate();

        final String url = this.service.imageUrl( params );
        assertThat( url ).startsWith( "/site/myproject/draft/context/path/_/error/404?message=Not+Found." );
    }

    @Test
    public void createUrl_withNonMediaContent()
    {
        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.legacy_imageService_enabled() ).thenReturn( true );
        this.service.activate( portalConfig );

        this.portalRequest.setContent( createContent( "non-media", false ) );

        final ImageUrlParams params =
            new ImageUrlParams().format( "png" ).portalRequest( this.portalRequest ).scale( "max(300)" ).validate();

        assertThat( this.service.imageUrl( params ) ).startsWith( "/site/myproject/draft/a/b/mycontent/_/error/404?message=Not+Found." );
    }

    @Test
    public void createUrl_absolute()
    {
        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.legacy_imageService_enabled() ).thenReturn( true );
        this.service.activate( portalConfig );

        this.portalRequest.setContent( createContent() );

        final ImageUrlParams params =
            new ImageUrlParams().type( UrlTypeConstants.ABSOLUTE ).portalRequest( this.portalRequest ).scale( "max(300)" ).validate();

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final String url = this.service.imageUrl( params );
        assertEquals(
            "http://localhost/site/myproject/draft/a/b/mycontent/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent",
            url );
    }

    @Test
    public void createUrl_withSpacesInName()
    {
        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.legacy_imageService_enabled() ).thenReturn( true );
        this.service.activate( portalConfig );

        this.portalRequest.setContent( createContent( "name with spaces(and-others).png", true ) );

        final ImageUrlParams params =
            new ImageUrlParams().format( "png" ).portalRequest( this.portalRequest ).scale( "max(300)" ).validate();

        final String url = this.service.imageUrl( params );
        assertEquals(
            "/site/myproject/draft/a/b/name%20with%20spaces(and-others).png/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/name%20with%20spaces(and-others).png",
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
            .thenThrow( ContentNotFoundException.create()
                            .contentPath( content.getPath() )
                            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
                            .branch( ContentConstants.BRANCH_DRAFT )
                            .build() );
        Mockito.when( this.contentService.getById( content.getId() ) )
            .thenThrow( ContentNotFoundException.create()
                            .contentId( content.getId() )
                            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
                            .branch( ContentConstants.BRANCH_DRAFT )
                            .build() );
        return content;
    }
}

