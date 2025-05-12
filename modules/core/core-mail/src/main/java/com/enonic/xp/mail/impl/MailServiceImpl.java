package com.enonic.xp.mail.impl;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.Address;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;

import com.enonic.xp.core.internal.concurrent.SimpleExecutor;
import com.enonic.xp.mail.MailException;
import com.enonic.xp.mail.MailService;
import com.enonic.xp.mail.SendMailParams;

@Component(immediate = true, configurationPid = "com.enonic.xp.mail")
public final class MailServiceImpl
    implements MailService
{
    private static final Logger LOG = LoggerFactory.getLogger( MailServiceImpl.class );

    private final SimpleExecutor simpleExecutor;

    private Session session;

    private volatile String defaultFromEmail;

    public MailServiceImpl()
    {
        this.simpleExecutor = new SimpleExecutor( Executors::newCachedThreadPool, "mail-service-executor-thread-%d",
                                                  e -> LOG.error( "Message sending failed", e ) );
    }

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
            this.session = Session.getInstance( properties, auth ? createAuthenticator( config ) : null );
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( oldLoader );
        }
    }

    @Deactivate
    public void deactivate()
    {
        simpleExecutor.shutdownAndAwaitTermination( Duration.ofSeconds( 5 ), neverCommenced -> LOG.warn( "Not all messages were sent" ) );
    }

    @Override
    public void send( final SendMailParams params )
    {
        try
        {
            MimeMessage message = new MimeMessageConverter( defaultFromEmail, session ).convert( params );
            simpleExecutor.execute( () -> {
                try
                {
                    doSend( message );
                }
                catch ( Exception e )
                {
                    throw new RuntimeException( e );
                }
            } );
        }
        catch ( final Exception e )
        {
            throw handleException( e );
        }
    }

    @Override
    public String getDefaultFromEmail()
    {
        return defaultFromEmail;
    }

    private MimeMessage newMessage()
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
        final Address[] to = message.getAllRecipients();
        final Transport transport = this.session.getTransport();

        final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader( Session.class.getClassLoader() );
        try (transport)
        {
            transport.connect();
            transport.sendMessage( message, to );
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( oldLoader );
        }
    }

    private Authenticator createAuthenticator( final MailConfig config )
    {
        return new Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication( config.smtpUser(), config.smtpPassword() );
            }
        };
    }
}
