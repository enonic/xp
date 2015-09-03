package com.enonic.xp.mail.impl;

import java.util.HashMap;
import java.util.List;

import javax.mail.Message;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;

import com.enonic.xp.mail.MailException;
import com.enonic.xp.mail.MailMessage;
import com.enonic.xp.mail.MailService;

import static org.junit.Assert.*;

public class MailServiceImplTest
{
    MailService mailService;

    @Before
    public void setUp() throws Exception {
        this.mailService = new MailServiceImpl();
        ((MailServiceImpl)this.mailService).activate( new HashMap<>( ) );
    }

    @After
    public void tearDown() throws Exception {
       Mailbox.clearAll();
    }

    @Test
    public void sendTest() throws Exception {

        MailMessage mailMessage = msg -> {
            msg.setRecipients( Message.RecipientType.TO, "testuser@mockserver.com" );
            msg.setSubject( "Some Subject" );
            msg.setText( "sometext" );
        };

        this.mailService.send( mailMessage );

        List<Message> inbox = Mailbox.get( "testuser@mockserver.com" );
        assertEquals( 1, inbox.size() );
    }

    @Test (expected = MailException.class)
    public void sessionNotActivatedTest() throws Exception {

        MailServiceImpl mailService = new MailServiceImpl();

        MailMessage mailMessage = msg -> {
            msg.setRecipients( Message.RecipientType.TO, "testuser@mockserver.com" );
            msg.setSubject( "Some Subject" );
            msg.setText( "sometext" );
        };

        mailService.send( mailMessage );
    }


}
