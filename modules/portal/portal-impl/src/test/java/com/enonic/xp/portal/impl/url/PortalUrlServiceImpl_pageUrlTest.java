package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.url.PageUrlParams;
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

public class PortalUrlServiceImpl_pageUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl_toMe()
    {
        final PageUrlParams params = new PageUrlParams().portalRequest( this.portalRequest ).param( "a", 3 );

        final String url = this.service.pageUrl( params );
        assertEquals( "/site/myproject/draft/context/path?a=3", url );
    }

    @Test
    public void createUrl_withRelativePath()
    {
        final PageUrlParams params = new PageUrlParams().portalRequest( this.portalRequest ).path( "a/b" ).param( "a", 3 );

        final String url = this.service.pageUrl( params );
        assertEquals( "/site/myproject/draft/context/path/a/b?a=3", url );
    }

    @Test
    public void createUrl_withAbsolutePath()
    {
        final PageUrlParams params = new PageUrlParams().portalRequest( this.portalRequest ).path( "/a/b" );

        final String url = this.service.pageUrl( params );
        assertEquals( "/site/myproject/draft/a/b", url );
    }

    @Test
    public void createUrl_withId()
    {
        final Content content = ContentFixtures.newContent();
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        final PageUrlParams params = new PageUrlParams().portalRequest( this.portalRequest ).id( "123456" );

        final String url = this.service.pageUrl( params );
        assertEquals( "/site/myproject/draft/a/b/mycontent", url );
    }

    @Test
    public void createUrl_withIdAndPath()
    {
        final Content content = ContentFixtures.newContent();
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );

        final PageUrlParams params = new PageUrlParams().portalRequest( this.portalRequest ).id( "123456" ).path( "/a/b" );

        final String url = this.service.pageUrl( params );
        assertEquals( "/site/myproject/draft/a/b/mycontent", url );
    }

    @Test
    public void createUrl_withId_notFound()
    {
        final Content content = ContentFixtures.newContent();
        when( this.contentService.getById( content.getId() ) ).thenThrow( ContentNotFoundException.create()
                                                                              .contentId( content.getId() )
                                                                              .repositoryId(
                                                                                  RepositoryId.from( "com.enonic.cms.myproject" ) )
                                                                              .branch( ContentConstants.BRANCH_DRAFT )
                                                                              .build() );

        final PageUrlParams params = new PageUrlParams().portalRequest( this.portalRequest ).id( "123456" );

        final String url = this.service.pageUrl( params );
        assertThat( url ).startsWith( "/_/error/404?message=Not+Found." );
    }

    @Test
    public void createUrl_absolute()
    {
        final PageUrlParams params =
            new PageUrlParams().type( UrlTypeConstants.ABSOLUTE ).portalRequest( this.portalRequest ).param( "a", 3 );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final String url = this.service.pageUrl( params );
        assertEquals( "http://localhost/site/myproject/draft/context/path?a=3", url );
    }

    @Test
    void createUrlOfflineWithoutBaseUrl()
    {
        final PageUrlParams params = new PageUrlParams().portalRequest( null )
            .path( "/mycontent" )
            .param( "a", 3 )
            .offline( true )
            .projectName( "myproject" )
            .branch( "draft" );

        when( this.contentService.getByPath( eq( ContentPath.from( "/mycontent" ) ) ) ).thenReturn( ContentFixtures.newContent() );

        final String url = this.service.pageUrl( params );
        assertEquals( "/site/myproject/draft/a/b/mycontent?a=3", url );
    }

    @Test
    void createUrlOfflineWithBaseUrl()
    {
        final PageUrlParams params = new PageUrlParams().portalRequest( null )
            .type( UrlTypeConstants.ABSOLUTE )
            .path( "/mycontent" )
            .param( "a", 3 )
            .offline( true )
            .projectName( "myproject" )
            .branch( "draft" );

        final Content content = ContentFixtures.newContent();

        final PropertyTree config = new PropertyTree();
        config.addString( "baseUrl", "https://cdn.company.com" );

        SiteConfigs siteConfigs = SiteConfigs.create()
            .add( SiteConfig.create().application( ApplicationKey.from( "com.enonic.xp.site" ) ).config( config ).build() )
            .build();

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/a" ) );
        when( site.getSiteConfigs() ).thenReturn( siteConfigs );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );
        when( contentService.getByPath( eq( ContentPath.from( "/mycontent" ) ) ) ).thenReturn( content );

        final String url = this.service.pageUrl( params );
        assertEquals( "https://cdn.company.com/b/mycontent?a=3", url );
    }

    @Test
    void createUrlOfflineWithBaseUrlOnProject()
    {
        final PageUrlParams params = new PageUrlParams().portalRequest( null )
            .type( UrlTypeConstants.ABSOLUTE )
            .path( "/mycontent" )
            .param( "a", 3 )
            .offline( true )
            .projectName( "myproject" )
            .branch( "draft" );

        final Content content = Content.create( ContentFixtures.newContent() ).parentPath( ContentPath.ROOT ).build();

        final PropertyTree config = new PropertyTree();
        config.addString( "baseUrl", "https://cdn.company.com" );

        SiteConfigs siteConfigs = SiteConfigs.create()
            .add( SiteConfig.create().application( ApplicationKey.from( "com.enonic.xp.site" ) ).config( config ).build() )
            .build();

        final Project project = mock( Project.class );
        when( project.getSiteConfigs() ).thenReturn( siteConfigs );

        when( projectService.get( eq( ProjectName.from( "myproject" ) ) ) ).thenReturn( project );
        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( null );
        when( contentService.getByPath( eq( ContentPath.from( "/mycontent" ) ) ) ).thenReturn( content );

        final String url = this.service.pageUrl( params );
        assertEquals( "https://cdn.company.com/mycontent?a=3", url );
    }
}
