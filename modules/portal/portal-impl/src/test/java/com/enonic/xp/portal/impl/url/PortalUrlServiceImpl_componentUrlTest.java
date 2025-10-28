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
import com.enonic.xp.portal.url.ComponentUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PortalUrlServiceImpl_componentUrlTest
    extends AbstractPortalUrlServiceImplTest
{

    @Test
    void testNoRequestNoProjectInContext()
    {
        PortalRequestAccessor.set( null );

        final String url = ContextBuilder.create().build().callWith( () -> this.service.componentUrl( new ComponentUrlParams() ) );
        assertThat( url ).startsWith( "/_/error/500?message=Something+went+wrong." );
    }

    @Test
    void testNoRequestWithNonContentProject()
    {
        PortalRequestAccessor.set( null );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "non.content.project" ) )
            .branch( Branch.from( "branch" ) )
            .build()
            .callWith( () -> this.service.componentUrl( new ComponentUrlParams() ) );
        assertThat( url ).startsWith( "/_/error/500?message=Something+went+wrong." );
    }

    @Test
    void testNoRequestWithContextProjectAndWithoutContextBranch()
    {
        PortalRequestAccessor.set( null );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .build()
            .callWith( () -> this.service.componentUrl( new ComponentUrlParams() ) );
        assertThat( url ).startsWith( "/_/error/500?message=Something+went+wrong." );
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

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( Branch.from( "draft" ) )
            .build()
            .callWith( () -> this.service.componentUrl( new ComponentUrlParams().id( contentId.toString() ) ) );

        assertThat( url ).startsWith( "/_/error/404?message=Not+Found" );
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

        final ComponentUrlParams params = new ComponentUrlParams().type( UrlTypeConstants.ABSOLUTE ).path( "/mycontent" ).param( "k", "v" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( Branch.from( "draft" ) )
            .build()
            .callWith( () -> this.service.componentUrl( params ) );

        assertEquals( "https://cdn.company.com/b/mycontent?k=v", url );
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

        final ComponentUrlParams params =
            new ComponentUrlParams().type( UrlTypeConstants.SERVER_RELATIVE ).path( "/mycontent" ).param( "k", "v" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( Branch.from( "draft" ) )
            .build()
            .callWith( () -> this.service.componentUrl( params ) );

        assertEquals( "/b/mycontent?k=v", url );
    }

    @Test
    void testNoRequestWithAbsoluteUrlTypeWithoutBaseUrlWithComponent()
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

        final ComponentUrlParams params =
            new ComponentUrlParams().type( UrlTypeConstants.ABSOLUTE ).path( "/mycontent" ).component( "/main/0" ).param( "k", "v" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( Branch.from( "master" ) )
            .build()
            .callWith( () -> this.service.componentUrl( params ) );

        assertEquals( "/site/myproject/master/a/b/mycontent/_/component/main/0?k=v", url );
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
                final ComponentUrlParams params = new ComponentUrlParams().type( UrlTypeConstants.ABSOLUTE )
                    .path( "/mycontent" )
                    .component( "/main/0" )
                    .param( "k", "v" )
                    .projectName( "explicit-project" );

                return this.service.componentUrl( params );
            } );

        assertEquals( "/site/explicit-project/context-branch/a/b/mycontent/_/component/main/0?k=v", url );
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
                final ComponentUrlParams params = new ComponentUrlParams().type( UrlTypeConstants.ABSOLUTE )
                    .path( "/mycontent" )
                    .component( "/main/0" )
                    .param( "k", "v" )
                    .projectName( "explicit-project" )
                    .branch( "explicit-branch" );

                return this.service.componentUrl( params );
            } );

        assertEquals( "/site/explicit-project/explicit-branch/a/b/mycontent/_/component/main/0?k=v", url );
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

        final ComponentUrlParams params = new ComponentUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" );

        final String url = this.service.componentUrl( params );
        assertEquals( "http://localhost/site/request-project/request-branch/a/b/mycontent", url );
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

        final ComponentUrlParams params = new ComponentUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" ).component( "/main/1" );

        final String url = this.service.componentUrl( params );
        assertEquals( "http://localhost/admin/site/preview/request-project/request-branch/a/b/mycontent/_/component/main/1", url );
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
            .callWith( () -> this.service.componentUrl(
                new ComponentUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" ).component( "/main/1" ) ) );

        assertEquals( "http://localhost/site/request-project/request-branch/a/b/mycontent/_/component/main/1", url );
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
        when( virtualHost.getTarget() ).thenReturn( "/site/request-project/request-branch/a" );
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
            .callWith( () -> this.service.componentUrl(
                new ComponentUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" ).component( "/main/1" ) ) );

        assertEquals( "http://localhost/source/b/mycontent/_/component/main/1", url );
    }

    @Test
    void testWithNonSiteRequestInContextWithoutBaseUrl()
    {
        portalRequest.setBaseUri( "/api" );
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
            .callWith( () -> this.service.componentUrl(
                new ComponentUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" ).component( "/main/1" ) ) );

        // Without virtual host rewrite to avoid OutOfScopeException
        assertEquals( "/site/context-project/context-branch/a/b/mycontent/_/component/main/1", url );
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
                final ComponentUrlParams params = new ComponentUrlParams().type( UrlTypeConstants.ABSOLUTE )
                    .path( "/mycontent" )
                    .component( "/main/0" )
                    .projectName( "explicit-project" )
                    .branch( "explicit-branch" )
                    .param( "k", "v" );

                return this.service.componentUrl( params );
            } );

        assertEquals( "https://cdn.company.com/b/mycontent/_/component/main/0?k=v", url );
    }

    @Test
    void testWithNonSiteRequestInContextWithBaseUrl()
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
            .callWith( () -> this.service.componentUrl(
                new ComponentUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" ).component( "/main/1" ) ) );

        assertEquals( "https://cdn.company.com/b/mycontent/_/component/main/1", url );
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
            .callWith( () -> this.service.componentUrl( new ComponentUrlParams().type( UrlTypeConstants.ABSOLUTE )
                                                            .id( "123456" )
                                                            .component( "/main/1" )
                                                            .projectName( "explicit-project" )
                                                            .branch( "explicit-branch" ) ) );

        assertEquals( "https://cdn.company.com/b/mycontent/_/component/main/1", url );
    }

    @Test
    void testWithSiteRequestWithComponentInRequest()
    {
        final PartComponent component = PartComponent.create().descriptor( "myapp:mycomp" ).build();
        Region.create().name( "main" ).add( component ).build();

        portalRequest.setBaseUri( "/site" );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setRawPath( "/site/request-project/request-branch/a/b/mycontent" );
        portalRequest.setComponent( component );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final Content content = ContentFixtures.newContent();
        when( this.contentService.getById( eq( content.getId() ) ) ).thenReturn( content );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.componentUrl( new ComponentUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" ) ) );

        assertEquals( "http://localhost/site/request-project/request-branch/a/b/mycontent/_/component/main/0", url );
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
            .callWith( () -> this.service.componentUrl( new ComponentUrlParams().type( UrlTypeConstants.ABSOLUTE )
                                                            .id( "123456" )
                                                            .component( "/main/1" )
                                                            .projectName( "explicit-project" )
                                                            .branch( "explicit-branch" ) ) );

        assertEquals( "/site/explicit-project/explicit-branch/a/b/mycontent/_/component/main/1", url );
    }
}
