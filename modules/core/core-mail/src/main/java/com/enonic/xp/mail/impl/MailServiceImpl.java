package com.enonic.xp.mail.impl;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
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
    public void activate( final MailConfig config )
        throws Exception
    {
        final Properties properties = new Properties();

        properties.put( "mail.transport.protocol", "smtp" );
        properties.put( "mail.smtp.host", config.smtpHost() );
        properties.put( "mail.smtp.port", config.smtpPort() );
        properties.put( "mail.smtp.starttls.enable", config.smtpTLS() );

        final boolean auth = config.smtpAuth();
        properties.put( "mail.smtp.auth", Boolean.toString( auth ) );

        this.session = Session.getInstance( properties, auth ? createAuthenticator( config ) : null );
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
        final Transport transport = this.session.getTransport();
        transport.connect();

        try
        {
            transport.sendMessage( message, to );
        }
        finally
        {
            transport.close();
        }
    }

    private Authenticator createAuthenticator( final MailConfig config )
    {
        return new javax.mail.Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication( config.smtpUser(), config.smtpPassword() );
            }
        };
    }
}
