package com.enonic.xp.mail.session.configurator;


import java.util.Map;

import com.enonic.xp.mail.impl.MailProtocolType;

public class MailSessionConfiguratorFactory
{
    public static MailSessionConfigurator getMailSessionConfigurator(MailProtocolType mailProtocolType, final Map<String, String> config) {
        if ( mailProtocolType == MailProtocolType.SMTP ) {
            return new SMTPMailSessionConfigurator( config );
        }

        return null;
    }
}
