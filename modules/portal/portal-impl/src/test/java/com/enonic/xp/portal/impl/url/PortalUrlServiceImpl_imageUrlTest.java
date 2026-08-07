package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.macro.MacroService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.RedirectChecksumService;
import com.enonic.xp.portal.url.ImageUrlGeneratorParams;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PortalUrlGeneratorService;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.webapp.WebappDescriptor;
import com.enonic.xp.webapp.WebappService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PortalUrlServiceImpl_imageUrlTest
{
    private ContentService contentService;

    private PortalUrlService service;

    private PortalRequest portalRequest;

    private HttpServletRequest req;

    private PortalUrlGeneratorService portalUrlGeneratorService;

    private WebappService webappService;

    private SiteService siteService;

    @BeforeEach
    void setUp()
    {
        this.contentService = mock( ContentService.class );

        webappService = mock( WebappService.class );
        siteService = mock( SiteService.class );
        portalUrlGeneratorService = new PortalUrlGeneratorServiceImpl( webappService, siteService );

        this.service = new PortalUrlServiceImpl( this.contentService, mock( ResourceService.class ), mock( MacroService.class ),
                                                 mock( StyleDescriptorService.class ), mock( RedirectChecksumService.class ),
                                                 mock( ProjectService.class ), portalUrlGeneratorService, mock( SiteService.class ) );

        req = mock( HttpServletRequest.class );

        portalRequest = new PortalRequest();
        portalRequest.setMode( RenderMode.LIVE );
        portalRequest.setRawRequest( req );

        PortalRequestAccessor.set( portalRequest );
    }

    @AfterEach
    void destroy()
    {
        PortalRequestAccessor.remove();
    }

    @Test
    void testNoRequestAndWithoutContext()
    {
        PortalRequestAccessor.set( null );

        final ImageUrlParams params = new ImageUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" ).scale( "max(300)" );

        final String url = ContextBuilder.create().build().callWith( () -> this.service.imageUrl( params ) );

        assertThat( url ).startsWith( "/_/error/500?message=Something+went+wrong." );
    }

    @Test
    void testNoRequestAndWithoutBranchInContext()
    {
        PortalRequestAccessor.set( null );

        final ImageUrlParams params = new ImageUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" ).scale( "max(300)" );

        final String url =
            ContextBuilder.create().repositoryId( "com.enonic.cms.context-repo" ).build().callWith( () -> this.service.imageUrl( params ) );

        assertThat( url ).startsWith( "/_/error/500?message=Something+went+wrong." );
    }

    @Test
    void testNoRequestAndWithBaseUrl()
    {
        PortalRequestAccessor.set( null );

        final ImageUrlParams params =
            new ImageUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" ).scale( "max(300)" ).baseUrl( "baseUrl" );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final String url = ContextBuilder.create()
            .repositoryId( "com.enonic.cms.context-project" )
            .branch( "context-branch" )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertEquals( "baseUrl/_/media:image/context-project:context-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
                      url );
    }

    @Test
    void testNoRequestAndWithoutBaseUrl()
    {
        PortalRequestAccessor.set( null );

        final ImageUrlParams params = new ImageUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" ).scale( "max(300)" );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final String url = ContextBuilder.create()
            .repositoryId( "com.enonic.cms.context-project" )
            .branch( "context-branch" )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertEquals( "/api/media:image/context-project:context-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
                      url );
    }

    @Test
    void testNoRequestAndWithEmptyBaseUrl()
    {
        PortalRequestAccessor.set( null );

        final ImageUrlParams params = new ImageUrlParams().id( "123456" ).scale( "max(300)" ).baseUrl( "" );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final String url = ContextBuilder.create()
            .repositoryId( "com.enonic.cms.context-project" )
            .branch( "context-branch" )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertEquals( "/api/media:image/context-project:context-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
                      url );
    }

    @Test
    void testNoRequestAndWithRootBaseUrl()
    {
        PortalRequestAccessor.set( null );

        final ImageUrlParams params = new ImageUrlParams().id( "123456" ).scale( "max(300)" ).baseUrl( "/" );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final String url = ContextBuilder.create()
            .repositoryId( "com.enonic.cms.context-project" )
            .branch( "context-branch" )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertEquals( "/_/media:image/context-project:context-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
                      url );
    }

    @Test
    void testNoRequestAndWithBaseUrlButWithoutIdAndPath()
    {
        PortalRequestAccessor.set( null );

        final ImageUrlParams params = new ImageUrlParams().scale( "max(300)" ).baseUrl( "baseUrl" );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final String url = ContextBuilder.create()
            .repositoryId( "com.enonic.cms.context-project" )
            .branch( "context-branch" )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertThat( url ).startsWith( "/_/error/500?message=Something+went+wrong." );
    }

    @Test
    void testNoRequestAndContentNotFoundById()
    {
        PortalRequestAccessor.set( null );

        final ImageUrlParams params = new ImageUrlParams().id( "123456" ).scale( "max(300)" );

        when( contentService.getById( any( ContentId.class ) ) ).thenReturn( null );

        final String url = ContextBuilder.create()
            .repositoryId( "com.enonic.cms.context-project" )
            .branch( "context-branch" )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertThat( url ).startsWith( "/_/error/404?message=Not+Found." );
    }

    @Test
    void testNoRequestAndContentNotFoundByPath()
    {
        PortalRequestAccessor.set( null );

        final ImageUrlParams params = new ImageUrlParams().path( "/path" ).scale( "max(300)" );

        when( contentService.getByPath( any( ContentPath.class ) ) ).thenReturn( null );

        final String url = ContextBuilder.create()
            .repositoryId( "com.enonic.cms.context-project" )
            .branch( "context-branch" )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertThat( url ).startsWith( "/_/error/404?message=Not+Found." );
    }

    @Test
    void testNoRequestAndWithoutContextAndWithExplicitProjectAndBranch()
    {
        PortalRequestAccessor.set( null );

        final ImageUrlParams params = new ImageUrlParams().type( UrlTypeConstants.ABSOLUTE )
            .id( "123456" )
            .scale( "max(300)" )
            .projectName( "explicit-project" )
            .branch( "explicit-branch" );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final String url = this.service.imageUrl( params );

        assertEquals( "/api/media:image/explicit-project:explicit-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
                      url );
    }

    @Test
    void testWithNoSiteRequestInContextWithVirtualHost()
    {
        portalRequest.setMode( null );
        portalRequest.setBaseUri( "/api/app:api" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/api/app:api/path" );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/source" );
        when( virtualHost.getTarget() ).thenReturn( "/api/app:api" );
        when( portalRequest.getRawRequest().getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final ImageUrlParams params = new ImageUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" ).scale( "max(300)" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        // APIs are addressed as siblings of the current endpoint: the vhost exposes only the
        // endpoint itself, so the media API is expected to be mounted next to it
        assertEquals(
            "http://localhost/media:image/context-project:context-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
            url );
    }

    @Test
    void testWithNoSiteRequestWithBaseUrlIgnoreRewrite()
    {
        portalRequest.setMode( null );
        portalRequest.setBaseUri( "/webapp/myapp" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/webapp/myapp/path" );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/source" );
        when( virtualHost.getTarget() ).thenReturn( "/webapp/myapp" );
        when( portalRequest.getRawRequest().getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final ImageUrlParams params = new ImageUrlParams().id( "123456" ).scale( "max(300)" ).baseUrl( "baseUrl" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertEquals( "baseUrl/_/media:image/context-project:context-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
                      url );
    }

    @Test
    void testWithNoSiteRequestInContextWithDefaultVirtualHost()
    {
        portalRequest.setMode( null );
        portalRequest.setBaseUri( "/api/app:api" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/api/app:api" );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/" );
        when( virtualHost.getTarget() ).thenReturn( "/" );
        when( portalRequest.getRawRequest().getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final ImageUrlParams params = new ImageUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" ).scale( "max(300)" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertEquals(
            "http://localhost/api/media:image/context-project:context-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
            url );
    }

    @Test
    void testWithNoSiteRequestNonApiBaseUri()
    {
        portalRequest.setMode( null );
        portalRequest.setBaseUri( "/webapp/myapp" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/webapp/myapp/path" );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final ImageUrlParams params = new ImageUrlParams().id( "123456" ).scale( "max(300)" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertEquals(
            "/webapp/myapp/_/media:image/context-project:context-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
            url );
    }

    @Test
    void testWithNoSiteRequestNonApiBaseUriWithVirtualHost()
    {
        portalRequest.setMode( null );
        portalRequest.setBaseUri( "/webapp/myapp" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/webapp/myapp/path" );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/source" );
        when( virtualHost.getTarget() ).thenReturn( "/webapp/myapp" );
        when( portalRequest.getRawRequest().getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final ImageUrlParams params = new ImageUrlParams().id( "123456" ).scale( "max(300)" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertEquals( "/source/_/media:image/context-project:context-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
                      url );
    }

    @Test
    void testWithNoSiteRequestWithEmptyBaseUriWithIgnoringRewrite()
    {
        portalRequest.setMode( null );
        portalRequest.setBaseUri( "" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/path/sub-path" );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/source" );
        when( virtualHost.getTarget() ).thenReturn( "/jax-rs" );
        when( portalRequest.getRawRequest().getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final ImageUrlParams params = new ImageUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" ).scale( "max(300)" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertEquals( "/api/media:image/context-project:context-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
                      url );
    }

    @Test
    void testWithSiteRequestWithoutContext()
    {
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setRawPath( "/site/request-project/request-branch/mysite" );
        portalRequest.setContentPath( ContentPath.from( "/mysite/123456" ) );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( media.getPath() ).thenReturn( ContentPath.from( "/mysite/123456" ) );
        when( media.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        when( contentService.getByPath( eq( media.getPath() ) ) ).thenReturn( media );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );
        portalRequest.setSite( site );

        final ImageUrlParams params = new ImageUrlParams().scale( "max(300)" );

        final String url = this.service.imageUrl( params );

        assertEquals(
            "/site/request-project/request-branch/mysite/_/media:image/request-project:request-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
            url );
    }

    @Test
    void testWithSiteRequestWithContext()
    {
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setRawPath( "/site/request-project/request-branch/mysite" );
        portalRequest.setContentPath( ContentPath.from( "/mysite/123456" ) );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( media.getPath() ).thenReturn( ContentPath.from( "/mysite/123456" ) );
        when( media.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        when( contentService.getByPath( eq( media.getPath() ) ) ).thenReturn( media );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );
        portalRequest.setSite( site );

        final ImageUrlParams params = new ImageUrlParams().scale( "max(300)" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertEquals(
            "/site/request-project/request-branch/mysite/_/media:image/request-project:request-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
            url );
    }

    @Test
    void testWithSiteRequestAutoMountDisabledWithoutMounts()
    {
        activateMediaApiAutoMountDisabled( null );

        setupSiteRequest( SiteConfigs.empty() );

        final ImageUrlParams params = new ImageUrlParams().scale( "max(300)" );

        final String url = this.service.imageUrl( params );

        // auto-mount disabled and the site does not mount media APIs: generation diverts
        // to the canonical /api form instead of a site URL that dispatch would 404
        assertEquals( "/api/media:image/request-project:request-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
                      url );
    }

    @Test
    void testWithSiteRequestAutoMountDisabledVhostContextOutranksDefaultMediaBaseUrl()
    {
        activateMediaApiAutoMountDisabled( "https://media.example.com" );

        setupSiteRequest( SiteConfigs.empty() );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getContext() ).thenReturn( java.util.Map.of( "apiBaseUrl", "https://apis.example.com" ) );
        when( req.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        final ImageUrlParams params = new ImageUrlParams().scale( "max(300)" );

        // an API location declared on the matched vhost mapping wins over the
        // instance-wide default media base
        assertEquals(
            "https://apis.example.com/media:image/request-project:request-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
            this.service.imageUrl( params ) );
    }

    @Test
    void testWithSiteRequestAutoMountDisabledWithoutMountsWithDefaultMediaBaseUrl()
    {
        activateMediaApiAutoMountDisabled( "https://media.example.com" );

        setupSiteRequest( SiteConfigs.empty() );

        final ImageUrlParams params = new ImageUrlParams().scale( "max(300)" );

        final String url = this.service.imageUrl( params );

        assertEquals(
            "https://media.example.com/media:image/request-project:request-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
            url );
    }

    @Test
    void testWithSiteRequestAutoMountDisabledWithBothMediaApisMounted()
    {
        activateMediaApiAutoMountDisabled( "https://media.example.com" );

        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );
        setupSiteRequest( SiteConfigs.create()
                              .add( SiteConfig.create().application( applicationKey ).config( new PropertyTree() ).build() )
                              .build() );

        when( siteService.getDescriptor( eq( applicationKey ) ) ).thenReturn( SiteDescriptor.create()
                                                                                  .applicationKey( applicationKey )
                                                                                  .apiMounts( DescriptorKeys.from(
                                                                                      DescriptorKey.from( "media:image" ),
                                                                                      DescriptorKey.from( "media:attachment" ) ) )
                                                                                  .build() );

        final ImageUrlParams params = new ImageUrlParams().scale( "max(300)" );

        final String url = this.service.imageUrl( params );

        assertEquals(
            "/site/request-project/request-branch/mysite/_/media:image/request-project:request-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
            url );
    }

    @Test
    void testSiteRenderInAdminToolAutoMountDisabledKeepsAdminAnchor()
    {
        activateMediaApiAutoMountDisabled( "https://media.example.com" );

        setupSiteRequest( SiteConfigs.empty() );
        portalRequest.setMode( RenderMode.EDIT );
        portalRequest.setBaseUri( "/admin/com.enonic.app.contentstudio/site/edit" );
        portalRequest.setRawPath( "/admin/com.enonic.app.contentstudio/site/edit/request-project/request-branch/mysite" );

        final ImageUrlParams params = new ImageUrlParams().scale( "max(300)" );

        final String url = this.service.imageUrl( params );

        // a site rendered inside an admin tool follows the admin tool rule when the site does
        // not mount media APIs: media anchors at the hosting tool's own "_" endpoint, staying
        // within the admin session (the media path carries the project:branch context)
        assertEquals(
            "/admin/com.enonic.app.contentstudio/site/_/media:image/request-project:request-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
            url );
    }

    private void setupSiteRequest( final SiteConfigs siteConfigs )
    {
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setRawPath( "/site/request-project/request-branch/mysite" );
        portalRequest.setContentPath( ContentPath.from( "/mysite/123456" ) );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( media.getPath() ).thenReturn( ContentPath.from( "/mysite/123456" ) );
        when( media.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        when( contentService.getByPath( eq( media.getPath() ) ) ).thenReturn( media );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );

        final PropertyTree data = new PropertyTree();
        SiteConfigsDataSerializer.toData( siteConfigs, data.getRoot() );
        when( site.getData() ).thenReturn( data );

        portalRequest.setSite( site );
    }

    private void activateMediaApiAutoMountDisabled( final String defaultMediaBaseUrl )
    {
        final PortalConfig config = mock( PortalConfig.class );
        when( config.media_defaultBaseUrl() ).thenReturn( defaultMediaBaseUrl );
        when( config.legacy_mediaApiAutoMount_enabled() ).thenReturn( false );
        ( (PortalUrlGeneratorServiceImpl) portalUrlGeneratorService ).activate( config );
    }

    @Test
    void testWithSiteRequestWithoutVirtualHost()
    {
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setRawPath( "/site/request-project/request-branch/mysite" );
        portalRequest.setContentPath( ContentPath.from( "/mysite/123456" ) );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/source" );
        when( virtualHost.getTarget() ).thenReturn( "/site/request-project/request-branch/mysite" );
        when( portalRequest.getRawRequest().getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( media.getPath() ).thenReturn( ContentPath.from( "/mysite/123456" ) );
        when( media.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        when( contentService.getByPath( eq( media.getPath() ) ) ).thenReturn( media );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );
        portalRequest.setSite( site );

        final ImageUrlParams params = new ImageUrlParams().type( UrlTypeConstants.ABSOLUTE ).scale( "max(300)" );

        final String url = this.service.imageUrl( params );

        assertEquals(
            "http://localhost/source/_/media:image/request-project:request-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
            url );
    }

    @Test
    void testWithSiteRequestWithExplicitProjectAndBranch()
    {
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setRawPath( "/site/request-project/request-branch/mysite" );
        portalRequest.setContentPath( ContentPath.from( "/mysite/123456" ) );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( media.getPath() ).thenReturn( ContentPath.from( "/mysite/123456" ) );
        when( media.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        when( contentService.getByPath( eq( media.getPath() ) ) ).thenReturn( media );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );
        portalRequest.setSite( site );

        final ImageUrlParams params = new ImageUrlParams().type( UrlTypeConstants.SERVER_RELATIVE )
            .scale( "max(300)" )
            .projectName( "explicit-project" )
            .branch( "explicit-branch" );

        final String url = this.service.imageUrl( params );

        assertEquals(
            "/site/request-project/request-branch/mysite/_/media:image/explicit-project:explicit-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
            url );
    }

    @Test
    void testWithSiteRequestWithBaseUrlWithoutContext()
    {
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.request-project" ) );
        portalRequest.setBranch( Branch.from( "request-branch" ) );
        portalRequest.setRawPath( "/site/request-project/request-branch/mysite" );
        portalRequest.setContentPath( ContentPath.from( "/mysite/123456" ) );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( media.getPath() ).thenReturn( ContentPath.from( "/mysite/123456" ) );

        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );
        portalRequest.setSite( site );

        final ImageUrlParams params = new ImageUrlParams().id( "123456" ).scale( "max(300)" ).baseUrl( "baseUrl" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertEquals( "baseUrl/_/media:image/context-project:context-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
                      url );
    }

    @Test
    void testImageUrl()
    {
        ImageUrlGeneratorParams params = ImageUrlGeneratorParams.create()
            .setBaseUrl( "baseUrl" )
            .setMedia( () -> mockMedia( "123456", "mycontent.png" ) )
            .setProjectName( () -> ProjectName.from( "project" ) )
            .setBranch( () -> Branch.from( "branch" ) )
            .setScale( "max(300)" )
            .build();

        final String url = this.portalUrlGeneratorService.imageUrl( params );

        assertEquals( "baseUrl/_/media:image/project:branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png", url );
    }

    private void mockWebappWithMediaApis()
    {
        final WebappDescriptor descriptor = WebappDescriptor.create()
            .applicationKey( ApplicationKey.from( "myapp" ) )
            .apiMounts( DescriptorKeys.from( DescriptorKey.from( "media:image" ), DescriptorKey.from( "media:attachment" ) ) )
            .build();
        when( webappService.getDescriptor( ApplicationKey.from( "myapp" ) ) ).thenReturn( descriptor );
    }

    private void activateDefaultMediaBaseUrl( final String value )
    {
        final PortalConfig config = mock( PortalConfig.class );
        when( config.media_defaultBaseUrl() ).thenReturn( value );
        when( config.legacy_mediaApiAutoMount_enabled() ).thenReturn( true );
        ( (PortalUrlGeneratorServiceImpl) portalUrlGeneratorService ).activate( config );
    }


    @Test
    void testWithWebappRequestWithoutMediaApiMountsWithDefaultMediaBaseUrl()
    {
        activateDefaultMediaBaseUrl( "https://media.example.com" );

        portalRequest.setMode( null );
        portalRequest.setBaseUri( "/webapp/myapp" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/webapp/myapp/path" );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final ImageUrlParams params = new ImageUrlParams().id( "123456" ).scale( "max(300)" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertEquals(
            "https://media.example.com/media:image/context-project:context-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
            url );
    }

    @Test
    void testWithWebappRequestWithMediaApiMountsIgnoresDefaultMediaBaseUrl()
    {
        activateDefaultMediaBaseUrl( "https://media.example.com" );

        portalRequest.setMode( null );
        portalRequest.setBaseUri( "/webapp/myapp" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/webapp/myapp/path" );
        mockWebappWithMediaApis();

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final ImageUrlParams params = new ImageUrlParams().id( "123456" ).scale( "max(300)" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        // the webapp declares the media APIs: its own "_" endpoint outranks the default media base
        assertEquals(
            "/webapp/myapp/_/media:image/context-project:context-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
            url );
    }

    @Test
    void testWithApiRequestWithDefaultMediaBaseUrl()
    {
        activateDefaultMediaBaseUrl( "https://media.example.com" );

        portalRequest.setMode( null );
        portalRequest.setBaseUri( "/api/app:api" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/api/app:api/path" );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final ImageUrlParams params = new ImageUrlParams().id( "123456" ).scale( "max(300)" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        // the configured default media base outranks the sibling assumption on API endpoints
        assertEquals(
            "https://media.example.com/media:image/context-project:context-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
            url );
    }

    @Test
    void testWithAdminToolRequestIgnoresDefaultMediaBaseUrl()
    {
        activateDefaultMediaBaseUrl( "https://media.example.com" );

        portalRequest.setMode( null );
        portalRequest.setBaseUri( "/admin/myapp/mytool" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/admin/myapp/mytool" );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final ImageUrlParams params = new ImageUrlParams().id( "123456" ).scale( "max(300)" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        // media URLs on admin mounts always stay "_"-anchored: within the authenticated admin session
        assertEquals(
            "/admin/myapp/mytool/_/media:image/context-project:context-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
            url );
    }

    @Test
    void testWithAdminToolRequestAutoMountDisabled()
    {
        activateMediaApiAutoMountDisabled( "https://media.example.com" );

        portalRequest.setMode( null );
        portalRequest.setBaseUri( "/admin/myapp/mytool" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/admin/myapp/mytool" );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final ImageUrlParams params = new ImageUrlParams().id( "123456" ).scale( "max(300)" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        // the auto-mount flag only affects site mounts: admin media URLs stay "_"-anchored
        assertEquals(
            "/admin/myapp/mytool/_/media:image/context-project:context-branch/123456:0a350f43700951cdcca1574f448a7e22/max-300/mycontent.png",
            url );
    }

    private Media mockMedia( String id, String name, String attachmentHash )
    {
        final Attachment attachment =
            Attachment.create().name( name ).mimeType( "image/png" ).sha512( attachmentHash ).label( "source" ).build();

        final Media media = mock( Media.class );

        final ContentId contentId = ContentId.from( id );

        when( media.getId() ).thenReturn( contentId );
        when( media.getPath() ).thenReturn( ContentPath.from( "/" + id ) );
        when( media.getName() ).thenReturn( ContentName.from( name ) );
        when( media.getData() ).thenReturn( new PropertyTree() );
        when( media.getAttachments() ).thenReturn( Attachments.from( attachment ) );

        return media;
    }

    private Media mockMedia( String id, String name )
    {
        return mockMedia( id, name, "ec25d6e4126c7064f82aaab8b34693fc" );
    }
}
