package com.enonic.xp.lib.mail;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

import com.enonic.xp.mail.MailMessage;
import com.enonic.xp.mail.MailService;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class SendMailScriptTest
    extends ScriptTestSupport
{
    private MailMessage actualMessage;

    @Before
    public void setUp()
    {
        final MailService mailService = message -> this.actualMessage = message;
        addService( MailService.class, mailService );
    }

    @Test
    public void testSimpleMail()
        throws Exception
    {
        runTestFunction( "test/send-test.js", "simpleMail" );

        final MimeMessage message = mockCompose( this.actualMessage );

        Assert.assertEquals( "test", message.getSubject() );
    }

    private MimeMessage mockCompose( final MailMessage message )
        throws Exception
    {
        final MimeMessage mimeMessage = new MimeMessage( (Session) null );
        message.compose( mimeMessage );
        return mimeMessage;
    }
}
