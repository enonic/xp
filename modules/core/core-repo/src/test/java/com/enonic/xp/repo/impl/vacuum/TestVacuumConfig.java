package com.enonic.xp.repo.impl.vacuum;

import java.lang.annotation.Annotation;
import java.time.Duration;

public class TestVacuumConfig
    implements VacuumConfig
{
    @Override
    public String ageThreshold()
    {
        return Duration.ZERO.toString();
    }

    @Override
    public Class<? extends Annotation> annotationType()
    {
        return null;
    }
}
