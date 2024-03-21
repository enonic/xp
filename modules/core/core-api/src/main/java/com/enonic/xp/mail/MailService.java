package com.enonic.xp.mail;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface MailService
{
    @Deprecated
    void send( MailMessage message );

    void send( SendMailParams message );

    String getDefaultFromEmail();
}
