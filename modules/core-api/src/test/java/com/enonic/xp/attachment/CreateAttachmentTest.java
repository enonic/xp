package com.enonic.xp.attachment;

import org.junit.Test;

import com.google.common.io.ByteSource;

import static org.junit.Assert.*;

public class CreateAttachmentTest
{
    @Test
    public void getNameWithoutExtension()
    {

        assertEquals( "MyImage", CreateAttachment.create().
            mimeType( "image/jpg" ).
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
    public void getExtension()
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
    public void getBinaryReference()
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

    @Test
    public void serializeAttachment()
    {
        Attachment a1 = Attachment.newAttachment().
            mimeType( "image/jpg" ).
            size( 1024 ).
            label( "My Image 1" ).
            name( "MyImage.jpg" ).
            build();

        assertEquals( "Attachment{name=MyImage.jpg, mimeType=image/jpg, label=My Image 1, size=1024}", a1.toString() );

    }

    @Test
    public void compareAttachments()
    {
        Attachment a1 = Attachment.newAttachment().
            mimeType( "image/jpg" ).
            size( 1024 ).
            label( "My Image 1" ).
            name( "MyImage.jpg" ).
            build();

        Attachment.Builder a2Builder = Attachment.newAttachment( a1 );

        assertTrue( a1.equals( a1 ) );

        assertTrue( a1.equals( a2Builder.build() ) );

        assertFalse( a1.equals( new Object() ) );

        assertFalse( a1.equals( a2Builder.size( 2048 ).build() ) );
    }

}