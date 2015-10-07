package com.enonic.xp.portal.impl.url;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

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
        assertEquals( "/portal/draft/a/b/mycontent/_/attachment/inline/123456:binaryHash2/a2.jpg?a=3", url );
    }

    @Test
    public void createUrl_withDownload()
    {
        this.portalRequest.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().
            portalRequest( this.portalRequest ).
            download( true );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/portal/draft/a/b/mycontent/_/attachment/download/123456:binaryHash2/a2.jpg", url );
    }

    @Test
    public void createUrl_withName()
    {
        this.portalRequest.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().
            portalRequest( this.portalRequest ).
            name( "a1.jpg" );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/portal/draft/a/b/mycontent/_/attachment/inline/123456:binaryHash1/a1.jpg", url );
    }

    @Test
    public void createUrl_withLabel()
    {
        this.portalRequest.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().
            portalRequest( this.portalRequest ).
            label( "thumb" );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/portal/draft/a/b/mycontent/_/attachment/inline/123456:binaryHash1/a1.jpg", url );
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
        assertEquals( "/portal/draft/context/path/_/attachment/inline/123456:binaryHash1/a1.jpg", url );
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
        assertEquals( "/portal/draft/context/path/_/attachment/inline/123456:binaryHash1/a1.jpg", url );
    }

    @Test
    public void createUrl_absolute()
    {
        this.portalRequest.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().
            type( UrlTypeConstants.ABSOLUTE ).
            portalRequest( this.portalRequest ).
            param( "a", 3 );

        MockHttpServletRequest req = new MockHttpServletRequest();
        ServletRequestHolder.setRequest( req );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "http://localhost/portal/draft/a/b/mycontent/_/attachment/inline/123456:binaryHash2/a2.jpg?a=3", url );
    }

    private Content createContent()
    {
        final Attachment a1 = Attachment.create().label( "thumb" ).name( "a1.jpg" ).mimeType( "image/jpg" ).build();
        final Attachment a2 = Attachment.create().label( "source" ).name( "a2.jpg" ).mimeType( "image/jpg" ).build();
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
