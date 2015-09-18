package com.enonic.xp.mail;

import javax.mail.internet.MimeMessage;

public interface MailMessage
{
    void compose( MimeMessage message )
        throws Exception;
}
