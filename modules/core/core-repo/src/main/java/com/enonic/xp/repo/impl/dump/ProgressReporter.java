package com.enonic.xp.repo.impl.dump;

import java.time.Duration;

public class ProgressReporter
{
    private Duration writeMetaData = Duration.ZERO;

    private Duration creatingDumpEntries = Duration.ZERO;

    private Duration writeVersion = Duration.ZERO;

    private Duration writeBinary = Duration.ZERO;

    private Duration fetchChildren = Duration.ZERO;

    void createdDumpEntry( final long nanoseconds )
    {
        this.creatingDumpEntries = creatingDumpEntries.plusNanos( nanoseconds );
    }

    void writeMetaData( final long nanoseconds )
    {
        this.writeMetaData = writeMetaData.plusNanos( nanoseconds );
    }

    void writeVersion( final long nanoseconds )
    {
        this.writeVersion = writeVersion.plusNanos( nanoseconds );
    }

    void writeBinary( final long nanoseconds )
    {
        this.writeBinary = writeBinary.plusNanos( nanoseconds );
    }

    void fetchChildren( final long nanoseconds )
    {
        this.fetchChildren = fetchChildren.plusNanos( nanoseconds );
    }

    @Override
    public String toString()
    {
        return "ProgressReporter{" +
            "writeMetaData=" + writeMetaData +
            ", creatingDumpEntries=" + creatingDumpEntries +
            ", writeVersion=" + writeVersion +
            ", writeBinary=" + writeBinary +
            ", fetchChildren=" + fetchChildren +
            '}';
    }
}
