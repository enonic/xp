package com.enonic.xp.repo.impl.vacuum;

import com.enonic.xp.vacuum.VacuumTaskResult;
import com.enonic.xp.vfs.VirtualFile;

public abstract class AbstractVacuumTask
    implements VacuumTask
{

    protected boolean include( final VirtualFile file )
    {
        //return System.currentTimeMillis() - file.getLastModified() > ageThreshold;
        return false;
    }

    protected void report( final EntryState entryState, final VacuumTaskResult.Builder result )
    {
        result.processed();

        switch ( entryState )
        {
            case FAILED:
                result.failed();
                break;
            case IN_USE:
                result.inUse();
                break;
            case NOT_FOUND:
                result.failed();
                break;
            case NOT_IN_USE:
                result.deleted();
                break;
        }
    }

}
