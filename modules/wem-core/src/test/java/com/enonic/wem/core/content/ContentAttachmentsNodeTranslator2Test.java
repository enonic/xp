package com.enonic.wem.core.content;

import org.junit.Test;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.data.PropertyTree;

import static org.junit.Assert.*;

public class ContentAttachmentsNodeTranslator2Test
{
    @Test
    public void translate()
        throws Exception
    {
        final Attachments attachments = Attachments.builder().
            add( Attachment.newAttachment().
                blobKey( new BlobKey( "abc" ) ).
                label( "source" ).
                mimeType( "text/plain" ).
                name( "my-attachment-1" ).
                size( 123l ).
                build() ).
            add( Attachment.newAttachment().
                blobKey( new BlobKey( "bcd" ) ).
                label( "large" ).
                mimeType( "image/png" ).
                name( "my-attachment-2" ).
                size( 234l ).
                build() ).
            build();

        final PropertyTree propertyTree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        final PropertySet attachmentProperties = ContentAttachmentsNodeTranslator2.translate( propertyTree, attachments );

        assertNotNull( attachmentProperties.getBinary( "my-attachment-1.blob" ) );
        assertEquals( "source", attachmentProperties.getString( "my-attachment-1.label" ) );
        assertEquals( "text/plain", attachmentProperties.getString( "my-attachment-1.mimeType" ) );
        assertEquals( new Long( 123 ), attachmentProperties.getLong( "my-attachment-1.size" ) );

        assertNotNull( attachmentProperties.getBinary( "my-attachment-2.blob" ) );
        assertEquals( "large", attachmentProperties.getString( "my-attachment-2.label" ) );
        assertEquals( "image/png", attachmentProperties.getString( "my-attachment-2.mimeType" ) );
        assertEquals( new Long( 234 ), attachmentProperties.getLong( "my-attachment-2.size" ) );
    }
}