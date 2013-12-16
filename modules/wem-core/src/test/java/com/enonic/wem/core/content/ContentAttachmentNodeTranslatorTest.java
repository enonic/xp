package com.enonic.wem.core.content;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.entity.Attachments;

import static org.junit.Assert.*;

public class ContentAttachmentNodeTranslatorTest
{
    final ContentAttachmentNodeTranslator contentAttachmentNodeTranslator = new ContentAttachmentNodeTranslator();

    @Test
    public void translate()
        throws Exception
    {

        final Set<Attachment> contentAttachments = Sets.newHashSet();

        final String name = "myAttachment";
        final BlobKey blobKey = new BlobKey( "test" );
        final String mimeType = "text/plain";
        final long size = 10L;
        contentAttachments.add( Attachment.newAttachment().
            name( name ).
            blobKey( blobKey ).
            mimeType( mimeType ).
            size( size ).
            build() );

        final Attachments attachments = contentAttachmentNodeTranslator.toNodeAttachments( contentAttachments );

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
        final Attachments attachments = contentAttachmentNodeTranslator.toNodeAttachments( null );

        assertTrue( attachments == null );
    }

    @Test
    public void translate_empty_then_empty()
    {
        final Attachments attachments = contentAttachmentNodeTranslator.toNodeAttachments( new HashSet<Attachment>() );

        assertEquals( Attachments.empty(), attachments );
    }

}
