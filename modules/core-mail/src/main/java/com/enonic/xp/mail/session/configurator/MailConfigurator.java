package com.enonic.xp.mail.session.configurator;


import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class MailConfigurator
{
    private final MailConfigMap config;

    public MailConfigurator( final Map<String, String> config ) {
        this.config = new MailConfigMap( config );
    }

    public Session configure()
    {
        final Properties properties = new Properties();

        properties.put( "mail.transport.protocol", "smtp" );
        properties.put( "mail.smtp.host", getHost() );
        properties.put( "mail.smtp.port", getPort() );

        final boolean auth = getAuth();
        properties.put( "mail.smtp.auth", auth );

        return Session.getInstance( properties, auth ? createAuthenticator() : null );
    }

    private String getHost() {
        return config.getString( "smtpHost", "localhost" );
    }

    private int getPort() {
        return config.getInt( "smtpPort", 25 );
    }

    private boolean getAuth() {
        return config.getBoolean( "smtpAuth", false );
    }

    private String getUser() {
        return config.getString( "smtpUser", "" );
    }

    private String getPassword() {
        return config.getString( "smtpPassword", "" );
    }

    private Authenticator createAuthenticator() {
        return new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(getUser(), getPassword());
            }
        };
    }
}
