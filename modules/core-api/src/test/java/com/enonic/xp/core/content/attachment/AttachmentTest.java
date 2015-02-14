package com.enonic.xp.core.content.attachment;

import org.junit.Test;

import com.enonic.xp.core.content.attachment.Attachment;

import static org.junit.Assert.*;

public class AttachmentTest
{
    @Test
    public void getNameWithoutExtension()
    {
        assertEquals( "MyImage", Attachment.newAttachment().
            mimeType( "image/jpg" ).
            name( "MyImage.jpg" ).
            build().getNameWithoutExtension() );

        assertEquals( "MyImage.something", Attachment.newAttachment().
            mimeType( "image/jpg" ).
            name( "MyImage.something.jpg" ).
            build().getNameWithoutExtension() );
    }

    @Test
    public void getExtension()
    {
        assertEquals( "jpg", Attachment.newAttachment().
            mimeType( "image/jpg" ).
            name( "MyImage.jpg" ).
            build().getExtension() );

        assertEquals( "gif", Attachment.newAttachment().
            mimeType( "image/gif" ).
            name( "MyImage.gif" ).
            build().getExtension() );

        assertEquals( "jpeg", Attachment.newAttachment().
            mimeType( "image/jpeg" ).
            name( "MyImage.jpeg" ).
            build().getExtension() );

        assertEquals( "png", Attachment.newAttachment().
            mimeType( "image/png" ).
            name( "MyImage.png" ).
            build().getExtension() );

        assertEquals( "jpg", Attachment.newAttachment().
            mimeType( "image/jpg" ).
            name( "MyImage.something.jpg" ).
            build().getExtension() );
    }

}