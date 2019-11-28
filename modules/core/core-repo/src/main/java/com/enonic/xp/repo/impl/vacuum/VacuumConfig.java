package com.enonic.xp.repo.impl.vacuum;

public @interface VacuumConfig
{
    String ageThreshold() default "P21D"; //21 days
}
