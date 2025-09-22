package com.enonic.xp.attachment;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AttachmentsTest
{
    @Test
    public void getByLabel()
    {
        Attachment a1 = Attachment.create().
            mimeType( "image/jpeg" ).
            label( "My Image 1" ).
            name( "MyImage.jpg" ).
            build();

        Attachment a2 = Attachment.create().
            mimeType( "image/gif" ).
            label( "My Image 2" ).
            name( "MyImage.something.gif" ).
            build();

        Attachments attachments = Attachments.from( a1, a2 );

        assertFalse( attachments.isEmpty() );

        assertTrue( attachments.byLabel( "My Image 1" ) != null );
        assertFalse( attachments.byLabel( "My Image 3" ) != null );

        assertEquals( a1, attachments.byLabel( "My Image 1" ) );
        assertNull( attachments.byLabel( "My Image 3" ) );
    }

    @Test
    public void getByName()
    {
        Attachment a1 = Attachment.create().
            mimeType( "image/jpeg" ).
            label( "My Image 1" ).
            name( "MyImage.jpg" ).
            build();

        Attachment a2 = Attachment.create().
            mimeType( "image/gif" ).
            label( "My Image 2" ).
            name( "MyImage.something.gif" ).
            build();

        Attachments attachments = Attachments.from( List.of( a1, a2 ) );

        assertFalse( attachments.isEmpty() );

        assertTrue( attachments.byName( "MyImage.something.gif" ) != null );
        assertFalse( attachments.byName( "MyImage.gif" ) != null );

        assertEquals( a2, attachments.byName( "MyImage.something.gif" ) );
        assertNull( attachments.byName( "MyImage.gif" ) );
    }

    @Test
    public void fromBuilder()
    {

        Attachment a1 = Attachment.create().
            mimeType( "image/jpeg" ).
            label( "My Image 1" ).
            name( "MyImage.jpg" ).
            build();

        Attachment a2 = Attachment.create().
            mimeType( "image/gif" ).
            label( "My Image 2" ).
            name( "MyImage.something.gif" ).
            build();

        Attachment a3 = Attachment.create().
            mimeType( "image/png" ).
            label( "My Image 3" ).
            name( "MyImage2.png" ).
            build();

        Attachments attachments = Attachments.create().add( a1 ).add( a2 ).add( a3 ).build();

        assertEquals( 3, attachments.getSize() );
        assertEquals( a1, attachments.first() );
        assertEquals( a2, attachments.get( 1 ) );
        assertEquals( a3, attachments.last() );
    }

    @Test
    public void fromEmpty()
    {
        Attachments attachments = Attachments.empty();

        assertTrue( attachments.isEmpty() );
    }

    @Test
    public void testByLabelForAttachmentWithoutLabel()
    {
        assertNull( Attachments.create()
                        .add( Attachment.create().mimeType( "image/gif" ).name( "image1.gif" ).build() )
                        .add( Attachment.create().mimeType( "image/gif" ).label( "source" ).name( "image2.gif" ).build() )
                        .build()
                        .byLabel( "anyLabel" ) );

        Attachment attachment = Attachments.create()
            .add( Attachment.create().mimeType( "image/gif" ).name( "image1.gif" ).build() )
            .add( Attachment.create().mimeType( "image/gif" ).label( "source" ).name( "image2.gif" ).build() )
            .build()
            .byLabel( "source" );

        assertNotNull( attachment );
        assertEquals( "image2.gif", attachment.getName() );
    }

}
