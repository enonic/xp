package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.content.ContentNodeHelper;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.url.BaseUrlParams;
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
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.web.vhost.VirtualHost;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PortalUrlService_baseUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    void testNoRequestNoProjectInContext()
    {
        PortalRequestAccessor.set( null );

        final BaseUrlParams params = BaseUrlParams.create().setId( "contentid" ).build();

        // a base URL is a prefix of other URLs: a failure is reported, never encoded into it
        assertThatThrownBy( () -> ContextBuilder.create().build().callWith( () -> this.service.baseUrl( params ) ) ).isInstanceOf(
            IllegalArgumentException.class ).hasMessageContaining( "RepositoryId must be set" );
    }

    @Test
    void testNoRequestWithNonContentProject()
    {
        PortalRequestAccessor.set( null );

        final BaseUrlParams params = BaseUrlParams.create().setId( "contentid" ).build();

        assertThatThrownBy( () -> ContextBuilder.create()
            .repositoryId( RepositoryId.from( "non.content.project" ) )
            .branch( Branch.from( "branch" ) )
            .build()
            .callWith( () -> this.service.baseUrl( params ) ) ).isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( "not a content repository" );
    }

    @Test
    void testNoRequestWithContextProjectAndWithoutContextBranch()
    {
        PortalRequestAccessor.set( null );

        final BaseUrlParams params = BaseUrlParams.create().setId( "contentid" ).build();

        assertThatThrownBy( () -> ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .build()
            .callWith( () -> this.service.baseUrl( params ) ) ).isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( "Branch not set" );
    }

    @Test
    void testNoRequestWithUnknownContent()
    {
        PortalRequestAccessor.set( null );

        final ContentId contentId = ContentId.from( "unknown" );
        when( contentService.getById( eq( contentId ) ) ).thenThrow( ContentNotFoundException.create()
                                                                         .contentId( contentId )
                                                                         .repositoryId( ContextAccessor.current().getRepositoryId() )
                                                                         .branch( ContextAccessor.current().getBranch() )
                                                                         .contentRoot( ContentNodeHelper.getContentRoot() )
                                                                         .build() );

        final BaseUrlParams params = BaseUrlParams.create().setId( contentId.toString() ).build();

        // the anchor does not exist: the caller is told, instead of receiving an error URL
        // that would silently prefix every URL built from it
        assertThatThrownBy( () -> ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( Branch.from( "draft" ) )
            .build()
            .callWith( () -> this.service.baseUrl( params ) ) ).isInstanceOf( ContentNotFoundException.class );
    }

    private static void mockDataWithSiteConfig( final SiteConfigs siteConfigs, final Site site )
    {
        final PropertyTree data = new PropertyTree();
        when( site.getData() ).thenReturn( data );

        SiteConfigsDataSerializer.toData( siteConfigs, site.getData().getRoot() );
    }

    @Test
    void testNoRequestWithAbsoluteUrlTypeWithBaseUrl()
    {
        PortalRequestAccessor.set( null );

        final BaseUrlParams params = BaseUrlParams.create().setUrlType( UrlTypeConstants.ABSOLUTE ).setPath( "/mycontent" ).build();

        final Content content = ContentFixtures.newContent();

        final PropertyTree config = new PropertyTree();
        config.addString( "baseUrl", "https://cdn.company.com" );

        final SiteConfigs siteConfigs =
            SiteConfigs.create().add( SiteConfig.create().application( ApplicationKey.from( "portal" ) ).config( config ).build() ).build();

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/a" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        mockDataWithSiteConfig( siteConfigs, site );

        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );
        when( contentService.getByPath( eq( ContentPath.from( "/mycontent" ) ) ) ).thenReturn( content );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( Branch.from( "draft" ) )
            .build()
            .callWith( () -> this.service.baseUrl( params ) );

        assertEquals( "https://cdn.company.com", url );
    }

    @Test
    void testNoRequestWithServerRelativeUrlTypeWithBaseUrl()
    {
        PortalRequestAccessor.set( null );

        final Content content = ContentFixtures.newContent();

        final PropertyTree config = new PropertyTree();
        config.addString( "baseUrl", "https://cdn.company.com" );

        final SiteConfigs siteConfigs =
            SiteConfigs.create().add( SiteConfig.create().application( ApplicationKey.from( "portal" ) ).config( config ).build() ).build();

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/a" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        mockDataWithSiteConfig( siteConfigs, site );

        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );
        when( contentService.getByPath( eq( ContentPath.from( "/mycontent" ) ) ) ).thenReturn( content );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( Branch.from( "draft" ) )
            .build()
            .callWith( () -> this.service.baseUrl(
                BaseUrlParams.create().setUrlType( UrlTypeConstants.SERVER_RELATIVE ).setPath( "/mycontent" ).build() ) );

        // the configured Base URL is used verbatim: urlType only applies to request-anchored URLs
        assertEquals( "https://cdn.company.com", url );
    }

    @Test
    void testNoRequestWithAbsoluteUrlTypeWithoutBaseUrl()
    {
        PortalRequestAccessor.set( null );

        final Content content = ContentFixtures.newContent();

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/a" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        mockDataWithSiteConfig( SiteConfigs.empty(), site );

        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );
        when( contentService.getByPath( eq( ContentPath.from( "/mycontent" ) ) ) ).thenReturn( content );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( Branch.from( "master" ) )
            .build()
            .callWith( () -> this.service.baseUrl(
                BaseUrlParams.create().setUrlType( UrlTypeConstants.ABSOLUTE ).setPath( "/mycontent" ).build() ) );

        // no Base URL configured: the base is where the site engine serves the site
        assertEquals( "/site/myproject/master/a", url );
    }

    @Test
    void testNoRequestWithExplicitProjectWithoutExplicitBranchWithoutBaseUrl()
    {
        PortalRequestAccessor.set( null );

        final Content content = ContentFixtures.newContent();

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/a" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        mockDataWithSiteConfig( SiteConfigs.empty(), site );

        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );
        when( contentService.getByPath( eq( ContentPath.from( "/mycontent" ) ) ) ).thenReturn( content );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> {
                final BaseUrlParams params = BaseUrlParams.create()
                    .setUrlType( UrlTypeConstants.ABSOLUTE )
                    .setPath( "/mycontent" )
                    .setProjectName( "explicit-project" )
                    .build();

                return this.service.baseUrl( params );
            } );

        assertEquals( "/site/explicit-project/context-branch/a", url );
    }

    @Test
    void testNoRequestWithExplicitProjectWithExplicitBranchWithoutBaseUrl()
    {
        PortalRequestAccessor.set( null );

        final Content content = ContentFixtures.newContent();

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/a" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        mockDataWithSiteConfig( SiteConfigs.empty(), site );

        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );
        when( contentService.getByPath( eq( ContentPath.from( "/mycontent" ) ) ) ).thenReturn( content );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> {
                final BaseUrlParams params = BaseUrlParams.create()
                    .setUrlType( UrlTypeConstants.ABSOLUTE )
                    .setPath( "/mycontent" )
                    .setProjectName( "explicit-project" )
                    .setBranch( "explicit-branch" )
                    .build();

                return this.service.baseUrl( params );
            } );

        assertEquals( "/site/explicit-project/explicit-branch/a", url );
    }

    @Test
    void testWithSiteRequest()
    {
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setRawPath( "/site/request-project/request-branch/a/b/mycontent" );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final Content content = ContentFixtures.newContent();
        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );

        final BaseUrlParams params = BaseUrlParams.create().setUrlType( UrlTypeConstants.ABSOLUTE ).setId( "123456" ).build();

        final String url = this.service.baseUrl( params );
        assertEquals( "http://localhost/site/request-project/request-branch", url );
    }

    @Test
    void testWithAdminSiteRequest()
    {
        portalRequest.setBaseUri( "/admin/site/preview" );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setRawPath( "/admin/site/preview/request-project/request-branch/a/b/mycontent" );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final Content content = ContentFixtures.newContent();
        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );

        final BaseUrlParams params = BaseUrlParams.create().setUrlType( UrlTypeConstants.ABSOLUTE ).setId( "123456" ).build();

        final String url = this.service.baseUrl( params );
        assertEquals( "http://localhost/admin/site/preview/request-project/request-branch", url );
    }

    @Test
    void testWithSiteRequestInContext()
    {
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setRawPath( "/site/request-project/request-branch/a/b/mycontent" );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final Content content = ContentFixtures.newContent();
        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith(
                () -> this.service.baseUrl( BaseUrlParams.create().setUrlType( UrlTypeConstants.ABSOLUTE ).setId( "123456" ).build() ) );

        assertEquals( "http://localhost/site/request-project/request-branch", url );
    }

    @Test
    void testWithSiteRequestInContextWithVirtualHost()
    {
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setRawPath( "/site/request-project/request-branch/a/b/mycontent" );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/source" );
        when( virtualHost.getTarget() ).thenReturn( "/site/request-project/request-branch" );
        when( portalRequest.getRawRequest().getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final Content content = ContentFixtures.newContent();
        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith(
                () -> this.service.baseUrl( BaseUrlParams.create().setUrlType( UrlTypeConstants.ABSOLUTE ).setId( "123456" ).build() ) );

        assertEquals( "http://localhost/source", url );
    }

    @Test
    void testWithNonSiteRequestInContextWithoutBaseUrl()
    {
        portalRequest.setBaseUri( "/api" );
        portalRequest.setMode( null );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/api/application:apikey" );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final Content content = ContentFixtures.newContent();
        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/source" );
        when( virtualHost.getTarget() ).thenReturn( "/site/context-project/context-branch/a" );
        when( portalRequest.getRawRequest().getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith(
                () -> this.service.baseUrl( BaseUrlParams.create().setUrlType( UrlTypeConstants.ABSOLUTE ).setId( "123456" ).build() ) );

        // Without virtual host rewrite to avoid OutOfScopeException
        assertEquals( "/site/context-project/context-branch", url );
    }

    @Test
    void testNoRequestWithExplicitProjectWithExplicitBranchWithBaseUrl()
    {
        PortalRequestAccessor.set( null );

        final Content content = ContentFixtures.newContent();

        final PropertyTree config = new PropertyTree();
        config.addString( "baseUrl", "https://cdn.company.com" );

        final SiteConfigs siteConfigs =
            SiteConfigs.create().add( SiteConfig.create().application( ApplicationKey.from( "portal" ) ).config( config ).build() ).build();

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/a" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        mockDataWithSiteConfig( siteConfigs, site );

        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );
        when( contentService.getByPath( eq( ContentPath.from( "/mycontent" ) ) ) ).thenReturn( content );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> {
                final BaseUrlParams params = BaseUrlParams.create()
                    .setUrlType( UrlTypeConstants.ABSOLUTE )
                    .setPath( "/mycontent" )
                    .setProjectName( "explicit-project" )
                    .setBranch( "explicit-branch" )
                    .build();

                return this.service.baseUrl( params );
            } );

        assertEquals( "https://cdn.company.com", url );
    }

    @Test
    void testWithNonSiteRequestInContextWithBaseUrl()
    {
        portalRequest.setBaseUri( "/api" );
        portalRequest.setMode( null );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/api/application:apikey" );

        final Content content = ContentFixtures.newContent();
        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );

        final PropertyTree config = new PropertyTree();
        config.addString( "baseUrl", "https://cdn.company.com" );

        final SiteConfigs siteConfigs =
            SiteConfigs.create().add( SiteConfig.create().application( ApplicationKey.from( "portal" ) ).config( config ).build() ).build();

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/a" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        mockDataWithSiteConfig( siteConfigs, site );

        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith(
                () -> this.service.baseUrl( BaseUrlParams.create().setUrlType( UrlTypeConstants.ABSOLUTE ).setId( "123456" ).build() ) );

        assertEquals( "https://cdn.company.com", url );
    }

    @Test
    void testWithNonSiteRequestWithExplicitContextWithBaseUrl()
    {
        portalRequest.setBaseUri( "/api" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/api/application:apikey" );

        final Content content = ContentFixtures.newContent();
        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );

        final PropertyTree config = new PropertyTree();
        config.addString( "baseUrl", "https://cdn.company.com" );

        final SiteConfigs siteConfigs =
            SiteConfigs.create().add( SiteConfig.create().application( ApplicationKey.from( "portal" ) ).config( config ).build() ).build();

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/a" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        mockDataWithSiteConfig( siteConfigs, site );

        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.baseUrl( BaseUrlParams.create()
                                                       .setUrlType( UrlTypeConstants.ABSOLUTE )
                                                       .setId( "123456" )
                                                       .setProjectName( "explicit-project" )
                                                       .setBranch( "explicit-branch" )
                                                       .build() ) );

        assertEquals( "https://cdn.company.com", url );
    }

    @Test
    void testWithNonSiteRequestWithExplicitContextWithoutBaseUrl()
    {
        portalRequest.setBaseUri( "/api" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/api/application:apikey" );

        final Content content = ContentFixtures.newContent();
        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/a" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        mockDataWithSiteConfig( SiteConfigs.empty(), site );

        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.baseUrl( BaseUrlParams.create()
                                                       .setUrlType( UrlTypeConstants.ABSOLUTE )
                                                       .setId( "123456" )
                                                       .setProjectName( "explicit-project" )
                                                       .setBranch( "explicit-branch" )
                                                       .build() ) );

        assertEquals( "/site/explicit-project/explicit-branch/a", url );
    }

    private void mockProjectWithBaseUrl( final String baseUrl )
    {
        final PropertyTree config = new PropertyTree();
        config.addString( "baseUrl", baseUrl );

        final Project project = Project.create()
            .name( ProjectName.from( "myproject" ) )
            .addSiteConfig( SiteConfig.create().application( ApplicationKey.from( "portal" ) ).config( config ).build() )
            .build();

        when( projectService.get( eq( ProjectName.from( "myproject" ) ) ) ).thenReturn( project );
    }

    private String projectRootBaseUrl()
    {
        return ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( Branch.from( "draft" ) )
            .build()
            .callWith( () -> this.service.baseUrl(
                BaseUrlParams.create().setPath( "/" ).setProjectName( "myproject" ).setBranch( "draft" ).build() ) );
    }

    @Test
    void testProjectRootAnchorWithConfiguredBaseUrl()
    {
        PortalRequestAccessor.set( null );

        mockProjectWithBaseUrl( "https://cdn.company.com" );

        // the root of a project holds no content: the URL is resolved from the project configuration
        assertEquals( "https://cdn.company.com", projectRootBaseUrl() );
    }

    @Test
    void testProjectRootAnchorWithoutConfiguredBaseUrl()
    {
        PortalRequestAccessor.set( null );

        assertEquals( "/site/myproject/draft", projectRootBaseUrl() );
    }

    @Test
    void testAnchorWithoutContentKey()
    {
        PortalRequestAccessor.set( null );

        mockProjectWithBaseUrl( "https://cdn.company.com" );

        // no content key at all anchors at the project, like the root path does
        assertEquals( "https://cdn.company.com", ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( Branch.from( "draft" ) )
            .build()
            .callWith( () -> this.service.baseUrl(
                BaseUrlParams.create().setProjectName( "myproject" ).setBranch( "draft" ).build() ) ) );
    }
}
