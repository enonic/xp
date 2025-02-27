package com.enonic.xp.portal.impl.url;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
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
import com.enonic.xp.portal.impl.RedirectChecksumService;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.Project;
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
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.web.vhost.VirtualHost;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PortalUrlServiceImpl_attachmentUrlTest
{
    private ContentService contentService;

    private ProjectService projectService;

    private PortalUrlService service;

    private PortalRequest portalRequest;

    @BeforeEach
    public void setUp()
    {
        this.contentService = mock( ContentService.class );
        this.projectService = mock( ProjectService.class );

        UrlGeneratorParamsAdapter urlGeneratorParamsAdapter = new UrlGeneratorParamsAdapter( this.contentService, this.projectService );

        this.service = new PortalUrlServiceImpl( this.contentService, mock( ResourceService.class ), mock( MacroService.class ),
                                                 mock( StyleDescriptorService.class ), mock( RedirectChecksumService.class ),
                                                 urlGeneratorParamsAdapter );

        final HttpServletRequest req = mock( HttpServletRequest.class );

        portalRequest = new PortalRequest();
        portalRequest.setBranch( Branch.from( "draft" ) );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRawPath( "/site/myproject/draft" );
        portalRequest.setRawRequest( req );
    }

    @Test
    public void destroy()
    {
        PortalRequestAccessor.remove();
    }

    @Test
    public void createAttachmentUrlUseCase1()
    {
        // Request
        // Site based request
        // Generate attachmentUrl for request mode:
        //  - for baseUrl `project` and `branch` are used from the portalRequest
        //  - for media path `project` and `branch` are used from the params
        // Nearest site is not found

        PortalRequestAccessor.set( portalRequest );

        final AttachmentUrlParams params = new AttachmentUrlParams().type( UrlTypeConstants.SERVER_RELATIVE )
            .projectName( "myproject2" )
            .branch( "master" )
            .id( "123456" );

        when( contentService.findNearestSiteByPath( any( ContentPath.class ) ) ).thenReturn( null );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/site/myproject/draft/_/media:attachment/myproject2/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png", url );
    }

    @Test
    public void createImageUrlUseCase2()
    {
        // Request
        // Site based request
        // Generate url for request mode, for baseUrl `project` and `branch` are used from the portalRequest
        // `project` and `branch` are not provided in the params and they will be resolved from the portalRequest
        // Nearest site is not found

        PortalRequestAccessor.set( portalRequest );

        final AttachmentUrlParams params = new AttachmentUrlParams().type( UrlTypeConstants.SERVER_RELATIVE ).id( "123456" );

        when( contentService.findNearestSiteByPath( any( ContentPath.class ) ) ).thenReturn( null );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/site/myproject/draft/_/media:attachment/myproject:draft/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png",
                      url );
    }

    @Test
    public void createImageUrlUseCase3()
    {
        // Request
        // Admin Site based request
        // Generate url for request mode, for baseUrl `project` and `branch` are used from the portalRequest
        // `project` and `branch` are not provided in the params and they will be resolved from the portalRequest
        // Nearest site is not found

        portalRequest.setBaseUri( "/admin/site/preview" );
        portalRequest.setRawPath( "/admin/site/preview/myproject/draft" );

        PortalRequestAccessor.set( portalRequest );

        final AttachmentUrlParams params = new AttachmentUrlParams().type( UrlTypeConstants.SERVER_RELATIVE ).id( "123456" );

        when( contentService.findNearestSiteByPath( any( ContentPath.class ) ) ).thenReturn( null );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final String url = this.service.attachmentUrl( params );
        assertEquals(
            "/admin/site/preview/myproject/draft/_/media:attachment/myproject:draft/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png",
            url );
    }

    @Test
    public void createImageUrlUseCase4()
    {
        // Request
        // Site based request
        // Generate url for request mode, for baseUrl `project` and `branch` are used from the portalRequest
        // `project` and `branch` are not provided in the params and they will be resolved from the portalRequest

        portalRequest.setContentPath( ContentPath.from( "/mysite/123456" ) );

        PortalRequestAccessor.set( portalRequest );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( media.getPath() ).thenReturn( ContentPath.from( "/mysite/123456" ) );
        when( media.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );
        when( contentService.getByPath( eq( media.getPath() ) ) ).thenReturn( media );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );

        when( contentService.findNearestSiteByPath( eq( media.getPath() ) ) ).thenReturn( site );

        final AttachmentUrlParams params =
            new AttachmentUrlParams().type( UrlTypeConstants.SERVER_RELATIVE ).id( "123456" ).baseUrlKey( "siteId" );

        final String url = this.service.attachmentUrl( params );
        assertEquals(
            "/site/myproject/draft/mysite/_/media:attachment/myproject:draft/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png", url );
    }

    @Test
    public void createImageUrlUseCase5()
    {
        // Request
        // Site based request
        // Generate url for request mode, for baseUrl `project` and `branch` are used from the portalRequest
        // `project` and `branch` are not provided in the params and they will be resolved from the portalRequest
        // Throw exception when finding nearest site

        portalRequest.setContentPath( ContentPath.from( "/mysite/123456" ) );

        PortalRequestAccessor.set( portalRequest );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( media.getPath() ).thenReturn( ContentPath.from( "/mysite/123456" ) );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final AttachmentUrlParams params =
            new AttachmentUrlParams().offline( false ).type( UrlTypeConstants.SERVER_RELATIVE ).id( "123456" );

        when( contentService.findNearestSiteByPath( any( ContentPath.class ) ) ).thenThrow( new RuntimeException() );

        final String url = this.service.attachmentUrl( params );
        assertTrue( url.startsWith( "/_/error/500?message=Something+went+wrong.+" ) );
    }

    @Test
    public void createImageUrlUseCase6()
    {
        // Request
        // Webapp based request
        // `project` and `branch` are not provided in the params and they will be resolved from the portalRequest

        portalRequest.setBaseUri( "/webapp/myapp" );
        portalRequest.setRawPath( "/webapp/myapp/path" );

        PortalRequestAccessor.set( portalRequest );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( media.getPath() ).thenReturn( ContentPath.from( "/mysite/123456" ) );
        when( media.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final AttachmentUrlParams params = new AttachmentUrlParams().type( UrlTypeConstants.SERVER_RELATIVE ).id( "123456" );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/webapp/myapp/_/media:attachment/myproject:draft/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png", url );
    }

    @Test
    public void createImageUrlUseCase7()
    {
        // Request
        // Site based request
        // Generate url for request mode, for baseUrl `project` and `branch` are used from the portalRequest
        // `project` and `branch` are not provided in the params and they will be resolved from the portalRequest
        // baseUrl must be overridden by virtual host

        portalRequest.setContentPath( ContentPath.from( "/mysite/123456" ) );

        VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/main" );
        when( virtualHost.getTarget() ).thenReturn( "/site/myproject/draft/mysite" );
        when( portalRequest.getRawRequest().getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        PortalRequestAccessor.set( portalRequest );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( media.getPath() ).thenReturn( ContentPath.from( "/mysite/123456" ) );
        when( media.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );
        when( contentService.getByPath( eq( media.getPath() ) ) ).thenReturn( media );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/mysite" ) );

        when( contentService.findNearestSiteByPath( eq( media.getPath() ) ) ).thenReturn( site );

        final AttachmentUrlParams params =
            new AttachmentUrlParams().type( UrlTypeConstants.SERVER_RELATIVE ).id( "123456" ).baseUrlKey( "siteId" );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/main/_/media:attachment/myproject:draft/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png", url );
    }

    @Test
    public void createImageUrlUseCase8()
    {
        // Offline
        // Generate url for offline mode, for baseUrl `project` and `branch` are used from the Context
        // `project` and `branch` are provided in the params and used for media path
        // Nearest site is not found
        // baseUrl not found on project level
        // fallback to /api

        final AttachmentUrlParams params = new AttachmentUrlParams().offline( true )
            .type( UrlTypeConstants.SERVER_RELATIVE )
            .projectName( "myproject2" )
            .branch( "master" )
            .id( "123456" );

        when( contentService.findNearestSiteByPath( any( ContentPath.class ) ) ).thenReturn( null );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( Branch.from( "draft" ) )
            .build()
            .callWith( () -> this.service.attachmentUrl( params ) );

        assertEquals( "/api/media:attachment/myproject2/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png", url );
    }

    @Test
    public void createImageUrlUseCase9()
    {
        // Offline
        // Generate url for offline mode, for baseUrl `project` and `branch` are used from the Context
        // `project` and `branch` are provided in the params and used for media path
        // Nearest site is not found
        // baseUrl found on project level

        final AttachmentUrlParams params = new AttachmentUrlParams().offline( true )
            .type( UrlTypeConstants.ABSOLUTE )
            .projectName( "myproject2" )
            .branch( "master" )
            .id( "123456" );

        when( contentService.findNearestSiteByPath( any( ContentPath.class ) ) ).thenReturn( null );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final PropertyTree config = new PropertyTree();
        config.addString( "baseUrl", "https://cdn.company.com" );

        SiteConfigs siteConfigs = SiteConfigs.create()
            .add( SiteConfig.create().application( ApplicationKey.from( "com.enonic.xp.site" ) ).config( config ).build() )
            .build();

        final Project project = mock( Project.class );
        when( project.getSiteConfigs() ).thenReturn( siteConfigs );

        when( projectService.get( eq( ProjectName.from( "myproject" ) ) ) ).thenReturn( project );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( Branch.from( "draft" ) )
            .build()
            .callWith( () -> this.service.attachmentUrl( params ) );

        assertEquals( "https://cdn.company.com/_/media:attachment/myproject2/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png", url );
    }

    @Test
    public void createImageUrlUseCase10()
    {
        // Offline
        // Generate url for offline mode, for baseUrl `project` and `branch` are used from the Context
        // `project` and `branch` are provided in the params and used for media path
        // Nearest site is found by `baseUrlKey`
        // baseUrl found on site

        final AttachmentUrlParams params = new AttachmentUrlParams().offline( true )
            .type( UrlTypeConstants.ABSOLUTE )
            .projectName( "myproject2" )
            .branch( "master" )
            .baseUrlKey( "siteId" )
            .id( "123456" );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final PropertyTree config = new PropertyTree();
        config.addString( "baseUrl", "https://cdn.company.com" );

        final SiteConfigs siteConfigs = SiteConfigs.create()
            .add( SiteConfig.create().application( ApplicationKey.from( "com.enonic.xp.site" ) ).config( config ).build() )
            .build();

        final Site site = mock( Site.class );
        when( site.getSiteConfigs() ).thenReturn( siteConfigs );
        when( contentService.getNearestSite( eq( ContentId.from( "siteId" ) ) ) ).thenReturn( site );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( Branch.from( "draft" ) )
            .build()
            .callWith( () -> this.service.attachmentUrl( params ) );

        assertEquals( "https://cdn.company.com/_/media:attachment/myproject2/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png", url );
    }

    @Test
    public void createImageUrlUseCase11()
    {
        // Offline
        // Generate url for offline mode, for baseUrl `project` and `branch` are used from the Context
        // `project` and `branch` are provided in the params and used for media path
        // baseUrl does not resolved on site and project levels
        // fallback to /api

        final AttachmentUrlParams params = new AttachmentUrlParams().offline( true )
            .type( UrlTypeConstants.ABSOLUTE )
            .projectName( "myproject2" )
            .branch( "master" )
            .baseUrlKey( "siteId" )
            .id( "123456" );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final PropertyTree config = new PropertyTree();
        config.addString( "baseUrl", "https://cdn.company.com" );

        final Site site = mock( Site.class );
        when( site.getSiteConfigs() ).thenReturn( SiteConfigs.empty() );
        when( contentService.getNearestSite( eq( ContentId.from( "siteId" ) ) ) ).thenReturn( site );

        final Project project = mock( Project.class );
        when( project.getSiteConfigs() ).thenReturn( SiteConfigs.empty() );
        when( projectService.get( eq( ProjectName.from( "myproject" ) ) ) ).thenReturn( project );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .branch( Branch.from( "draft" ) )
            .build()
            .callWith( () -> this.service.attachmentUrl( params ) );

        assertEquals( "/api/media:attachment/myproject2/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png", url );
    }

    private Media mockMedia( String id, String name )
    {
        final Attachment attachment = Attachment.create()
            .name( name )
            .mimeType( "image/png" )
            .sha512( "ec25d6e4126c7064f82aaab8b34693fc" )
            .label( "source" )
            .build();

        final Media media = mock( Media.class );

        final ContentId contentId = ContentId.from( id );

        when( media.getId() ).thenReturn( contentId );
        when( media.getPath() ).thenReturn( ContentPath.from( "/" + id ) );
        when( media.getName() ).thenReturn( ContentName.from( name ) );
        when( media.getMediaAttachment() ).thenReturn( attachment );
        when( media.getAttachments() ).thenReturn( Attachments.from( attachment ) );

        return media;
    }
}
