package com.enonic.wem.core.content;

import org.junit.Test;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;

import static org.junit.Assert.*;

public class ContentAttachmentNodeTranslatorTest
{
    final ContentAttachmentNodeTranslator contentAttachmentNodeTranslator = new ContentAttachmentNodeTranslator();

    @Test
    public void translate()
        throws Exception
    {
        final String name = "myAttachment";
        final BlobKey blobKey = new BlobKey( "test" );
        final String mimeType = "text/plain";
        final long size = 10L;
        final Attachments contentAttachments = Attachments.from( Attachment.newAttachment().
            name( name ).
            blobKey( blobKey ).
            mimeType( mimeType ).
            size( size ).
            build() );

        final com.enonic.wem.api.entity.Attachments attachments = contentAttachmentNodeTranslator.toNodeAttachments( contentAttachments );

        assertEquals( 1, attachments.getSize() );

        final com.enonic.wem.api.entity.Attachment attachment = attachments.get( 0 );

        assertEquals( name, attachment.name() );
        assertEquals( size, attachment.size() );
        assertEquals( mimeType, attachment.mimeType() );
        assertEquals( blobKey, attachment.blobKey() );
    }

    @Test
    public void translate_null_then_null()
    {
        final com.enonic.wem.api.entity.Attachments attachments = contentAttachmentNodeTranslator.toNodeAttachments( null );

        assertTrue( attachments == null );
    }

    @Test
    public void translate_empty_then_empty()
    {
        final com.enonic.wem.api.entity.Attachments attachments = contentAttachmentNodeTranslator.toNodeAttachments( Attachments.empty() );

        assertEquals( com.enonic.wem.api.entity.Attachments.empty(), attachments );
    }

}
