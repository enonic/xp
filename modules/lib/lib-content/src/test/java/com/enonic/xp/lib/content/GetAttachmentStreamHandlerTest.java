package com.enonic.xp.lib.content;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;

class GetAttachmentStreamHandlerTest
    extends BaseContentHandlerTest
{
    private static final byte[] ATTACHMENT_DATA = "data".getBytes( StandardCharsets.UTF_8 );

    @Test
    void testExample()
    {
        mockAttachmentBinary();
        runScript( "/lib/xp/examples/content/getAttachmentStream.js" );
    }

    private void mockAttachmentBinary()
    {
        final Content content = TestDataFixtures.newContent();
        final Attachment attachment = content.getAttachments().byName( "document.pdf" );
        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        Mockito.when( this.contentService.getBinary( content.getId(), attachment.getBinaryReference() ) ).thenReturn(
            ByteSource.wrap( ATTACHMENT_DATA ) );
    }

    @Test
    void getById()
    {
        mockAttachmentBinary();
        runFunction( "/test/GetAttachmentStreamHandlerTest.js", "getAttachmentStreamById" );
    }

    @Test
    void getByPath()
    {
        mockAttachmentBinary();
        runFunction( "/test/GetAttachmentStreamHandlerTest.js", "getAttachmentStreamByPath" );
    }

    @Test
    void getById_notFound()
    {
        final ContentId id = ContentId.from( "123456" );
        Mockito.when( this.contentService.getById( Mockito.any() ) )
            .thenThrow( ContentNotFoundException.class );


        runFunction( "/test/GetAttachmentStreamHandlerTest.js", "getAttachmentStreamById_notFound" );
    }

    @Test
    void getByPath_notFound()
    {
        final ContentPath path = ContentPath.from( "/a/b/mycontent" );
        Mockito.when( this.contentService.getByPath( path ) ).thenThrow( ContentNotFoundException.class );

        runFunction( "/test/GetAttachmentStreamHandlerTest.js", "getAttachmentStreamByPath_notFound" );
    }

    @Test
    void getById_AttachmentNotFound()
    {
        final Content content = TestDataFixtures.newContent();
        final Attachment attachment = content.getAttachments().byName( "document.pdf" );

        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );
        Mockito.when( this.contentService.getBinary( content.getId(), attachment.getBinaryReference() ) ).thenReturn(
            ByteSource.wrap( ATTACHMENT_DATA ) );

        runFunction( "/test/GetAttachmentStreamHandlerTest.js", "getAttachmentStreamById_AttachmentNotFound" );
    }
}
