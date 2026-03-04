package com.enonic.xp.mail;

public interface MailService
{
    void send( SendMailParams message );

    String getDefaultFromEmail();
}
