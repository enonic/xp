package com.enonic.xp.portal.impl.url;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.core.content.Content;
import com.enonic.xp.core.content.attachment.Attachment;
import com.enonic.xp.core.content.attachment.Attachments;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.url.AttachmentUrlParams;

import static org.junit.Assert.*;

public class PortalUrlServiceImpl_attachmentUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl_withoutNameAndLabel()
    {
        this.context.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().
            context( this.context ).
            param( "a", 3 );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/portal/draft/a/b/mycontent/_/attachment/inline/123456/a2.jpg?a=3", url );
    }

    @Test
    public void createUrl_withDownload()
    {
        this.context.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().
            context( this.context ).
            download( true );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/portal/draft/a/b/mycontent/_/attachment/download/123456/a2.jpg", url );
    }

    @Test
    public void createUrl_withName()
    {
        this.context.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().
            context( this.context ).
            name( "myfile.pdf" );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/portal/draft/a/b/mycontent/_/attachment/inline/123456/myfile.pdf", url );
    }

    @Test
    public void createUrl_withLabel()
    {
        this.context.setContent( createContent() );

        final AttachmentUrlParams params = new AttachmentUrlParams().
            context( this.context ).
            label( "thumb" );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/portal/draft/a/b/mycontent/_/attachment/inline/123456/a1.jpg", url );
    }

    @Test
    public void createUrl_withId()
    {
        createContent();

        final AttachmentUrlParams params = new AttachmentUrlParams().
            id( "123456" ).
            name( "myfile.pdf" ).
            context( this.context );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/portal/draft/context/path/_/attachment/inline/123456/myfile.pdf", url );
    }

    @Test
    public void createUrl_withPath()
    {
        createContent();

        final AttachmentUrlParams params = new AttachmentUrlParams().
            path( "/a/b/mycontent" ).
            name( "myfile.pdf" ).
            context( this.context );

        final String url = this.service.attachmentUrl( params );
        assertEquals( "/portal/draft/context/path/_/attachment/inline/123456/myfile.pdf", url );
    }

    private Content createContent()
    {
        final Attachment a1 = Attachment.newAttachment().label( "thumb" ).name( "a1.jpg" ).mimeType( "image/jpg" ).build();
        final Attachment a2 = Attachment.newAttachment().label( "source" ).name( "a2.jpg" ).mimeType( "image/jpg" ).build();
        final Attachments attachments = Attachments.from( a1, a2 );

        final Content content = Content.newContent( ContentFixtures.newContent() ).
            attachments( attachments ).
            build();

        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );
        return content;
    }
}
