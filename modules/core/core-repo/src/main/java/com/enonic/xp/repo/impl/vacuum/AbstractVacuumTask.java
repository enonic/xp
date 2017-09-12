package com.enonic.xp.repo.impl.vacuum;

import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.vacuum.VacuumTaskResult;

public abstract class AbstractVacuumTask
    implements VacuumTask
{
    protected boolean includeRecord( final BlobRecord record, final long ageThreshold )
    {
        return System.currentTimeMillis() - record.lastModified() >= ageThreshold;
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
