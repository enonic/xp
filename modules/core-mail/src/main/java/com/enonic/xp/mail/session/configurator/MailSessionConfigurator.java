package com.enonic.xp.mail.session.configurator;

import java.util.Map;

import javax.mail.Session;

public abstract class MailSessionConfigurator
{
    protected final MailSessionConfigMap config;

    MailSessionConfigurator( final Map<String, String> config ) {
        this.config = new MailSessionConfigMap( config );
    }

    public abstract Session configure();
}
