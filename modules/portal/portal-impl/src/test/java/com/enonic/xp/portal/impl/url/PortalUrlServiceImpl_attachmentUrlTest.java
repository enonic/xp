package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.Test;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.site.Site;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PortalUrlServiceImpl_attachmentUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl_withoutNameAndLabel()
    {
        this.portalRequest.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().portalRequest( this.portalRequest ).param( "a", 3 );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/site/myproject/draft/a/b/mycontent/_/attachment/inline/123456:binaryHash2/a2.jpg?a=3", url );
    }

    @Test
    public void createUrl_withDownload()
    {
        this.portalRequest.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().portalRequest( this.portalRequest ).download( true );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/site/myproject/draft/a/b/mycontent/_/attachment/download/123456:binaryHash2/a2.jpg", url );
    }

    @Test
    public void createUrl_withName()
    {
        this.portalRequest.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().portalRequest( this.portalRequest ).name( "a1.jpg" );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/site/myproject/draft/a/b/mycontent/_/attachment/inline/123456:binaryHash1/a1.jpg", url );
    }

    @Test
    public void createUrl_withLabel()
    {
        this.portalRequest.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().portalRequest( this.portalRequest ).label( "thumb" );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/site/myproject/draft/a/b/mycontent/_/attachment/inline/123456:binaryHash1/a1.jpg", url );
    }

    @Test
    public void createUrl_withId()
    {
        createContent();

        final AttachmentUrlParams params = new AttachmentUrlParams().id( "123456" ).name( "a1.jpg" ).portalRequest( this.portalRequest );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/site/myproject/draft/context/path/_/attachment/inline/123456:binaryHash1/a1.jpg", url );
    }

    @Test
    public void createUrl_withPath()
    {
        createContent();

        final AttachmentUrlParams params =
            new AttachmentUrlParams().path( "/a/b/mycontent" ).name( "a1.jpg" ).portalRequest( this.portalRequest );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/site/myproject/draft/context/path/_/attachment/inline/123456:binaryHash1/a1.jpg", url );
    }

    @Test
    public void createUrl_absolute()
    {
        this.portalRequest.setContent( createContent() );

        final AttachmentUrlParams params =
            new AttachmentUrlParams().type( UrlTypeConstants.ABSOLUTE ).portalRequest( this.portalRequest ).param( "a", 3 );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "http://localhost/site/myproject/draft/a/b/mycontent/_/attachment/inline/123456:binaryHash2/a2.jpg?a=3", url );
    }

    @Test
    public void createAttachmentUrlForSlashApi()
    {
        this.portalRequest.setBaseUri( "" );
        this.portalRequest.setRawPath( "/api/com.enonic.app.appname" );
        this.portalRequest.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().id( "123456" )
            .type( UrlTypeConstants.ABSOLUTE )
            .name( "a2.jpg" )
            .portalRequest( this.portalRequest )
            .download( true );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 8080 );

        String url = this.service.attachmentUrl( params );
        assertEquals( "http://localhost:8080/api/media/attachment/myproject:draft/123456:binaryHash2/a2.jpg?download", url );
    }

    @Test
    public void createAttachmentUrlForSlashApiWithVhostContextConfig()
    {
        ContextAccessor.current().getLocalScope().setAttribute( "mediaService.baseUrl", "http://media.enonic.com" );

        this.portalRequest.setBaseUri( "" );
        this.portalRequest.setRawPath( "/api/com.enonic.app.appname" );
        this.portalRequest.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().id( "123456" )
            .type( UrlTypeConstants.ABSOLUTE )
            .name( "a2.jpg" )
            .portalRequest( this.portalRequest )
            .download( true );

        String url = this.service.attachmentUrl( params );
        assertEquals( "http://media.enonic.com/attachment/myproject:draft/123456:binaryHash2/a2.jpg?download", url );
    }

    @Test
    public void createAttachmentUrlForMasterBranch()
    {
        this.portalRequest.setBaseUri( "" );
        this.portalRequest.setRawPath( "/api/com.enonic.app.appname" );
        this.portalRequest.setBranch( ContentConstants.BRANCH_MASTER );
        this.portalRequest.setContent( createContent() );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 8080 );

        final AttachmentUrlParams params = new AttachmentUrlParams().id( "123456" )
            .type( UrlTypeConstants.ABSOLUTE )
            .name( "a2.jpg" )
            .portalRequest( this.portalRequest )
            .download( true );

        String url = this.service.attachmentUrl( params );
        assertEquals( "http://localhost:8080/api/media/attachment/myproject/123456:binaryHash2/a2.jpg?download", url );
    }

    @Test
    public void createAttachmentUrlWhenLegacyModeDisabledWithoutSite()
    {
        this.portalRequest.setBaseUri( "/site" );
        this.portalRequest.setRawPath( "/site/myproject/draft/a/b/mycontent" );
        this.portalRequest.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().id( "123456" )
            .type( UrlTypeConstants.ABSOLUTE )
            .name( "a1.jpg" )
            .portalRequest( this.portalRequest )
            .download( true );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 8080 );

        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.legacy_attachmentService_enabled() ).thenReturn( false );
        this.service.activate( portalConfig );

        // fallback to project, because a site is not provided
        final String url = this.service.attachmentUrl( params );
        assertEquals( "http://localhost:8080/site/myproject/draft/_/media/attachment/myproject:draft/123456:binaryHash1/a1.jpg?download", url );
    }

    @Test
    public void createAttachmentUrlWhenLegacyModeDisabledWithSite()
    {
        this.portalRequest.setBaseUri( "/site" );
        this.portalRequest.setRawPath( "/site/myproject/draft/a/b/mycontent" );
        this.portalRequest.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().id( "123456" )
            .type( UrlTypeConstants.ABSOLUTE )
            .name( "a1.jpg" )
            .portalRequest( this.portalRequest )
            .param( "a", 3 )
            .param( "b", 4 )
            .download( true );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 8080 );

        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.legacy_attachmentService_enabled() ).thenReturn( false );
        this.service.activate( portalConfig );

        final Site site = mock( Site.class );
        when( site.getPath() ).thenReturn( ContentPath.from( "/a/b" ) );
        when( site.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        when( contentService.getByPath(ContentPath.from( "/a/b" ) ) ).thenReturn( site );
        when( contentService.findNearestSiteByPath( ContentPath.from( "/a/b/mycontent" ) ) ).thenReturn( site );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "http://localhost:8080/site/myproject/draft/a/b/_/media/attachment/myproject:draft/123456:binaryHash1/a1.jpg?a=3&b=4&download", url );
    }

    @Test
    public void createAttachmentUrlWhenLegacyModeEnabled()
    {
        this.portalRequest.setBaseUri( "/site" );
        this.portalRequest.setRawPath( "/site/myproject/draft/a/b/mycontent" );
        this.portalRequest.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().id( "123456" )
            .type( UrlTypeConstants.ABSOLUTE )
            .name( "a1.jpg" )
            .portalRequest( this.portalRequest )
            .download( true );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 8080 );

        final PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.legacy_attachmentService_enabled() ).thenReturn( true );
        this.service.activate( portalConfig );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "http://localhost:8080/site/myproject/draft/a/b/mycontent/_/attachment/download/123456:binaryHash1/a1.jpg", url );
    }

    private Content createContent()
    {
        final Attachment a1 = Attachment.create().label( "thumb" ).name( "a1.jpg" ).mimeType( "image/jpeg" ).build();
        final Attachment a2 = Attachment.create().label( "source" ).name( "a2.jpg" ).mimeType( "image/jpeg" ).build();
        final Attachments attachments = Attachments.from( a1, a2 );

        final Content content = Content.create( ContentFixtures.newContent() ).attachments( attachments ).build();

        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        when( this.contentService.getById( content.getId() ) ).thenReturn( content );
        when( this.contentService.getBinaryKey( content.getId(), a1.getBinaryReference() ) ).thenReturn( "binaryHash1" );
        when( this.contentService.getBinaryKey( content.getId(), a2.getBinaryReference() ) ).thenReturn( "binaryHash2" );

        return content;
    }
}
