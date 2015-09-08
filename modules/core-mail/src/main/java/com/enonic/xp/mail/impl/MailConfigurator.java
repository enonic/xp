package com.enonic.xp.mail.impl;


import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import com.enonic.xp.convert.Converters;

final class MailConfigurator
{
    private final Map<String, String> config;

    public MailConfigurator( final Map<String, String> config )
    {
        this.config = config;
    }

    public Session configure()
    {
        final Properties properties = new Properties();

        properties.put( "mail.transport.protocol", "smtp" );
        properties.put( "mail.smtp.host", getHost() );
        properties.put( "mail.smtp.port", Integer.toString( getPort() ) );

        final boolean auth = getAuth();
        properties.put( "mail.smtp.auth", Boolean.toString( auth ) );

        return Session.getInstance( properties, auth ? createAuthenticator() : null );
    }

    private String getHost()
    {
        return Converters.convertOrDefault( config.get( "smtpHost" ), String.class, "localhost" );
    }

    private int getPort()
    {
        return Converters.convertOrDefault( config.get( "smtpPort" ), Integer.class, 25 );
    }

    private boolean getAuth()
    {
        return Converters.convertOrDefault( config.get( "smtpAuth" ), Boolean.class, false );
    }

    private String getUser()
    {
        return Converters.convertOrDefault( config.get( "smtpUser" ), String.class, "" );
    }

    private String getPassword()
    {
        return Converters.convertOrDefault( config.get( "smtpPassword" ), String.class, "" );
    }

    private Authenticator createAuthenticator()
    {
        return new javax.mail.Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication( getUser(), getPassword() );
            }
        };
    }
}
