package com.enonic.xp.mail.impl;

public @interface MailConfig
{
    String smtpHost() default "localhost";

    int smtpPort() default 25;

    boolean smtpAuth() default false;

    String smtpUser() default "";

    String smtpPassword() default "";

    boolean smtpTLS() default false;
}
