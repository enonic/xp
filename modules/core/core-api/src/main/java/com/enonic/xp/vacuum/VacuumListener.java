package com.enonic.xp.vacuum;

public interface VacuumListener
{
    void vacuumBegin( long taskCount );

    void taskBegin( String task, Long stepCount );

    void stepBegin( String stepName, Long toProcessCount );

    void processed( long count );
}
