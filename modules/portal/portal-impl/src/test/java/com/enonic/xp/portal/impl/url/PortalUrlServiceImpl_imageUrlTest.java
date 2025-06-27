package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.attachment.Attachment;
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
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.web.vhost.VirtualHost;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PortalUrlServiceImpl_imageUrlTest
{
    private ContentService contentService;

    private PortalUrlService service;

    private PortalRequest portalRequest;

    private HttpServletRequest req;

    private PortalUrlGeneratorService portalUrlGeneratorService;

    @BeforeEach
    public void setUp()
    {
        this.contentService = mock( ContentService.class );

        portalUrlGeneratorService = new PortalUrlGeneratorServiceImpl();

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

        assertEquals( "baseUrl/_/media:image/context-project:context-branch/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png",
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

        assertEquals( "/api/media:image/context-project:context-branch/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png",
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

        assertEquals( "/api/media:image/explicit-project:explicit-branch/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png",
                      url );
    }

    @Test
    void testWithNoSiteRequestInContextWithVirtualHost()
    {
        portalRequest.setBaseUri( "/api/app:api" );
        portalRequest.setRepositoryId( null );
        portalRequest.setBranch( null );
        portalRequest.setRawPath( "/api/app:api/path" );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/source" );
        when( virtualHost.getTarget() ).thenReturn( "/api/media:image" );
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
            "http://localhost/source/context-project:context-branch/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png", url );
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

        final ImageUrlParams params = new ImageUrlParams().id( "123456" ).scale( "max(300)" ).baseUrl( "baseUrl" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertEquals( "baseUrl/_/media:image/context-project:context-branch/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png",
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

        final ImageUrlParams params = new ImageUrlParams().type( UrlTypeConstants.ABSOLUTE ).id( "123456" ).scale( "max(300)" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertEquals(
            "http://localhost/api/media:image/context-project:context-branch/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png",
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

        final ImageUrlParams params = new ImageUrlParams().id( "123456" ).scale( "max(300)" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertEquals(
            "/webapp/myapp/_/media:image/context-project:context-branch/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png",
            url );
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

        final ImageUrlParams params = new ImageUrlParams().id( "123456" ).scale( "max(300)" );

        final String url = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "com.enonic.cms.context-project" ) )
            .branch( Branch.from( "context-branch" ) )
            .build()
            .callWith( () -> this.service.imageUrl( params ) );

        assertEquals( "/source/_/media:image/context-project:context-branch/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png",
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

        assertEquals( "/api/media:image/context-project:context-branch/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png",
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
            "/site/request-project/request-branch/mysite/_/media:image/request-project:request-branch/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png",
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
            "/site/request-project/request-branch/mysite/_/media:image/request-project:request-branch/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png",
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
        portalRequest.setSite( site );

        final ImageUrlParams params = new ImageUrlParams().type( UrlTypeConstants.ABSOLUTE ).scale( "max(300)" );

        final String url = this.service.imageUrl( params );

        assertEquals(
            "http://localhost/source/_/media:image/request-project:request-branch/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png",
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
            "/site/request-project/request-branch/mysite/_/media:image/explicit-project:explicit-branch/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png",
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

        assertEquals( "baseUrl/_/media:image/context-project:context-branch/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png",
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

        assertEquals( "baseUrl/_/media:image/project:branch/123456:b12b4c973748042e3b3a7e4798344289/max-300/mycontent.png", url );
    }

    private Media mockMedia( String id, String name, String attachmentHash )
    {
        final Attachment attachment = Attachment.create().name( name ).mimeType( "image/png" ).sha512( attachmentHash ).build();

        final Media media = mock( Media.class );

        final ContentId contentId = ContentId.from( id );

        when( media.getId() ).thenReturn( contentId );
        when( media.getPath() ).thenReturn( ContentPath.from( "/" + id ) );
        when( media.getName() ).thenReturn( ContentName.from( name ) );
        when( media.getMediaAttachment() ).thenReturn( attachment );

        return media;
    }

    private Media mockMedia( String id, String name )
    {
        return mockMedia( id, name, "ec25d6e4126c7064f82aaab8b34693fc" );
    }
}
