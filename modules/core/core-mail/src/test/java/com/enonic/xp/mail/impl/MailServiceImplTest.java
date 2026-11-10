package com.enonic.xp.mail.impl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.mock_javamail.Mailbox;

import com.google.common.io.ByteSource;

import jakarta.mail.Message;
import jakarta.mail.internet.MimeMultipart;

import com.enonic.xp.mail.MailAttachment;
import com.enonic.xp.mail.MailException;
import com.enonic.xp.mail.SendMailParams;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MailServiceImplTest
{
    private MailServiceImpl mailService;

    @BeforeEach
    void setUp()
    {
        final MailConfig config = mock( MailConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( config.defaultFromEmail() ).thenReturn( "noreply@domain.com" );

        this.mailService = new MailServiceImpl();
        this.mailService.activate( config );
    }

    @AfterEach
    void tearDown()
    {
        Mailbox.clearAll();
    }

    @Test
    void sendTest()
        throws Exception
    {
        this.mailService.send( createMockMessage() );

        List<Message> inbox = Mailbox.get( "testuser@mockserver.com" );
        assertEquals( 1, inbox.size() );
    }

    @Test
    void sendWithAttachmentTest()
        throws Exception
    {
        final SendMailParams params = SendMailParams.create()
            .to( "testuser@mockserver.com" )
            .from( "from@bar.com" )
            .subject( "Some Subject" )
            .body( "sometext" )
            .addAttachment( MailAttachment.create()
                                .fileName( "hello.txt" )
                                .mimeType( "text/plain" )
                                .data( ByteSource.wrap( "hello".getBytes( StandardCharsets.UTF_8 ) ) )
                                .build() )
            .build();

        assertDoesNotThrow( () -> this.mailService.send( params ) );

        final List<Message> inbox = Mailbox.get( "testuser@mockserver.com" );
        assertEquals( 1, inbox.size() );

        final Message message = inbox.get( 0 );
        assertTrue( message.isMimeType( "multipart/mixed" ) );

        final MimeMultipart content = (MimeMultipart) message.getContent();
        assertEquals( 2, content.getCount() );
        assertEquals( "sometext", content.getBodyPart( 0 ).getContent() );
        assertEquals( "hello.txt", content.getBodyPart( 1 ).getFileName() );
        assertEquals( "hello", new String( content.getBodyPart( 1 ).getInputStream().readAllBytes(), StandardCharsets.UTF_8 ) );
    }

    @Test
    void sessionNotActivatedTest()
    {

        MailServiceImpl mailService = new MailServiceImpl();

        assertThrows( MailException.class, () -> mailService.send( createMockMessage() ) );
    }

    @Test
    void testSend()
    {
        assertDoesNotThrow( () -> this.mailService.send(
            SendMailParams.create().subject( "test subject" ).body( "test body" ).to( "to@bar.com" ).from( "from@bar.com" ).build() ) );
    }

    @Test
    void testGetDefaultFromEmail()
    {
        assertEquals( "noreply@domain.com", this.mailService.getDefaultFromEmail() );
    }

    private SendMailParams createMockMessage()
    {
        return SendMailParams.create().to( "testuser@mockserver.com" ).subject( "Some Subject" ).body( "sometext" ).build();
    }
}
