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
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.portal.url.BaseUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.web.vhost.VirtualHost;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

        final String url = ContextBuilder.create().build().callWith( () -> this.service.baseUrl( params ) );
        assertThat( url ).startsWith( "/_/error/500?message=Something+went+wrong." );
    }

    @Test
    void testNoRequestWithNonContentProject()
    {
        PortalRequestAccessor.set( null );

        final BaseUrlParams params = BaseUrlParams.create().setId( "contentid" ).build();

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "non.content.project" ) )
            .branch( Branch.from( "branch" ) )
            .build()
            .callWith( () -> this.service.baseUrl( params ) );
        assertThat( url ).startsWith( "/_/error/500?message=Something+went+wrong." );
    }

    @Test
    void testNoRequestWithContextProjectAndWithoutContextBranch()
    {
        PortalRequestAccessor.set( null );

        final BaseUrlParams params = BaseUrlParams.create().setId( "contentid" ).build();

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .build()
            .callWith( () -> this.service.baseUrl( params ) );
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

        final BaseUrlParams params = BaseUrlParams.create().setId( contentId.toString() ).build();

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( Branch.from( "draft" ) )
            .build()
            .callWith( () -> this.service.baseUrl( params ) );

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

        assertEquals( "/site/myproject/master", url );
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

        assertEquals( "/site/explicit-project/context-branch", url );
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

        assertEquals( "/site/explicit-project/explicit-branch", url );
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

        assertEquals( "/site/explicit-project/explicit-branch", url );
    }

    private Site mockSite( final SiteConfigs siteConfigs )
    {
        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/a" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );
        mockDataWithSiteConfig( siteConfigs, site );
        return site;
    }

    private static SiteConfigs configuredBaseUrl( final String baseUrl, final ApplicationKey... extraApplications )
    {
        final PropertyTree config = new PropertyTree();
        config.addString( "baseUrl", baseUrl );

        final SiteConfigs.Builder siteConfigs =
            SiteConfigs.create().add( SiteConfig.create().application( ApplicationKey.from( "portal" ) ).config( config ).build() );
        for ( final ApplicationKey extraApplication : extraApplications )
        {
            siteConfigs.add( SiteConfig.create().application( extraApplication ).config( new PropertyTree() ).build() );
        }
        return siteConfigs.build();
    }

    private void activatePortalConfig( final String defaultMediaBaseUrl, final boolean mediaApiAutoMount )
    {
        final PortalConfig config = mock( PortalConfig.class );
        when( config.media_defaultBaseUrl() ).thenReturn( defaultMediaBaseUrl );
        when( config.legacy_mediaApiAutoMount_enabled() ).thenReturn( mediaApiAutoMount );
        this.service.activate( config );
    }

    private String resolveApiBaseUrl( final DescriptorKey api )
    {
        return ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( Branch.from( "draft" ) )
            .build()
            .callWith( () -> this.service.baseUrl( BaseUrlParams.create()
                                                       .setPath( "/mycontent" )
                                                       .setProjectName( "myproject" )
                                                       .setBranch( "draft" )
                                                       .setApi( api )
                                                       .build() ) );
    }

    @Test
    void testApiBaseUrlWithConfiguredBaseUrl()
    {
        PortalRequestAccessor.set( null );

        final Content content = ContentFixtures.newContent();
        final Site site = mockSite( configuredBaseUrl( "https://cdn.company.com" ) );

        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );
        when( contentService.getByPath( eq( ContentPath.from( "/mycontent" ) ) ) ).thenReturn( content );

        // media APIs are auto-mounted on sites by default: the configured Base URL is a mount base
        assertEquals( "https://cdn.company.com/_", resolveApiBaseUrl( DescriptorKey.from( "media:image" ) ) );
    }

    @Test
    void testApiBaseUrlWithConfiguredBaseUrlWithTrailingSlash()
    {
        PortalRequestAccessor.set( null );

        final Content content = ContentFixtures.newContent();
        final Site site = mockSite( configuredBaseUrl( "https://cdn.company.com/" ) );

        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );
        when( contentService.getByPath( eq( ContentPath.from( "/mycontent" ) ) ) ).thenReturn( content );

        assertEquals( "https://cdn.company.com/_", resolveApiBaseUrl( DescriptorKey.from( "media:attachment" ) ) );
    }

    @Test
    void testApiBaseUrlWithoutConfiguredBaseUrl()
    {
        PortalRequestAccessor.set( null );

        final Content content = ContentFixtures.newContent();
        final Site site = mockSite( SiteConfigs.empty() );

        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );
        when( contentService.getByPath( eq( ContentPath.from( "/mycontent" ) ) ) ).thenReturn( content );

        // no configured Base URL: URLs should stay request-based
        assertNull( resolveApiBaseUrl( DescriptorKey.from( "media:image" ) ) );
    }

    @Test
    void testApiBaseUrlWithoutConfiguredBaseUrlWithDefaultMediaBaseUrl()
    {
        PortalRequestAccessor.set( null );

        activatePortalConfig( "https://media.example.com", true );

        final Content content = ContentFixtures.newContent();
        final Site site = mockSite( SiteConfigs.empty() );

        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );
        when( contentService.getByPath( eq( ContentPath.from( "/mycontent" ) ) ) ).thenReturn( content );

        // the default media base is used verbatim: it points directly at the API root
        assertEquals( "https://media.example.com", resolveApiBaseUrl( DescriptorKey.from( "media:image" ) ) );
    }

    @Test
    void testApiBaseUrlAutoMountDisabledWithoutMounts()
    {
        PortalRequestAccessor.set( null );

        activatePortalConfig( "https://media.example.com", false );

        final Content content = ContentFixtures.newContent();
        final Site site = mockSite( configuredBaseUrl( "https://cdn.company.com" ) );

        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );
        when( contentService.getByPath( eq( ContentPath.from( "/mycontent" ) ) ) ).thenReturn( content );

        // auto-mount disabled and the site does not mount media APIs: divert to the default media base
        assertEquals( "https://media.example.com", resolveApiBaseUrl( DescriptorKey.from( "media:image" ) ) );
    }

    @Test
    void testApiBaseUrlAutoMountDisabledWithBothMediaApisMounted()
    {
        PortalRequestAccessor.set( null );

        activatePortalConfig( null, false );

        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );
        final Content content = ContentFixtures.newContent();
        final Site site = mockSite( configuredBaseUrl( "https://cdn.company.com", applicationKey ) );

        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );
        when( contentService.getByPath( eq( ContentPath.from( "/mycontent" ) ) ) ).thenReturn( content );

        when( siteService.getDescriptor( eq( applicationKey ) ) ).thenReturn( SiteDescriptor.create()
                                                                                   .applicationKey( applicationKey )
                                                                                   .apiMounts( DescriptorKeys.from(
                                                                                       DescriptorKey.from( "media:image" ),
                                                                                       DescriptorKey.from( "media:attachment" ) ) )
                                                                                   .build() );

        assertEquals( "https://cdn.company.com/_", resolveApiBaseUrl( DescriptorKey.from( "media:image" ) ) );
    }

    @Test
    void testApiBaseUrlAutoMountDisabledMediaBasesDiverge()
    {
        PortalRequestAccessor.set( null );

        activatePortalConfig( null, false );

        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );
        final Content content = ContentFixtures.newContent();
        final Site site = mockSite( configuredBaseUrl( "https://cdn.company.com", applicationKey ) );

        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );
        when( contentService.getByPath( eq( ContentPath.from( "/mycontent" ) ) ) ).thenReturn( content );

        when( siteService.getDescriptor( eq( applicationKey ) ) ).thenReturn( SiteDescriptor.create()
                                                                                   .applicationKey( applicationKey )
                                                                                   .apiMounts( DescriptorKeys.from(
                                                                                       DescriptorKey.from( "media:image" ) ) )
                                                                                   .build() );

        // mounts are honored per API: the two media bases diverge when only one API is mounted
        assertEquals( "https://cdn.company.com/_", resolveApiBaseUrl( DescriptorKey.from( "media:image" ) ) );
        assertNull( resolveApiBaseUrl( DescriptorKey.from( "media:attachment" ) ) );
    }

    @Test
    void testApiBaseUrlNonMediaApiMounted()
    {
        PortalRequestAccessor.set( null );

        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );
        final Content content = ContentFixtures.newContent();
        final Site site = mockSite( configuredBaseUrl( "https://cdn.company.com", applicationKey ) );

        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );
        when( contentService.getByPath( eq( ContentPath.from( "/mycontent" ) ) ) ).thenReturn( content );

        when( siteService.getDescriptor( eq( applicationKey ) ) ).thenReturn( SiteDescriptor.create()
                                                                                   .applicationKey( applicationKey )
                                                                                   .apiMounts( DescriptorKeys.from(
                                                                                       DescriptorKey.from( "myapplication:myapi" ) ) )
                                                                                   .build() );

        assertEquals( "https://cdn.company.com/_", resolveApiBaseUrl( DescriptorKey.from( "myapplication:myapi" ) ) );
    }

    @Test
    void testApiBaseUrlNonMediaApiNotMounted()
    {
        PortalRequestAccessor.set( null );

        final Content content = ContentFixtures.newContent();
        final Site site = mockSite( configuredBaseUrl( "https://cdn.company.com" ) );

        when( contentService.getNearestSite( eq( content.getId() ) ) ).thenReturn( site );
        when( contentService.getByPath( eq( ContentPath.from( "/mycontent" ) ) ) ).thenReturn( content );

        // non-media APIs are never auto-mounted: without an explicit mount there is no anchor
        assertNull( resolveApiBaseUrl( DescriptorKey.from( "myapplication:myapi" ) ) );
    }

    @Test
    void testApiBaseUrlWithContentSupplierAnchor()
    {
        PortalRequestAccessor.set( null );

        final Site site = mockSite( configuredBaseUrl( "https://cdn.company.com" ) );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( Branch.from( "draft" ) )
            .build()
            .callWith( () -> this.service.baseUrl( BaseUrlParams.create()
                                                       .setContent( () -> site )
                                                       .setProjectName( "myproject" )
                                                       .setBranch( "draft" )
                                                       .setApi( DescriptorKey.from( "media:image" ) )
                                                       .build() ) );

        // the anchor is taken from the supplier: no content lookup is made
        assertEquals( "https://cdn.company.com/_", url );
    }
}
