package com.enonic.xp.mail;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface MailService
{
    void send( SendMailParams message );

    String getDefaultFromEmail();
}
