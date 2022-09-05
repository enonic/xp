package com.enonic.xp.attachment;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateAttachmentsParamsTest
{

    @Test
    public void fromBuilder()
    {
        ContentId id = ContentId.from( "id-1" );

        Attachment attachment = Attachment.create().
            mimeType( "image/jpeg" ).
            name( "MyImage.jpg" ).
            build();

        UpdateAttachmentsParams params = UpdateAttachmentsParams.create( id ).
            addAttachments( attachment ).build();

        assertEquals( id, params.getContentId() );
        assertEquals( 1, params.getAttachments().getSize() );
        assertEquals( attachment, params.getAttachments().first() );
    }

}
