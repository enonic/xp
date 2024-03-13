package com.enonic.xp.lib.mail;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.CharStreams;

import com.enonic.xp.mail.MailMessage;
import com.enonic.xp.mail.MailMessageParams;
import com.enonic.xp.mail.MailService;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

public class SendMailScriptTest
    extends ScriptTestSupport
{
    private MailMessageParams actualMessage;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        addService( MailService.class, new MailService()
        {
            @Override
            public void send( final MailMessage message )
            {
                // do nothing
            }

            @Override
            public void send( final MailMessageParams message )
            {
                SendMailScriptTest.this.actualMessage = message;
            }
        } );
    }

    @Test
    public void testExample()
    {
        runScript( "/lib/xp/examples/mail/send.js" );
    }

    @Test
    public void testSimpleMail()
    {
        runFunction( "/test/send-test.js", "simpleMail" );

        final MailMessageParams message = this.actualMessage;

        assertEquals( "test subject", message.getSubject() );
        assertEquals( "test body", message.getBody() );
        assertArrayEquals( new String[]{"from@bar.com"}, message.getFrom() );
        assertArrayEquals( new String[]{"to@bar.com"}, message.getTo() );
        assertArrayEquals( new String[]{"cc@bar.com"}, message.getCc() );
        assertArrayEquals( new String[]{"bcc@bar.com"}, message.getBcc() );
        assertArrayEquals( new String[]{"replyTo@bar.com"}, message.getReplyTo() );
        assertEquals( "Value", message.getHeaders().get( "X-Custom" ) );
        assertEquals( "2", message.getHeaders().get( "X-Other" ) );
    }

    @Test
    public void testMultiRecipientsMail()
    {
        runFunction( "/test/send-test.js", "multiRecipientsMail" );

        final MailMessageParams message = this.actualMessage;

        assertEquals( "test subject", message.getSubject() );
        assertEquals( "test body", message.getBody() );
        assertArrayEquals( new String[]{"from@bar.com", "from@foo.com"}, message.getFrom() );
        assertArrayEquals( new String[]{"to@bar.com", "to@foo.com"}, message.getTo() );
        assertArrayEquals( new String[]{"cc@bar.com", "cc@foo.com"}, message.getCc() );
        assertArrayEquals( new String[]{"bcc@bar.com", "bcc@foo.com"}, message.getBcc() );
        assertArrayEquals( new String[]{"replyTo@bar.com", "replyTo@foo.com"}, message.getReplyTo() );
    }

    @Test
    public void testRFC822AddressMail()
        throws Exception
    {
        runFunction( "/test/send-test.js", "rfc822AddressMail" );

        final MailMessageParams message = this.actualMessage;

        assertEquals( "test subject", message.getSubject() );
        assertEquals( "test body", message.getBody() );
        assertArrayEquals( new String[]{"From Bar <from@bar.com>", "From Foo <from@foo.com>"}, message.getFrom() );
        assertArrayEquals( new String[]{"To Bar <to@bar.com>", "To Foo <to@foo.com>"}, message.getTo() );
    }

    @Test
    public void testFailSendMail()
        throws Exception
    {
        final MailService mailService = new MailService()
        {
            @Override
            public void send( final MailMessage message )
            {
                throw new RuntimeException( "Error sending mail" );
            }

            @Override
            public void send( final MailMessageParams message )
            {
                throw new RuntimeException( "Error sending mail" );
            }
        };
        addService( MailService.class, mailService );

        runFunction( "/test/send-test.js", "failSendMail" );

        assertNull( this.actualMessage );
    }

    @Test
    public void testMailWithContentType()
        throws Exception
    {
        runFunction( "/test/send-test.js", "sendMailWithContentType" );

        final MailMessageParams message = this.actualMessage;

        assertEquals( "test subject", message.getSubject() );
        assertEquals( "test body", message.getBody() );
        assertArrayEquals( new String[]{"from@bar.com"}, message.getFrom() );
        assertArrayEquals( new String[]{"to@bar.com"}, message.getTo() );
        assertEquals( "text/html", message.getContentType() );
    }

    @Test
    public void testFailMissingFrom()
        throws Exception
    {
        final MailService mailService = new MailService()
        {
            @Override
            public void send( final MailMessage message )
            {
                throw new RuntimeException( "Error sending mail" );
            }

            @Override
            public void send( final MailMessageParams message )
            {
                throw new RuntimeException( "Error sending mail" );
            }
        };
        addService( MailService.class, mailService );

        try
        {
            runFunction( "/test/send-test.js", "sendWithoutRequiredFrom" );
            fail( "Expected exception" );
        }
        catch ( ResourceProblemException e )
        {
            assertEquals( "Parameter 'from' is required", e.getMessage() );
        }

        assertNull( this.actualMessage );
    }

    @Test
    public void testFailMissingTo()
        throws Exception
    {
        addService( MailService.class, new MailService()
        {
            @Override
            public void send( final MailMessage message )
            {
                throw new RuntimeException( "Error sending mail" );
            }

            @Override
            public void send( final MailMessageParams message )
            {
                throw new RuntimeException( "Error sending mail" );
            }
        } );

        try
        {
            runFunction( "/test/send-test.js", "sendWithoutRequiredTo" );
            fail( "Expected exception" );
        }
        catch ( ResourceProblemException e )
        {
            assertEquals( "Parameter 'to' is required", e.getMessage() );
        }

        assertNull( this.actualMessage );
    }

    @Test
    public void testMailWithAttachments()
        throws Exception
    {
        runFunction( "/test/send-test.js", "sendWithAttachments" );

        final MailMessageParams message = this.actualMessage;

        assertEquals( 2, message.getAttachments().size() );

        final Map<String, Object> attachment1 = message.getAttachments().get( 0 );
        assertEquals( "image.png", attachment1.get( "fileName" ) );
        assertEquals( "image/png", attachment1.get( "mimeType" ) );

        assertEquals( "image data", CharStreams.toString(
            new InputStreamReader( ( (ByteSource) attachment1.get( "data" ) ).openBufferedStream(), StandardCharsets.UTF_8 ) ) );
        Map<String, Object> headersAttachment1 = (Map<String, Object>) attachment1.get( "headers" );
        assertEquals( "<myimg>", headersAttachment1.get( "Content-ID" ) );

        final Map<String, Object> attachment2 = message.getAttachments().get( 1 );
        assertEquals( "text.txt", attachment2.get( "fileName" ) );
    }

    public ByteSource createByteSource( final String value )
    {
        return ByteSource.wrap( value.getBytes() );
    }
}
