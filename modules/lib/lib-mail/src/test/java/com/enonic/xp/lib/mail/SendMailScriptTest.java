package com.enonic.xp.lib.mail;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.CharStreams;

import com.enonic.xp.mail.MailAttachment;
import com.enonic.xp.mail.MailHeader;
import com.enonic.xp.mail.MailService;
import com.enonic.xp.mail.SendMailParams;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

public class SendMailScriptTest
    extends ScriptTestSupport
{
    private SendMailParams actualMessage;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        addService( MailService.class, new MailService()
        {
            @Override
            public void send( final SendMailParams message )
            {
                SendMailScriptTest.this.actualMessage = message;
            }

            @Override
            public String getDefaultFromEmail()
            {
                return "noreply@domain.com";
            }
        } );
    }

    @Test
    void testExample()
    {
        runScript( "/lib/xp/examples/mail/send.js" );
    }

    @Test
    void testGetDefaultFromEmail()
    {
        runScript( "/lib/xp/examples/mail/getDefaultFromEmail.js" );
    }

    @Test
    void testSimpleMail()
    {
        runFunction( "/test/send-test.js", "simpleMail" );

        final SendMailParams message = this.actualMessage;

        assertEquals( "test subject", message.getSubject() );
        assertEquals( "test body", message.getBody() );
        assertEquals( List.of( "from@bar.com" ), message.getFrom() );
        assertEquals( List.of( "to@bar.com" ), message.getTo() );
        assertEquals( List.of( "cc@bar.com" ), message.getCc() );
        assertEquals( List.of( "bcc@bar.com" ), message.getBcc() );
        assertEquals( List.of( "replyTo@bar.com" ), message.getReplyTo() );
        assertEquals( "Value", getHeader( message.getHeaders(), "X-Custom" ) );
        assertEquals( "2", getHeader( message.getHeaders(), "X-Other" ) );
    }

    @Test
    void testMultiRecipientsMail()
    {
        runFunction( "/test/send-test.js", "multiRecipientsMail" );

        final SendMailParams message = this.actualMessage;

        assertEquals( "test subject", message.getSubject() );
        assertEquals( "test body", message.getBody() );
        assertEquals( List.of( "from@bar.com", "from@foo.com" ), message.getFrom() );
        assertEquals( List.of( "to@bar.com", "to@foo.com" ), message.getTo() );
        assertEquals( List.of( "cc@bar.com", "cc@foo.com" ), message.getCc() );
        assertEquals( List.of( "bcc@bar.com", "bcc@foo.com" ), message.getBcc() );
        assertEquals( List.of( "replyTo@bar.com", "replyTo@foo.com" ), message.getReplyTo() );
    }

    @Test
    void testRFC822AddressMail()
    {
        runFunction( "/test/send-test.js", "rfc822AddressMail" );

        final SendMailParams message = this.actualMessage;

        assertEquals( "test subject", message.getSubject() );
        assertEquals( "test body", message.getBody() );
        assertEquals( List.of( "From Bar <from@bar.com>", "From Foo <from@foo.com>" ), message.getFrom() );
        assertEquals( List.of( "To Bar <to@bar.com>", "To Foo <to@foo.com>" ), message.getTo() );
    }

    @Test
    void testFailSendMail()
        throws Exception
    {
        final MailService mailService = new MailService()
        {
            @Override
            public void send( final SendMailParams message )
            {
                throw new RuntimeException( "Error sending mail" );
            }

            @Override
            public String getDefaultFromEmail()
            {
                return null;
            }
        };
        addService( MailService.class, mailService );

        runFunction( "/test/send-test.js", "failSendMail" );

        assertNull( this.actualMessage );
    }

    @Test
    void testMailWithContentType()
    {
        runFunction( "/test/send-test.js", "sendMailWithContentType" );

        final SendMailParams message = this.actualMessage;

        assertEquals( "test subject", message.getSubject() );
        assertEquals( "test body", message.getBody() );
        assertEquals( List.of( "from@bar.com" ), message.getFrom() );
        assertEquals( List.of( "to@bar.com" ), message.getTo() );
        assertEquals( "text/html", message.getContentType() );
    }

    @Test
    void testFailMissingFrom()
        throws Exception
    {
        final MailService mailService = new MailService()
        {
            @Override
            public void send( final SendMailParams message )
            {
                throw new RuntimeException( "Error sending mail" );
            }

            @Override
            public String getDefaultFromEmail()
            {
                return null;
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
    void testFailMissingTo()
        throws Exception
    {
        addService( MailService.class, new MailService()
        {
            @Override
            public void send( final SendMailParams message )
            {
                throw new RuntimeException( "Error sending mail" );
            }

            @Override
            public String getDefaultFromEmail()
            {
                return null;
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
    void testMailWithAttachments()
        throws Exception
    {
        runFunction( "/test/send-test.js", "sendWithAttachments" );

        final SendMailParams message = this.actualMessage;

        assertEquals( 2, message.getAttachments().size() );

        final MailAttachment attachment1 = message.getAttachments().get( 0 );
        assertEquals( "image.png", attachment1.getFileName() );
        assertEquals( "image/png", attachment1.getMimeType() );

        assertEquals( "image data", CharStreams.toString(
            new InputStreamReader( ( attachment1.getData() ).openBufferedStream(), StandardCharsets.UTF_8 ) ) );
        Map<String, String> headersAttachment1 = attachment1.getHeaders();
        assertEquals( "<myimg>", headersAttachment1.get( "Content-ID" ) );

        final MailAttachment attachment2 = message.getAttachments().get( 1 );
        assertEquals( "text.txt", attachment2.getFileName() );
    }

    public ByteSource createByteSource( final String value )
    {
        return ByteSource.wrap( value.getBytes() );
    }

    private String getHeader( List<MailHeader> headers, String header )
    {
        return headers.stream().filter( h -> h.getKey().equalsIgnoreCase( header ) ).map( MailHeader::getValue ).findFirst().orElse( null );
    }
}
