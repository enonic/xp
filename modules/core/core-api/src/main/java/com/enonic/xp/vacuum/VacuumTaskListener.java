package com.enonic.xp.vacuum;

public interface VacuumTaskListener
{
    void total( long total );

    void taskExecuted();
}
