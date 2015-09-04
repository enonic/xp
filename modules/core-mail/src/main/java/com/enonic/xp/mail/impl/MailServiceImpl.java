package com.enonic.xp.mail.impl;

import java.util.Map;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.mail.MailException;
import com.enonic.xp.mail.MailMessage;
import com.enonic.xp.mail.MailService;

@Component(immediate = true, configurationPid = "com.enonic.xp.mail")
public final class MailServiceImpl
    implements MailService
{
    private Session session;

    @Activate
    public void activate( final Map<String, String> config )
        throws Exception
    {
        session = new MailConfigurator( config ).configure();
    }

    @Override
    public void send( final MailMessage message )
    {
        try
        {
            final MimeMessage mimeMessage = newMessage();
            message.compose( mimeMessage );
            doSend( mimeMessage );
        }
        catch ( final Exception e )
        {
            throw handleException( e );
        }
    }

    private MimeMessage newMessage()
        throws Exception
    {
        return new MimeMessage( this.session );
    }

    private MailException handleException( final Exception e )
    {
        return new MailException( e.getMessage(), e );
    }

    private void doSend( final MimeMessage message )
        throws Exception
    {
        final Address[] to = message.getRecipients( Message.RecipientType.TO );
        this.session.getTransport().sendMessage( message, to );
    }
}
