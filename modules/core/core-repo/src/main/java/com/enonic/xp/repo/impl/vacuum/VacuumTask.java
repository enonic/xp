package com.enonic.xp.repo.impl.vacuum;

import com.enonic.xp.vacuum.VacuumTaskResult;

public interface VacuumTask
{
    VacuumTaskResult execute( final VacuumTaskParams params );

    int order();

    String name();
}
