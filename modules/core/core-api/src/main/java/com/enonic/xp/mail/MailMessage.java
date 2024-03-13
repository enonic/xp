package com.enonic.xp.mail;

import javax.mail.internet.MimeMessage;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface MailMessage
{
    @Deprecated
    void compose( MimeMessage message )
        throws Exception;
}
