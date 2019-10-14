package com.enonic.xp.repo.impl.vacuum;

public @interface VacuumConfig
{
    long threshold_ageMinutes() default 21 * 24 * 60; //21 days
}
