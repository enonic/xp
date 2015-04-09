package com.enonic.xp.content.attachment;

import org.junit.Test;

import com.enonic.xp.content.ContentId;

import static org.junit.Assert.*;

public class UpdateAttachmentsParamsTest
{

    @Test
    public void fromBuilder()
    {
        ContentId id = ContentId.from( "id-1" );

        Attachment attachment = Attachment.newAttachment().
            mimeType( "image/jpg" ).
            name( "MyImage.jpg" ).
            build();

        UpdateAttachmentsParams params = UpdateAttachmentsParams.newUpdateAttachments( id ).
            addAttachments( attachment ).build();

        assertEquals( id, params.getContentId() );
        assertEquals( 1, params.getAttachments().getSize() );
        assertEquals( attachment, params.getAttachments().first() );
    }

}