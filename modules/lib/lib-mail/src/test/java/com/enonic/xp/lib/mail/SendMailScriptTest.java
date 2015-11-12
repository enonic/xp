package com.enonic.xp.lib.mail;

import java.util.stream.Stream;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.mail.MailMessage;
import com.enonic.xp.mail.MailService;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.testing.script.ScriptTestSupport;

import static org.junit.Assert.*;

public class SendMailScriptTest
    extends ScriptTestSupport
{
    private MailMessage actualMessage;

    @Override
    public void initialize()
    {
        super.initialize();
        final MailService mailService = message -> this.actualMessage = message;
        addService( MailService.class, mailService );
    }

    @Test
    public void testSimpleMail()
        throws Exception
    {
        runFunction( "/site/test/send-test.js", "simpleMail" );

        final MimeMessage message = mockCompose( this.actualMessage );

        Assert.assertEquals( "test subject", message.getSubject() );
        Assert.assertEquals( "test body", message.getContent() );
        assertArrayEquals( toAddresses( "from@bar.com" ), message.getFrom() );
        assertArrayEquals( toAddresses( "to@bar.com" ), message.getRecipients( Message.RecipientType.TO ) );
        assertArrayEquals( toAddresses( "cc@bar.com" ), message.getRecipients( Message.RecipientType.CC ) );
        assertArrayEquals( toAddresses( "bcc@bar.com" ), message.getRecipients( Message.RecipientType.BCC ) );
        assertArrayEquals( toAddresses( "replyTo@bar.com" ), message.getReplyTo() );
        Assert.assertEquals( "Value", message.getHeader( "X-Custom" )[0] );
        Assert.assertEquals( "2", message.getHeader( "X-Other" )[0] );
    }

    @Test
    public void testMultiRecipientsMail()
        throws Exception
    {
        runFunction( "/site/test/send-test.js", "multiRecipientsMail" );

        final MimeMessage message = mockCompose( this.actualMessage );

        Assert.assertEquals( "test subject", message.getSubject() );
        Assert.assertEquals( "test body", message.getContent() );
        assertArrayEquals( toAddresses( "from@bar.com", "from@foo.com" ), message.getFrom() );
        assertArrayEquals( toAddresses( "to@bar.com", "to@foo.com" ), message.getRecipients( Message.RecipientType.TO ) );
        assertArrayEquals( toAddresses( "cc@bar.com", "cc@foo.com" ), message.getRecipients( Message.RecipientType.CC ) );
        assertArrayEquals( toAddresses( "bcc@bar.com", "bcc@foo.com" ), message.getRecipients( Message.RecipientType.BCC ) );
        assertArrayEquals( toAddresses( "replyTo@bar.com", "replyTo@foo.com" ), message.getReplyTo() );
    }

    @Test
    public void testRFC822AddressMail()
        throws Exception
    {
        runFunction( "/site/test/send-test.js", "rfc822AddressMail" );

        final MimeMessage message = mockCompose( this.actualMessage );

        Assert.assertEquals( "test subject", message.getSubject() );
        Assert.assertEquals( "test body", message.getContent() );
        assertArrayEquals( toAddresses( "From Bar <from@bar.com>", "From Foo <from@foo.com>" ), message.getFrom() );
        assertArrayEquals( toAddresses( "To Bar <to@bar.com>", "To Foo <to@foo.com>" ), message.getRecipients( Message.RecipientType.TO ) );
    }

    @Test
    public void testFailSendMail()
        throws Exception
    {
        final MailService mailService = message -> {
            throw new RuntimeException( "Error sending mail" );
        };
        addService( MailService.class, mailService );

        runFunction( "/site/test/send-test.js", "failSendMail" );

        Assert.assertNull( this.actualMessage );
    }

    @Test
    public void testMailWithContentType()
        throws Exception
    {
        runFunction( "/site/test/send-test.js", "sendMailWithContentType" );

        final MimeMessage message = mockCompose( this.actualMessage );

        Assert.assertEquals( "test subject", message.getSubject() );
        Assert.assertEquals( "test body", message.getContent() );
        assertArrayEquals( toAddresses( "from@bar.com" ), message.getFrom() );
        assertArrayEquals( toAddresses( "to@bar.com" ), message.getRecipients( Message.RecipientType.TO ) );
        Assert.assertEquals( "text/html", message.getHeader( "Content-Type" )[0] );
    }

    @Test
    public void testFailMissingFrom()
        throws Exception
    {
        final MailService mailService = message -> {
            throw new RuntimeException( "Error sending mail" );
        };
        addService( MailService.class, mailService );

        try
        {
            runFunction( "/site/test/send-test.js", "sendWithoutRequiredFrom" );
            Assert.fail( "Expected exception" );
        }
        catch ( ResourceProblemException e )
        {
            assertEquals( "Parameter 'from' is required", e.getMessage() );
        }

        Assert.assertNull( this.actualMessage );
    }

    @Test
    public void testFailMissingTo()
        throws Exception
    {
        final MailService mailService = message -> {
            throw new RuntimeException( "Error sending mail" );
        };
        addService( MailService.class, mailService );

        try
        {
            runFunction( "/site/test/send-test.js", "sendWithoutRequiredTo" );
            Assert.fail( "Expected exception" );
        }
        catch ( ResourceProblemException e )
        {
            assertEquals( "Parameter 'to' is required", e.getMessage() );
        }

        Assert.assertNull( this.actualMessage );
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

    private MimeMessage mockCompose( final MailMessage message )
        throws Exception
    {
        final MimeMessage mimeMessage = new MimeMessage( (Session) null );
        message.compose( mimeMessage );
        return mimeMessage;
    }
}
