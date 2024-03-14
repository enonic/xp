package com.enonic.xp.mail.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.CharStreams;

import com.enonic.xp.mail.SendMailParams;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MimeMessageConverterTest
{
    @Test
    public void testSimpleMail()
        throws Exception
    {
        SendMailParams params = SendMailParams.create()
            .subject( "test subject" )
            .body( "test body" )
            .to( "to@bar.com" )
            .from( "from@bar.com" )
            .cc( "cc@bar.com" )
            .bcc( "bcc@bar.com" )
            .replyTo( "replyTo@bar.com" )
            .headers( Map.of( "X-Custom", "Value", "X-Other", "2" ) )
            .build();

        Message message = new MimeMessageConverter( null, Session.getDefaultInstance( new Properties() ) ).convert( params );

        assertEquals( "test subject", message.getSubject() );
        assertEquals( "test body", message.getContent() );
        assertArrayEquals( toAddresses( "from@bar.com" ), message.getFrom() );
        assertArrayEquals( toAddresses( "to@bar.com" ), message.getRecipients( Message.RecipientType.TO ) );
        assertArrayEquals( toAddresses( "cc@bar.com" ), message.getRecipients( Message.RecipientType.CC ) );
        assertArrayEquals( toAddresses( "bcc@bar.com" ), message.getRecipients( Message.RecipientType.BCC ) );
        assertArrayEquals( toAddresses( "replyTo@bar.com" ), message.getReplyTo() );
        assertEquals( "Value", message.getHeader( "X-Custom" )[0] );
        assertEquals( "2", message.getHeader( "X-Other" )[0] );
    }

    @Test
    public void testMultiRecipientsMail()
        throws Exception
    {
        SendMailParams params = SendMailParams.create()
            .subject( "test subject" )
            .body( "test body" )
            .to( "to@bar.com", "to@foo.com" )
            .from( "from@bar.com", "from@foo.com" )
            .cc( "cc@bar.com", "cc@foo.com" )
            .bcc( "bcc@bar.com", "bcc@foo.com" )
            .replyTo( "replyTo@bar.com", "replyTo@foo.com" )
            .build();

        Message message = new MimeMessageConverter( null, Session.getDefaultInstance( new Properties() ) ).convert( params );

        assertEquals( "test subject", message.getSubject() );
        assertEquals( "test body", message.getContent() );
        assertArrayEquals( toAddresses( "from@bar.com", "from@foo.com" ), message.getFrom() );
        assertArrayEquals( toAddresses( "to@bar.com", "to@foo.com" ), message.getRecipients( Message.RecipientType.TO ) );
        assertArrayEquals( toAddresses( "cc@bar.com", "cc@foo.com" ), message.getRecipients( Message.RecipientType.CC ) );
        assertArrayEquals( toAddresses( "bcc@bar.com", "bcc@foo.com" ), message.getRecipients( Message.RecipientType.BCC ) );
        assertArrayEquals( toAddresses( "replyTo@bar.com", "replyTo@foo.com" ), message.getReplyTo() );
    }

    @Test
    public void testRfc822AddressMail()
        throws Exception
    {
        SendMailParams params = SendMailParams.create()
            .subject( "test subject" )
            .body( "test body" )
            .to( "To Bar <to@bar.com>", "To Foo <to@foo.com>" )
            .from( "From Bar <from@bar.com>", "From Foo <from@foo.com>", "<>", "Some User <>", "username@domain.com" )
            .build();

        Message message =
            new MimeMessageConverter( "noreply@domain.com", Session.getDefaultInstance( new Properties() ) ).convert( params );

        assertEquals( "test subject", message.getSubject() );
        assertEquals( "test body", message.getContent() );
        assertArrayEquals(
            toAddresses( "From Bar <from@bar.com>", "From Foo <from@foo.com>", "noreply@domain.com", "Some User <noreply@domain.com>",
                         "username@domain.com" ), message.getFrom() );
        assertArrayEquals( toAddresses( "To Bar <to@bar.com>", "To Foo <to@foo.com>" ), message.getRecipients( Message.RecipientType.TO ) );
    }

    @Test
    public void testDefaultFromMail()
        throws Exception
    {
        SendMailParams params = SendMailParams.create()
            .subject( "test subject" )
            .body( "test body" )
            .to( "To Bar <to@bar.com>", "To Foo <to@foo.com>" )
            .from( "Username <username@domain.com>", "<>", "Some User <>", "username2@domain.com" )
            .build();

        Message message =
            new MimeMessageConverter( "noreply@domain.com", Session.getDefaultInstance( new Properties() ) ).convert( params );

        assertEquals( "test subject", message.getSubject() );
        assertEquals( "test body", message.getContent() );
        assertArrayEquals(
            toAddresses( "Username <username@domain.com>", "noreply@domain.com", "Some User <noreply@domain.com>", "username2@domain.com" ),
            message.getFrom() );
        assertArrayEquals( toAddresses( "To Bar <to@bar.com>", "To Foo <to@foo.com>" ), message.getRecipients( Message.RecipientType.TO ) );
    }

    @Test
    public void testInvalidDefaultFromMail()
        throws Exception
    {
        SendMailParams params = SendMailParams.create()
            .subject( "test subject" )
            .body( "test body" )
            .to( "To Bar <to@bar.com>", "To Foo <to@foo.com>" )
            .from( "Username <username@domain.com>", "<>", "Some User <>", "username2@domain.com" )
            .build();

        IllegalArgumentException ex = assertThrows( IllegalArgumentException.class, () -> new MimeMessageConverter( null,
                                                                                                                    Session.getDefaultInstance(
                                                                                                                        new Properties() ) ).convert(
            params ) );

        assertEquals( "To use \"<>\" the \"defaultFromEmail\" configuration must be set in \"com.enonic.xp.mail.cfg\"", ex.getMessage() );
    }

    @Test
    public void testSendMailWithContentType()
        throws Exception
    {
        SendMailParams params = SendMailParams.create()
            .subject( "test subject" )
            .body( "test body" )
            .to( "to@bar.com" )
            .from( "from@bar.com" )
            .contentType( "text/html" )
            .build();

        Message message = new MimeMessageConverter( null, Session.getDefaultInstance( new Properties() ) ).convert( params );

        assertEquals( "test subject", message.getSubject() );
        assertEquals( "test body", message.getContent() );
        assertArrayEquals( toAddresses( "from@bar.com" ), message.getFrom() );
        assertArrayEquals( toAddresses( "to@bar.com" ), message.getRecipients( Message.RecipientType.TO ) );
        assertEquals( "text/html", message.getContentType() );
    }

    @Test
    public void testSendWithAttachments()
        throws Exception
    {
        Map<String, Object> attachment1 = new HashMap<>();
        attachment1.put( "fileName", "image.png" );
        attachment1.put( "mimeType", "image/png" );
        attachment1.put( "data", ByteSource.wrap( "image data".getBytes() ) );
        attachment1.put( "headers", Map.of( "Content-ID", "<myimg>" ) );

        Map<String, Object> attachment2 = new HashMap<>();
        attachment2.put( "fileName", "text.txt" );
        attachment2.put( "data", ByteSource.wrap( "Some text".getBytes() ) );

        SendMailParams params = SendMailParams.create()
            .subject( "test subject" )
            .body( "test body" )
            .to( "to@bar.com" )
            .from( "from@bar.com" )
            .attachments( List.of( attachment1, attachment2 ) )
            .build();

        Message message = new MimeMessageConverter( null, Session.getDefaultInstance( new Properties() ) ).convert( params );
        message.saveChanges(); // required to updated headers (mimeType)

        MimeMultipart content = (MimeMultipart) message.getContent();
        assertEquals( 3, content.getCount() );
        final BodyPart first = content.getBodyPart( 0 );
        final BodyPart second = content.getBodyPart( 1 );
        final BodyPart third = content.getBodyPart( 2 );
        final String secondContent =
            CharStreams.toString( new InputStreamReader( (InputStream) second.getContent(), StandardCharsets.UTF_8 ) );

        assertEquals( "test body", first.getContent() );
        assertEquals( "image data", secondContent );
        assertEquals( "Some text", third.getContent() );

        assertNull( first.getFileName() );
        assertEquals( "image.png", second.getFileName() );
        assertEquals( "text.txt", third.getFileName() );

        assertEquals( "text/plain; charset=UTF-8", first.getContentType() );
        assertTrue( second.getContentType().startsWith( "image/png" ) );
        assertTrue( third.getContentType().startsWith( "text/plain" ) );

        assertEquals( "<myimg>", second.getHeader( "Content-ID" )[0] );
    }


    private InternetAddress[] toAddresses( final String... addresses )
    {
        return Stream.of( addresses ).map( this::toAddress ).toArray( InternetAddress[]::new );
    }

    private InternetAddress toAddress( final String address )
        throws RuntimeException
    {
        try
        {
            return new InternetAddress( address );
        }
        catch ( AddressException e )
        {
            throw new RuntimeException( e );
        }
    }
}
