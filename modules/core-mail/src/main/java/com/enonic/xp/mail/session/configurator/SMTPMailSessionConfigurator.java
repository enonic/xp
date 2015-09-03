package com.enonic.xp.mail.session.configurator;


import java.util.Map;
import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class SMTPMailSessionConfigurator extends MailSessionConfigurator
{
    public SMTPMailSessionConfigurator( final Map<String, String> config ) {
        super(config);
    }

    @Override
    public Session configure()
    {
        final Properties properties= new Properties();
        properties.put( "mail.transport.protocol", "smtp" );
        properties.put( "mail.smtp.host", getHost() );
        properties.put( "mail.smtp.port", getPort() );

        final boolean auth = getAuth();
        properties.put( "mail.smtp.auth", auth );

        if (auth) {
            return Session.getInstance(properties,
                                              new javax.mail.Authenticator() {
                                                  protected PasswordAuthentication getPasswordAuthentication() {
                                                      return new PasswordAuthentication(getUser(), getPassword());
                                                  }
                                              });
        }
        else {
            return Session.getInstance( properties );
        }

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
}
