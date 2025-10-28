package com.enonic.xp.attachment;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AttachmentTest
{
    @Test
    void getNameWithoutExtension()
    {
        assertEquals( "MyImage", Attachment.create().
            mimeType( "image/jpeg" ).
            name( "MyImage.jpg" ).
            build().getNameWithoutExtension() );

        assertEquals( "MyImage.something", Attachment.create().
            mimeType( "image/gif" ).
            name( "MyImage.something.gif" ).
            build().getNameWithoutExtension() );
    }

    @Test
    void getBinaryReference()
    {
        assertEquals( "MyImage.jpg", Attachment.create().
            mimeType( "image/jpeg" ).
            name( "MyImage.jpg" ).
            build().getBinaryReference().toString() );

        assertEquals( "MyImage.something.gif", Attachment.create().
            mimeType( "image/gif" ).
            name( "MyImage.something.gif" ).
            build().getBinaryReference().toString() );
    }

    @Test
    void getExtension()
    {
        assertEquals( "jpg", Attachment.create().
            mimeType( "image/jpeg" ).
            name( "MyImage.jpg" ).
            build().getExtension() );

        assertEquals( "gif", Attachment.create().
            mimeType( "image/gif" ).
            name( "MyImage.gif" ).
            build().getExtension() );

        assertEquals( "jpeg", Attachment.create().
            mimeType( "image/jpeg" ).
            name( "MyImage.jpeg" ).
            build().getExtension() );

        assertEquals( "png", Attachment.create().
            mimeType( "image/png" ).
            name( "MyImage.png" ).
            build().getExtension() );

        assertEquals( "jpg", Attachment.create().
            mimeType( "image/jpeg" ).
            name( "MyImage.something.jpg" ).
            build().getExtension() );
    }


    @Test
    void serializeAttachment()
    {
        Attachment a1 = Attachment.create().
            mimeType( "image/jpeg" ).
            size( 1024 ).
            label( "My Image 1" ).
            name( "MyImage.jpg" ).
            build();

        assertEquals( "Attachment{name=MyImage.jpg, mimeType=image/jpeg, label=My Image 1, size=1024, sha512=null, textContent=null}", a1.toString() );

    }

    @Test
    void compareAttachments()
    {
        Attachment a1 = Attachment.create().
            mimeType( "image/jpeg" ).
            size( 1024 ).
            label( "My Image 1" ).
            name( "MyImage.jpg" ).
            build();

        Attachment.Builder a2Builder = Attachment.create( a1 );

        assertTrue( a1.equals( a1 ) );

        assertTrue( a1.equals( a2Builder.build() ) );

        assertFalse( a1.equals( new Object() ) );

        assertFalse( a1.equals( a2Builder.size( 2048 ).build() ) );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( Attachment.class ).verify();
    }
}
