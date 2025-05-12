package com.enonic.xp.attachment;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateAttachmentsTest
{
    @Test
    public void empty()
    {
        CreateAttachments attachments = CreateAttachments.empty();

        assertTrue( attachments.isEmpty() );
    }

    @Test
    public void fromBuilder()
    {
        CreateAttachment a1 = CreateAttachment.create().
            mimeType( "image/jpeg" ).
            byteSource( ByteSource.empty() ).
            label( "My Image 1" ).
            name( "MyImage.jpg" ).
            build();

        CreateAttachment a2 = CreateAttachment.create().
            mimeType( "image/gif" ).
            byteSource( ByteSource.empty() ).
            label( "My Image 2" ).
            name( "MyImage.something.gif" ).
            build();

        CreateAttachment a3 = CreateAttachment.create().
            mimeType( "image/png" ).
            byteSource( ByteSource.empty() ).
            label( "My Image 3" ).
            name( "MyImage2.png" ).
            build();

        CreateAttachments attachments = CreateAttachments.create().add( a1 ).add( CreateAttachments.from( a2, a3 ) ).build();

        assertEquals( 3, attachments.getSize() );
        assertEquals( a1, attachments.first() );
    }

    @Test
    public void fromIterable()
    {
        CreateAttachment a1 = CreateAttachment.create().
            mimeType( "image/jpeg" ).
            byteSource( ByteSource.empty() ).
            label( "My Image 1" ).
            name( "MyImage.jpg" ).
            build();

        CreateAttachment a2 = CreateAttachment.create().
            mimeType( "image/gif" ).
            byteSource( ByteSource.empty() ).
            label( "My Image 2" ).
            name( "MyImage.something.gif" ).
            build();

        CreateAttachment a3 = CreateAttachment.create().
            mimeType( "image/png" ).
            byteSource( ByteSource.empty() ).
            label( "My Image 3" ).
            name( "MyImage2.png" ).
            build();

        CreateAttachments attachments = CreateAttachments.from( List.of( a1, a2 ) );

        assertEquals( 2, attachments.getSize() );
        Iterator<CreateAttachment> it = attachments.iterator();
        assertEquals( a1, it.next() );
        assertEquals( a2, it.next() );

        CreateAttachments newAttachments = CreateAttachments.from( (Iterable<CreateAttachment>) List.of( a2, a3 ) );

        assertEquals( 2, newAttachments.getSize() );
        it = newAttachments.iterator();
        assertEquals( a2, it.next() );
        assertEquals( a3, it.next() );
    }

}
