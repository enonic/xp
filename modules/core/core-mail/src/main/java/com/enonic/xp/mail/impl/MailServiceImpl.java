package com.enonic.xp.mail.impl;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import com.enonic.xp.mail.MailException;
import com.enonic.xp.mail.MailMessage;
import com.enonic.xp.mail.MailService;
import com.enonic.xp.mail.SendMailParams;

@Component(immediate = true, configurationPid = "com.enonic.xp.mail")
public final class MailServiceImpl
    implements MailService
{
    private volatile Session session;

    private volatile String defaultFromEmail;

    @Activate
    @Modified
    public void activate( final MailConfig config )
    {
        this.defaultFromEmail = config.defaultFromEmail();

        final Properties properties = new Properties();

        properties.put( "mail.transport.protocol", "smtp" );
        properties.put( "mail.smtp.host", config.smtpHost() );
        properties.put( "mail.smtp.port", config.smtpPort() );
        properties.put( "mail.smtp.starttls.enable", config.smtpTLS() );

        final boolean auth = config.smtpAuth();
        properties.put( "mail.smtp.auth", Boolean.toString( auth ) );

        final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader( Session.class.getClassLoader() );
        try
        {
            this.session =
                Session.getInstance( properties, auth ? new PasswordAuthenticator( config.smtpUser(), config.smtpPassword() ) : null );
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( oldLoader );
        }
    }

    @Override
    public void send( final MailMessage message )
    {
        try
        {
            final MimeMessage mimeMessage = new MimeMessage( session );
            message.compose( mimeMessage );
            doSend( mimeMessage );
        }
        catch ( final Exception e )
        {
            throw handleException( e );
        }
    }

    @Override
    public void send( final SendMailParams params )
    {
        try
        {
            final MimeMessage message = new MimeMessageConverter( defaultFromEmail, session ).convert( params );
            doSend( message );
        }
        catch ( Exception e )
        {
            throw handleException( e );
        }
    }

    @Override
    public String getDefaultFromEmail()
    {
        return defaultFromEmail;
    }

    private static MailException handleException( final Exception e )
    {
        return new MailException( e.getMessage(), e );
    }

    private void doSend( final MimeMessage message )
        throws MessagingException
    {
        final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader( Session.class.getClassLoader() );
        try (Transport transport = this.session.getTransport())
        {
            transport.connect();
            transport.sendMessage( message, message.getAllRecipients() );
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( oldLoader );
        }
    }

    private static class PasswordAuthenticator
        extends Authenticator
    {
        final PasswordAuthentication passwordAuthentication;

        PasswordAuthenticator( final String user, final String password )
        {
            this.passwordAuthentication = new PasswordAuthentication( user, password );
        }

        public PasswordAuthentication getPasswordAuthentication()
        {
            return this.passwordAuthentication;
        }
    }
}
