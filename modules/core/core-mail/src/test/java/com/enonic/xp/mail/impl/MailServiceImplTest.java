package com.enonic.xp.mail.impl;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.mock_javamail.Mailbox;

import jakarta.mail.Message;

import com.enonic.xp.mail.MailException;
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
        this.mailService.send( createMockMessage() );

        Thread.sleep( 1000 );

        List<Message> inbox = Mailbox.get( "testuser@mockserver.com" );
        assertEquals( 1, inbox.size() );
    }

    @Test
    public void sessionNotActivatedTest()
        throws Exception
    {

        MailServiceImpl mailService = new MailServiceImpl();

        assertThrows( MailException.class, () -> mailService.send( createMockMessage() ) );
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

    private SendMailParams createMockMessage()
    {
        return SendMailParams.create().to( "testuser@mockserver.com" ).subject( "Some Subject" ).body( "sometext" ).build();
    }
}
