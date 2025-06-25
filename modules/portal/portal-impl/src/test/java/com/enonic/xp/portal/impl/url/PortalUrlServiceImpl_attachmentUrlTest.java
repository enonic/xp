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
import com.enonic.xp.macro.MacroService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.RedirectChecksumService;
import com.enonic.xp.portal.url.AttachmentUrlGeneratorParams;
import com.enonic.xp.portal.url.AttachmentUrlParams;
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
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.web.vhost.VirtualHost;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PortalUrlServiceImpl_attachmentUrlTest
{
    private ContentService contentService;

    private PortalUrlService service;

    private PortalUrlGeneratorService portalUrlGeneratorService;

    private PortalRequest portalRequest;

    private HttpServletRequest req;

    @BeforeEach
    public void setUp()
    {
        this.contentService = mock( ContentService.class );

        this.portalUrlGeneratorService = new PortalUrlGeneratorServiceImpl( contentService, mock( ProjectService.class ) );

        this.service = new PortalUrlServiceImpl( this.contentService, mock( ResourceService.class ), mock( MacroService.class ),
                                                 mock( StyleDescriptorService.class ), mock( RedirectChecksumService.class ),
                                                 mock( ProjectService.class ), portalUrlGeneratorService );

        req = mock( HttpServletRequest.class );

        portalRequest = new PortalRequest();
        portalRequest.setRawRequest( req );

        PortalRequestAccessor.set( portalRequest );
    }

    @AfterEach
    public void destroy()
    {
        PortalRequestAccessor.remove();
    }

    @Test
    void testNoRequestAndWithoutContext()
    {
        PortalRequestAccessor.set( null );

        final AttachmentUrlParams params = new AttachmentUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" );

        final String url = ContextBuilder.create().build().callWith( () -> this.service.attachmentUrl( params ) );

        assertThat( url ).startsWith( "/_/error/500?message=Something+went+wrong." );
    }

    @Test
    void testNoRequestAndWithoutBranchInContext()
    {
        PortalRequestAccessor.set( null );

        final AttachmentUrlParams params = new AttachmentUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" );

        final String url = ContextBuilder.create()
            .repositoryId( "com.enonic.cms.context-repo" )
            .build()
            .callWith( () -> this.service.attachmentUrl( params ) );

        assertThat( url ).startsWith( "/_/error/500?message=Something+went+wrong." );
    }

    @Test
    void testNoRequestAndWithBaseUrl()
    {
        PortalRequestAccessor.set( null );

        final AttachmentUrlParams params = new AttachmentUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" ).baseUrl( "baseUrl" );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final String url = ContextBuilder.create()
            .repositoryId( "com.enonic.cms.context-project" )
            .branch( "context-branch" )
            .build()
            .callWith( () -> this.service.attachmentUrl( params ) );

        assertEquals( "baseUrl/_/media:attachment/context-project:context-branch/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png",
                      url );
    }

    @Test
    void testNoRequestAndWithoutBaseUrl()
    {
        PortalRequestAccessor.set( null );

        final AttachmentUrlParams params = new AttachmentUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final String url = ContextBuilder.create()
            .repositoryId( "com.enonic.cms.context-project" )
            .branch( "context-branch" )
            .build()
            .callWith( () -> this.service.attachmentUrl( params ) );

        assertEquals( "/api/media:attachment/context-project:context-branch/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png", url );
    }

    @Test
    void testNoRequestAndWithBaseUrlButWithoutIdAndPath()
    {
        PortalRequestAccessor.set( null );

        final AttachmentUrlParams params = new AttachmentUrlParams().baseUrl( "baseUrl" );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final String url = ContextBuilder.create()
            .repositoryId( "com.enonic.cms.context-project" )
            .branch( "context-branch" )
            .build()
            .callWith( () -> this.service.attachmentUrl( params ) );

        assertThat( url ).startsWith( "/_/error/500?message=Something+went+wrong." );
    }

    @Test
    void testNoRequestAndContentNotFoundById()
    {
        PortalRequestAccessor.set( null );

        final AttachmentUrlParams params = new AttachmentUrlParams().id( "123456" );

        when( contentService.getById( any( ContentId.class ) ) ).thenReturn( null );

        final String url = ContextBuilder.create()
            .repositoryId( "com.enonic.cms.context-project" )
            .branch( "context-branch" )
            .build()
            .callWith( () -> this.service.attachmentUrl( params ) );

        assertThat( url ).startsWith( "/_/error/404?message=Not+Found." );
    }

    @Test
    void testNoRequestAndContentNotFoundByPath()
    {
        PortalRequestAccessor.set( null );

        final AttachmentUrlParams params = new AttachmentUrlParams().path( "/path" );

        when( contentService.getByPath( any( ContentPath.class ) ) ).thenReturn( null );

        final String url = ContextBuilder.create()
            .repositoryId( "com.enonic.cms.context-project" )
            .branch( "context-branch" )
            .build()
            .callWith( () -> this.service.attachmentUrl( params ) );

        assertThat( url ).startsWith( "/_/error/404?message=Not+Found." );
    }

    @Test
    void testNoRequestAndWithoutContextAndWithExplicitProjectAndBranch()
    {
        PortalRequestAccessor.set( null );

        final AttachmentUrlParams params = new AttachmentUrlParams().type( UrlTypeConstants.ABSOLUTE )
            .id( "123456" )
            .projectName( "explicit-project" )
            .branch( "explicit-branch" );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final String url = this.service.attachmentUrl( params );

        assertEquals( "/api/media:attachment/explicit-project:explicit-branch/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png", url );
    }

    @Test
    void testWithNoSiteRequestInContextWithVirtualHost()
    {
        portalRequest.setBaseUri( "/api/app:api" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/api/app:api" );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/source" );
        when( virtualHost.getTarget() ).thenReturn( "/api/media:attachment" );
        when( portalRequest.getRawRequest().getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final AttachmentUrlParams params = new AttachmentUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.attachmentUrl( params ) );

        assertEquals( "http://localhost/source/context-project:context-branch/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png", url );
    }

    @Test
    void testWithNoSiteRequestWithBaseUrlIgnoreRewrite()
    {
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

        final AttachmentUrlParams params = new AttachmentUrlParams().id( "123456" ).baseUrl( "baseUrl" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.attachmentUrl( params ) );

        assertEquals( "baseUrl/_/media:attachment/context-project:context-branch/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png",
                      url );
    }

    @Test
    void testWithNoSiteRequestInContextWithDefaultVirtualHost()
    {
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

        final AttachmentUrlParams params = new AttachmentUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.attachmentUrl( params ) );

        assertEquals(
            "http://localhost/api/media:attachment/context-project:context-branch/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png",
            url );
    }

    @Test
    void testWithNoSiteRequestNonApiBaseUri()
    {
        portalRequest.setBaseUri( "/webapp/myapp" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/webapp/myapp/path" );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final AttachmentUrlParams params = new AttachmentUrlParams().id( "123456" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.attachmentUrl( params ) );

        assertEquals(
            "/webapp/myapp/_/media:attachment/context-project:context-branch/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png", url );
    }

    @Test
    void testWithNoSiteRequestNonApiBaseUriWithVirtualHost()
    {
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

        final AttachmentUrlParams params = new AttachmentUrlParams().id( "123456" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.attachmentUrl( params ) );

        assertEquals( "/source/_/media:attachment/context-project:context-branch/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png",
                      url );
    }

    @Test
    void testWithNoSiteRequestWithEmptyBaseUriWithIgnoringRewrite()
    {
        portalRequest.setBaseUri( "" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/path/sub-path" );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/source" );
        when( virtualHost.getTarget() ).thenReturn( "/api/media:image" );
        when( portalRequest.getRawRequest().getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final Media media = mockMedia( "123456", "mycontent.png" );
        when( contentService.getById( eq( media.getId() ) ) ).thenReturn( media );

        final AttachmentUrlParams params = new AttachmentUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.attachmentUrl( params ) );

        assertEquals( "/api/media:attachment/context-project:context-branch/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png", url );
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

        when( contentService.findNearestSiteByPath( eq( media.getPath() ) ) ).thenReturn( site );

        final AttachmentUrlParams params = new AttachmentUrlParams();

        final String url = this.service.attachmentUrl( params );

        assertEquals(
            "/site/request-project/request-branch/mysite/_/media:attachment/request-project:request-branch/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png",
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

        when( contentService.findNearestSiteByPath( eq( media.getPath() ) ) ).thenReturn( site );

        final AttachmentUrlParams params = new AttachmentUrlParams();

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.attachmentUrl( params ) );

        assertEquals(
            "/site/request-project/request-branch/mysite/_/media:attachment/request-project:request-branch/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png",
            url );
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

        when( contentService.findNearestSiteByPath( eq( media.getPath() ) ) ).thenReturn( site );

        final AttachmentUrlParams params = new AttachmentUrlParams().type( UrlTypeConstants.ABSOLUTE );

        final String url = this.service.attachmentUrl( params );

        assertEquals(
            "http://localhost/source/_/media:attachment/request-project:request-branch/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png",
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

        when( contentService.findNearestSiteByPath( eq( media.getPath() ) ) ).thenReturn( site );

        final AttachmentUrlParams params = new AttachmentUrlParams().type( UrlTypeConstants.SERVER_RELATIVE )

            .projectName( "explicit-project" ).branch( "explicit-branch" );

        final String url = this.service.attachmentUrl( params );

        assertEquals(
            "/site/request-project/request-branch/mysite/_/media:attachment/explicit-project:explicit-branch/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png",
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

        when( contentService.findNearestSiteByPath( eq( media.getPath() ) ) ).thenReturn( site );

        final AttachmentUrlParams params = new AttachmentUrlParams().id( "123456" ).baseUrl( "baseUrl" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.attachmentUrl( params ) );

        assertEquals( "baseUrl/_/media:attachment/context-project:context-branch/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png",
                      url );
    }

    @Test
    void testAttachmentUrl()
    {
        AttachmentUrlGeneratorParams params = AttachmentUrlGeneratorParams.create()
            .setBaseUrl( "baseUrl" )
            .setContent( () -> mockMedia( "123456", "mycontent.png" ) )
            .setProjectName( () -> ProjectName.from( "project" ) )
            .setBranch( () -> Branch.from( "branch" ) )
            .setDownload( true )
            .build();

        final String url = this.portalUrlGeneratorService.attachmentUrl( params );

        assertEquals( "baseUrl/_/media:attachment/project:branch/123456:ec25d6e4126c7064f82aaab8b34693fc/mycontent.png?download", url );
    }

    private Media mockMedia( String id, String name )
    {
        final Attachment attachment =
            Attachment.create().name( name ).mimeType( "image/png" ).sha512( "ec25d6e4126c7064f82aaab8b34693fc" ).label( "source" ).build();

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
