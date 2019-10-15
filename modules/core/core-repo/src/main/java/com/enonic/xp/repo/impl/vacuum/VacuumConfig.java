package com.enonic.xp.repo.impl.vacuum;

public @interface VacuumConfig
{
    long ageThresholdMinutes() default 21 * 24 * 60; //21 days
}
