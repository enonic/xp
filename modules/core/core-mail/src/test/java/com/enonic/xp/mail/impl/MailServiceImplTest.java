package com.enonic.xp.mail.impl;

import java.util.List;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.mock_javamail.Mailbox;

import com.enonic.xp.mail.MailException;
import com.enonic.xp.mail.MailMessage;
import com.enonic.xp.mail.SendMailParams;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MailServiceImplTest
{
    private MailServiceImpl mailService;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        final MailConfig config = mock( MailConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( config.defaultFromEmail() ).thenReturn( "noreply@domain.com" );

        this.mailService = new MailServiceImpl();
        this.mailService.activate( config );
    }

    @AfterEach
    public void tearDown()
        throws Exception
    {
        Mailbox.clearAll();
    }

    @Test
    public void sendTest()
        throws Exception
    {
        this.mailService.send( this::createMockMessage );

        List<Message> inbox = Mailbox.get( "testuser@mockserver.com" );
        assertEquals( 1, inbox.size() );
    }

    @Test
    public void sessionNotActivatedTest()
        throws Exception
    {

        MailServiceImpl mailService = new MailServiceImpl();

        MailMessage mockMessage = this::createMockMessage;
        assertThrows( MailException.class, () -> mailService.send( mockMessage ) );
    }

    @Test
    public void testSend()
    {
        assertDoesNotThrow( () -> this.mailService.send(
            SendMailParams.create().subject( "test subject" ).body( "test body" ).to( "to@bar.com" ).from( "from@bar.com" ).build() ) );
    }

    @Test
    public void testGetDefaultFromEmail()
    {
        assertEquals( "noreply@domain.com", this.mailService.getDefaultFromEmail() );
    }

    private void createMockMessage( MimeMessage msg )
        throws Exception
    {
        msg.setRecipients( Message.RecipientType.TO, "testuser@mockserver.com" );
        msg.setSubject( "Some Subject" );
        msg.setText( "sometext" );
    }
}
