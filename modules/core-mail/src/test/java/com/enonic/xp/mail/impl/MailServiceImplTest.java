package com.enonic.xp.mail.impl;

import java.util.List;

import javax.mail.Message;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;
import org.mockito.Mockito;

import com.enonic.xp.mail.MailException;
import com.enonic.xp.mail.MailMessage;

import static org.junit.Assert.*;

public class MailServiceImplTest
{
    private MailServiceImpl mailService;

    @Before
    public void setUp()
        throws Exception
    {
        final MailConfig config = Mockito.mock( MailConfig.class );
        Mockito.when( config.smtpHost() ).thenReturn( "localhost" );
        Mockito.when( config.smtpPort() ).thenReturn( 25 );

        this.mailService = new MailServiceImpl();
        this.mailService.activate( config );
    }

    @After
    public void tearDown()
        throws Exception
    {
        Mailbox.clearAll();
    }

    @Test
    public void sendTest()
        throws Exception
    {
        this.mailService.send( createMockMessage() );

        List<Message> inbox = Mailbox.get( "testuser@mockserver.com" );
        assertEquals( 1, inbox.size() );
    }

    @Test(expected = MailException.class)
    public void sessionNotActivatedTest()
        throws Exception
    {

        MailServiceImpl mailService = new MailServiceImpl();

        mailService.send( createMockMessage() );
    }

    private MailMessage createMockMessage()
    {
        return msg -> {
            msg.setRecipients( Message.RecipientType.TO, "testuser@mockserver.com" );
            msg.setSubject( "Some Subject" );
            msg.setText( "sometext" );
        };
    }
}
