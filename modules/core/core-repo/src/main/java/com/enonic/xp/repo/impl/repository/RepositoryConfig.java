package com.enonic.xp.repo.impl.repository;

public @interface RepositoryConfig
{
    boolean auditlog_enabled() default true;
}
