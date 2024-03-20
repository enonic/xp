package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class PortalUrlServiceImpl_attachmentUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl_withoutNameAndLabel()
    {
        this.portalRequest.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().
            portalRequest( this.portalRequest ).
            param( "a", 3 );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/site/myproject/draft/a/b/mycontent/_/attachment/inline/123456:binaryHash2/a2.jpg?a=3", url );
    }

    @Test
    public void createUrl_withDownload()
    {
        this.portalRequest.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().
            portalRequest( this.portalRequest ).
            download( true );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/site/myproject/draft/a/b/mycontent/_/attachment/download/123456:binaryHash2/a2.jpg", url );
    }

    @Test
    public void createUrl_withName()
    {
        this.portalRequest.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().
            portalRequest( this.portalRequest ).
            name( "a1.jpg" );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/site/myproject/draft/a/b/mycontent/_/attachment/inline/123456:binaryHash1/a1.jpg", url );
    }

    @Test
    public void createUrl_withLabel()
    {
        this.portalRequest.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().
            portalRequest( this.portalRequest ).
            label( "thumb" );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/site/myproject/draft/a/b/mycontent/_/attachment/inline/123456:binaryHash1/a1.jpg", url );
    }

    @Test
    public void createUrl_withId()
    {
        createContent();

        final AttachmentUrlParams params = new AttachmentUrlParams().
            id( "123456" ).
            name( "a1.jpg" ).
            portalRequest( this.portalRequest );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/site/myproject/draft/context/path/_/attachment/inline/123456:binaryHash1/a1.jpg", url );
    }

    @Test
    public void createUrl_withPath()
    {
        createContent();

        final AttachmentUrlParams params = new AttachmentUrlParams().
            path( "/a/b/mycontent" ).
            name( "a1.jpg" ).
            portalRequest( this.portalRequest );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/site/myproject/draft/context/path/_/attachment/inline/123456:binaryHash1/a1.jpg", url );
    }

    @Test
    public void createUrl_absolute()
    {
        this.portalRequest.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().
            type( UrlTypeConstants.ABSOLUTE ).
            portalRequest( this.portalRequest ).
            param( "a", 3 );

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
        assertEquals( "http://localhost:8080/api/media/attachment/myproject/draft/123456:binaryHash2/a2.jpg?download", url );
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
        assertEquals( "http://media.enonic.com/attachment/myproject/draft/123456:binaryHash2/a2.jpg?download", url );
    }

    private Content createContent()
    {
        final Attachment a1 = Attachment.create().label( "thumb" ).name( "a1.jpg" ).mimeType( "image/jpeg" ).build();
        final Attachment a2 = Attachment.create().label( "source" ).name( "a2.jpg" ).mimeType( "image/jpeg" ).build();
        final Attachments attachments = Attachments.from( a1, a2 );

        final Content content = Content.create( ContentFixtures.newContent() ).
            attachments( attachments ).
            build();

        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );
        Mockito.when( this.contentService.getBinaryKey( content.getId(), a1.getBinaryReference() ) ).thenReturn( "binaryHash1" );
        Mockito.when( this.contentService.getBinaryKey( content.getId(), a2.getBinaryReference() ) ).thenReturn( "binaryHash2" );
        return content;
    }
}
