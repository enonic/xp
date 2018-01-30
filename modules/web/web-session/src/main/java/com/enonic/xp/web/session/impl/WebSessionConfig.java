package com.enonic.xp.web.session.impl;

public @interface WebSessionConfig
{
    boolean transactional() default false;

    int retries() default 3;
}
