package com.enonic.xp.attachment;

import org.junit.Test;

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
            mimeType( "image/gif" ).
            name( "MyImage.something.gif" ).
            build().getNameWithoutExtension() );
    }

    @Test
    public void getBinaryReference()
    {
        assertEquals( "MyImage.jpg", Attachment.newAttachment().
            mimeType( "image/jpg" ).
            name( "MyImage.jpg" ).
            build().getBinaryReference().toString() );

        assertEquals( "MyImage.something.gif", Attachment.newAttachment().
            mimeType( "image/gif" ).
            name( "MyImage.something.gif" ).
            build().getBinaryReference().toString() );
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