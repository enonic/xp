package com.enonic.xp.attachment;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateAttachmentTest
{
    @Test
    void getNameWithoutExtension()
    {

        assertEquals( "MyImage", CreateAttachment.create().
            mimeType( "image/jpeg" ).
            label( "My Image 1" ).
            name( "MyImage.jpg" ).
            byteSource( ByteSource.empty() ).
            build().getNameWithoutExtension() );

        assertEquals( "MyImage.something", CreateAttachment.create().
            mimeType( "image/gif" ).
            label( "My Image 2" ).
            name( "MyImage.something.gif" ).
            byteSource( ByteSource.empty() ).
            build().getNameWithoutExtension() );
    }

    @Test
    void getExtension()
    {
        assertEquals( "jpg", CreateAttachment.create().
            name( "MyImage.jpg" ).
            byteSource( ByteSource.empty() ).
            build().getExtension() );

        assertEquals( "gif", CreateAttachment.create().
            byteSource( ByteSource.empty() ).
            name( "MyImage.gif" ).
            build().getExtension() );

        assertEquals( "jpeg", CreateAttachment.create().
            byteSource( ByteSource.empty() ).
            name( "MyImage.jpeg" ).
            build().getExtension() );

        assertEquals( "png", CreateAttachment.create().
            byteSource( ByteSource.empty() ).
            name( "MyImage.png" ).
            build().getExtension() );

        assertEquals( "jpg", CreateAttachment.create().
            byteSource( ByteSource.empty() ).
            name( "MyImage.something.jpg" ).
            build().getExtension() );
    }

    @Test
    void getBinaryReference()
    {
        assertEquals( "MyImage.jpg", CreateAttachment.create().
            byteSource( ByteSource.empty() ).
            name( "MyImage.jpg" ).
            build().getBinaryReference().toString() );

        assertEquals( "MyImage.something.gif", CreateAttachment.create().
            byteSource( ByteSource.empty() ).
            name( "MyImage.something.gif" ).
            build().getBinaryReference().toString() );
    }
}
