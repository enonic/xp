package com.enonic.xp.mail;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MailAttachmentTest
{
    @Test
    void fileNameWithLineBreakRejected()
    {
        final MailAttachment.Builder builder =
            MailAttachment.create().data( ByteSource.empty() ).fileName( "report.txt\r\nContent-Type: text/html" );

        assertThrows( IllegalArgumentException.class, builder::build );
    }

    @Test
    void mimeTypeWithLineBreakRejected()
    {
        final MailAttachment.Builder builder =
            MailAttachment.create().data( ByteSource.empty() ).fileName( "report.txt" ).mimeType( "text/plain\r\nBcc: attacker@example.com" );

        assertThrows( IllegalArgumentException.class, builder::build );
    }

    @Test
    void headerWithLineBreakRejected()
    {
        final MailAttachment.Builder builder = MailAttachment.create()
            .data( ByteSource.empty() )
            .fileName( "report.txt" )
            .headers( Map.of( "Content-ID", "<report>\r\nBcc: attacker@example.com" ) );

        assertThrows( IllegalArgumentException.class, builder::build );
    }

    @Test
    void singleLineValuesAccepted()
    {
        final MailAttachment attachment = MailAttachment.create()
            .data( ByteSource.empty() )
            .fileName( "report.txt" )
            .mimeType( "text/plain" )
            .headers( Map.of( "Content-ID", "<report>" ) )
            .build();

        assertEquals( "report.txt", attachment.getFileName() );
        assertEquals( "text/plain", attachment.getMimeType() );
        assertEquals( "<report>", attachment.getHeaders().get( "Content-ID" ) );
    }
}
