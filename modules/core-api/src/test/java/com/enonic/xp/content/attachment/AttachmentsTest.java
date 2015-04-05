package com.enonic.xp.content.attachment;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import static org.junit.Assert.*;

public class AttachmentsTest
{
    @Test
    public void getByLabel()
    {
        Attachment a1 = Attachment.newAttachment().
            mimeType( "image/jpg" ).
            label( "My Image 1" ).
            name( "MyImage.jpg" ).
            build();

        Attachment a2 = Attachment.newAttachment().
            mimeType( "image/gif" ).
            label( "My Image 2" ).
            name( "MyImage.something.gif" ).
            build();

        Attachments attachments = Attachments.from( a1, a2 );

        assertTrue( attachments.isNotEmpty() );

        assertTrue( attachments.hasByLabel( "My Image 1" ) );
        assertFalse( attachments.hasByLabel( "My Image 3" ) );

        assertEquals( a1, attachments.byLabel( "My Image 1" ) );
        assertNull( attachments.byLabel( "My Image 3" ) );
    }

    @Test
    public void getByName()
    {
        Attachment a1 = Attachment.newAttachment().
            mimeType( "image/jpg" ).
            label( "My Image 1" ).
            name( "MyImage.jpg" ).
            build();

        Attachment a2 = Attachment.newAttachment().
            mimeType( "image/gif" ).
            label( "My Image 2" ).
            name( "MyImage.something.gif" ).
            build();

        Attachments attachments = Attachments.from( ImmutableList.of( a1, a2 ) );

        assertFalse( attachments.isEmpty() );

        assertTrue( attachments.hasByName( "MyImage.something.gif" ) );
        assertFalse( attachments.hasByName( "MyImage.gif" ) );

        assertEquals( a2, attachments.byName( "MyImage.something.gif" ) );
        assertNull( attachments.byName( "MyImage.gif" ) );
    }

    @Test
    public void fromBuilder()
    {

        Attachment a1 = Attachment.newAttachment().
            mimeType( "image/jpg" ).
            label( "My Image 1" ).
            name( "MyImage.jpg" ).
            build();

        Attachment a2 = Attachment.newAttachment().
            mimeType( "image/gif" ).
            label( "My Image 2" ).
            name( "MyImage.something.gif" ).
            build();

        Attachment a3 = Attachment.newAttachment().
            mimeType( "image/png" ).
            label( "My Image 3" ).
            name( "MyImage2.png" ).
            build();

        Attachments attachments = Attachments.builder().add( a1 ).addAll( Attachments.from( a2, a3 ) ).build();

        assertEquals( 3, attachments.getSize() );
        assertEquals( a1, attachments.first() );
        assertEquals( a2, attachments.get( 1 ) );
        assertEquals( a3, attachments.last() );
    }

    @Test
    public void fromEmpty()
    {
        Attachment a1 = Attachment.newAttachment().
            mimeType( "image/jpg" ).
            label( "My Image 1" ).
            name( "MyImage.jpg" ).
            build();

        Attachment a2 = Attachment.newAttachment().
            mimeType( "image/gif" ).
            label( "My Image 2" ).
            name( "MyImage.something.gif" ).
            build();

        Attachment a3 = Attachment.newAttachment().
            mimeType( "image/png" ).
            label( "My Image 3" ).
            name( "MyImage2.png" ).
            build();

        Attachments attachments = Attachments.empty();

        assertTrue( attachments.isEmpty() );

        Attachments newAttachments = attachments.add( a1 );

        assertTrue( attachments.isEmpty() );
        assertEquals( 1, newAttachments.getSize() );
        assertEquals( a1, newAttachments.get( 0 ) );

        Attachments newestAttachments = newAttachments.add( ImmutableList.of( a2, a3 ) );

        assertEquals( 1, newAttachments.getSize() );
        assertEquals( 3, newestAttachments.getSize() );
        assertEquals( newAttachments.first(), newestAttachments.first() );
    }

}