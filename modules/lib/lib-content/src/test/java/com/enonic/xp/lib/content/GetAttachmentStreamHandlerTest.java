package com.enonic.xp.lib.content;

import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;

public class GetAttachmentStreamHandlerTest
    extends BaseContentHandlerTest
{
    private static final byte[] ATTACHMENT_DATA = "data".getBytes( StandardCharsets.UTF_8 );

    @Test
    public void testExample()
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
    public void getById()
        throws Exception
    {
        mockAttachmentBinary();
        runFunction( "/test/GetAttachmentStreamHandlerTest.js", "getAttachmentStreamById" );
    }

    @Test
    public void getByPath()
        throws Exception
    {
        mockAttachmentBinary();
        runFunction( "/test/GetAttachmentStreamHandlerTest.js", "getAttachmentStreamByPath" );
    }

    @Test
    public void getById_notFound()
        throws Exception
    {
        final ContentId id = ContentId.from( "123456" );
        Mockito.when( this.contentService.getById( id ) ).thenThrow( new ContentNotFoundException( id, null ) );

        runFunction( "/test/GetAttachmentStreamHandlerTest.js", "getAttachmentStreamById_notFound" );
    }

    @Test
    public void getByPath_notFound()
        throws Exception
    {
        final ContentPath path = ContentPath.from( "/a/b/mycontent" );
        Mockito.when( this.contentService.getByPath( path ) ).thenThrow( new ContentNotFoundException( path, null ) );

        runFunction( "/test/GetAttachmentStreamHandlerTest.js", "getAttachmentStreamByPath_notFound" );
    }

    @Test
    public void getById_AttachmentNotFound()
        throws Exception
    {
        final Content content = TestDataFixtures.newContent();
        final Attachment attachment = content.getAttachments().byName( "document.pdf" );

        Mockito.when( this.contentService.getById( content.getId() ) ).thenReturn( content );
        Mockito.when( this.contentService.getBinary( content.getId(), attachment.getBinaryReference() ) ).thenReturn(
            ByteSource.wrap( ATTACHMENT_DATA ) );

        runFunction( "/test/GetAttachmentStreamHandlerTest.js", "getAttachmentStreamById_AttachmentNotFound" );
    }
}
