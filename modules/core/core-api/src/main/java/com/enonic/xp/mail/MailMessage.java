package com.enonic.xp.mail;

import jakarta.mail.internet.MimeMessage;

@Deprecated
public interface MailMessage
{
    void compose( MimeMessage message )
        throws Exception;
}
