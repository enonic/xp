package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.portal.url.ContextPathType;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PortalUrlServiceImpl_imageUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl()
    {
        this.portalRequest.setContent( createContent() );

        final ImageUrlParams params = new ImageUrlParams().portalRequest( this.portalRequest ).scale( "max(300)" ).validate();

        final String url = this.service.imageUrl( params );
        assertEquals( "/site/myproject/draft/a/b/mycontent/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent",
                      url );
    }

    @Test
    public void createUrl_withoutContentPath()
    {
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
        createContent();

        final ImageUrlParams params =
            new ImageUrlParams().id( "123456" ).portalRequest( this.portalRequest ).scale( "max(300)" ).validate();

        final String url = this.service.imageUrl( params );
        assertEquals( "/site/myproject/draft/context/path/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent", url );
    }

    @Test
    public void createUrl_withPath()
    {
        createContent();

        final ImageUrlParams params =
            new ImageUrlParams().path( "/a/b/mycontent" ).portalRequest( this.portalRequest ).scale( "max(300)" ).validate();

        final String url = this.service.imageUrl( params );
        assertEquals( "/site/myproject/draft/context/path/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent", url );
    }

    @Test
    public void createUrl_withId_notFound()
    {
        createContentNotFound();

        final ImageUrlParams params =
            new ImageUrlParams().id( "123456" ).portalRequest( this.portalRequest ).scale( "max(300)" ).validate();

        final String url = this.service.imageUrl( params );
        assertThat( url ).startsWith( "/site/myproject/draft/context/path/_/error/404?message=Not+Found." );
    }

    @Test
    public void createUrl_withNonMediaContent()
    {
        this.portalRequest.setContent( createContent( "non-media", false ) );

        final ImageUrlParams params =
            new ImageUrlParams().format( "png" ).portalRequest( this.portalRequest ).scale( "max(300)" ).validate();

        assertThat( this.service.imageUrl( params ) ).startsWith( "/site/myproject/draft/a/b/mycontent/_/error/404?message=Not+Found." );
    }

    @Test
    public void createUrl_absolute()
    {
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
        this.portalRequest.setContent( createContent( "name with spaces(and-others).png", true ) );

        final ImageUrlParams params =
            new ImageUrlParams().format( "png" ).portalRequest( this.portalRequest ).scale( "max(300)" ).validate();

        final String url = this.service.imageUrl( params );
        assertEquals(
            "/site/myproject/draft/a/b/name%20with%20spaces(and-others).png/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/name%20with%20spaces(and-others).png",
            url );
    }

    @Test
    public void createImageUrlForSlashApiWithContentPathInContext()
    {
        final PropertyTree siteConfig = new PropertyTree();
        siteConfig.setString( "baseUrl", "http://media.enonic.com" );

        final SiteConfigs siteConfigs = mock( SiteConfigs.class );
        when( siteConfigs.get( eq( ApplicationKey.from( "com.enonic.xp.site" ) ) ) ).thenReturn(
            SiteConfig.create().application( ApplicationKey.from( "com.enonic.xp.site" ) ).config( siteConfig ).build() );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/a/b" ) );
        when( site.getSiteConfigs() ).thenReturn( siteConfigs );

        final Content content = createContent();
        when( contentService.getByPath( eq( content.getPath() ) ) ).thenReturn( content );
        when( contentService.findNearestSiteByPath( eq( content.getPath() ) ) ).thenReturn( site );

        this.portalRequest.setBaseUri( "" );
        this.portalRequest.setRawPath( "/api/com.enonic.app.appname" );

        ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( ContentConstants.BRANCH_DRAFT )
            .attribute( "contentKey", content.getPath().toString() )
            .build()
            .runWith( () -> {
                ImageUrlParams params = new ImageUrlParams().format( "png" )
                    .path( content.getPath().toString() )
                    .type( UrlTypeConstants.ABSOLUTE )
                    .portalRequest( this.portalRequest )
                    .scale( "max(300)" );

                String url = this.service.imageUrl( params );
                assertEquals(
                    "http://media.enonic.com/api/media/image/myproject:draft/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent.png",
                    url );

                params = new ImageUrlParams().format( "png" )
                    .path( content.getPath().toString() )
                    .type( UrlTypeConstants.SERVER_RELATIVE )
                    .portalRequest( this.portalRequest )
                    .scale( "max(300)" );

                url = this.service.imageUrl( params );
                assertEquals( "/api/media/image/myproject:draft/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent.png",
                              url );
            } );
    }

    @Test
    void testCreateUrlWithContentPathInContext()
    {
        // Case when contentPath is set in the context and nearest site is not found.
        // Trying to resolve the base URL from the siteConfig of a project.

        final PropertyTree siteConfig = new PropertyTree();
        siteConfig.setString( "baseUrl", "http://media.enonic.com" );

        final SiteConfigs siteConfigs = mock( SiteConfigs.class );
        when( siteConfigs.get( eq( ApplicationKey.from( "com.enonic.xp.site" ) ) ) ).thenReturn(
            SiteConfig.create().application( ApplicationKey.from( "com.enonic.xp.site" ) ).config( siteConfig ).build() );

        final Project project = mock( Project.class );
        when( project.getSiteConfigs() ).thenReturn( siteConfigs );

        final Content content = createContent();
        when( contentService.getById( eq( content.getId() ) ) ).thenReturn( content );
        when( contentService.findNearestSiteByPath( eq( ContentPath.from( "/path/to/content" ) ) ) ).thenReturn( null );
        when( projectService.get( eq( ProjectName.from( "myproject" ) ) ) ).thenReturn( project );

        this.portalRequest.setBaseUri( "" );
        this.portalRequest.setRawPath( "/api/com.enonic.app.appname" );

        ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( ContentConstants.BRANCH_DRAFT )
            .attribute( "contentKey", "/path/to/content" )
            .build()
            .runWith( () -> {
                ImageUrlParams params = new ImageUrlParams().format( "png" )
                    .id( content.getId().toString() )
                    .type( UrlTypeConstants.ABSOLUTE )
                    .portalRequest( this.portalRequest )
                    .scale( "max(300)" );

                String url = this.service.imageUrl( params );
                assertEquals(
                    "http://media.enonic.com/api/media/image/myproject:draft/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent.png",
                    url );
            } );
    }

    @Test
    public void createUrlForSlashApiWithoutBaseUrl()
    {
        this.portalRequest.setBaseUri( "" );
        this.portalRequest.setRawPath( "/api/com.enonic.app.appname" );
        this.portalRequest.setContent( createContent() );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 8080 );

        ImageUrlParams params = new ImageUrlParams().id( "123456" )
            .format( "png" )
            .type( UrlTypeConstants.ABSOLUTE )
            .portalRequest( this.portalRequest )
            .scale( "max(300)" );

        ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( ContentConstants.BRANCH_DRAFT )
            .build()
            .runWith( () -> {
                String url = this.service.imageUrl( params );
                assertEquals(
                    "http://localhost:8080/api/media/image/myproject:draft/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent.png",
                    url );
            } );
    }

    @Test
    public void createImageUrlForMasterBranch()
    {
        this.portalRequest.setBaseUri( "" );
        this.portalRequest.setRawPath( "/api/com.enonic.app.appname" );
        this.portalRequest.setContent( createContent() );
        this.portalRequest.setBranch( ContentConstants.BRANCH_MASTER );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        ImageUrlParams params = new ImageUrlParams().id( "123456" )
            .format( "png" )
            .type( UrlTypeConstants.ABSOLUTE )
            .portalRequest( this.portalRequest )
            .scale( "max(300)" );

        String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( ContentConstants.BRANCH_MASTER )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertEquals( "http://localhost/api/media/image/myproject/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent.png",
                      url );
    }

    @Test
    public void createImageUrlWhenLegacyModeDisabledWithoutSite()
    {
        this.portalRequest.setBaseUri( "/site" );
        this.portalRequest.setRawPath( "/site/myproject/draft/a/b/mycontent" );
        this.portalRequest.setContent( createContent() );

        final ImageUrlParams params =
            new ImageUrlParams().format( "png" ).type( UrlTypeConstants.ABSOLUTE ).portalRequest( this.portalRequest ).scale( "max(300)" );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 8080 );

        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.legacy_imageService_enabled() ).thenReturn( false );
        this.service.activate( portalConfig );

        // fallback to project, because a site is not provided
        final String url = this.service.imageUrl( params );
        assertEquals(
            "http://localhost:8080/site/myproject/draft/_/media/image/myproject:draft/123456:ec25d6e4126c7064f82aaab8b34693fc/max-300/mycontent.png",
            url );
    }

    @Test
    public void createImageUrlWhenLegacyModeDisabledWithSite()
    {
        this.portalRequest.setBaseUri( "/site" );
        this.portalRequest.setRawPath( "/site/myproject/draft/a/b/mycontent" );
        this.portalRequest.setContent( createContent() );

        final ImageUrlParams params =
            new ImageUrlParams().format( "png" ).type( UrlTypeConstants.ABSOLUTE ).portalRequest( this.portalRequest ).scale( "max(300)" );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 8080 );

        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.legacy_imageService_enabled() ).thenReturn( false );
        this.service.activate( portalConfig );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/a/b" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        when( contentService.getByPath( ContentPath.from( "/a/b" ) ) ).thenReturn( site );
        when( contentService.findNearestSiteByPath( ContentPath.from( "/a/b/mycontent" ) ) ).thenReturn( site );

        final String url = this.service.imageUrl( params );
        assertEquals(
            "http://localhost:8080/site/myproject/draft/a/b/_/media/image/myproject:draft/123456:ec25d6e4126c7064f82aaab8b34693fc/max-300/mycontent.png",
            url );
    }

    @Test
    public void createImageUrlWhenLegacyModeEnabled()
    {
        this.portalRequest.setBaseUri( "/site" );
        this.portalRequest.setRawPath( "/site/myproject/draft/a/b/mycontent" );
        this.portalRequest.setContent( createContent() );

        final ImageUrlParams params =
            new ImageUrlParams().format( "png" ).type( UrlTypeConstants.ABSOLUTE ).portalRequest( this.portalRequest ).scale( "max(300)" );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 8080 );

        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.legacy_imageService_enabled() ).thenReturn( true );
        this.service.activate( portalConfig );

        final String url = this.service.imageUrl( params );
        assertEquals(
            "http://localhost:8080/site/myproject/draft/a/b/mycontent/_/image/123456:8cf45815bba82c9711c673c9bb7304039a790026/max-300/mycontent.png",
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

