package com.enonic.xp.repo.impl.vacuum;

import java.lang.annotation.Annotation;

public class TestVacuumConfig
    implements VacuumConfig
{
    @Override
    public long ageThresholdMinutes()
    {
        return 0;
    }

    @Override
    public Class<? extends Annotation> annotationType()
    {
        return null;
    }
}
