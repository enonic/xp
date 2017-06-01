package com.enonic.xp.repo.impl.dump;

import java.time.Duration;

public class ProgressReporter
{
    Duration writeMetaData = Duration.ZERO;

    Duration creatingDumpEntries = Duration.ZERO;

    Duration writeVersion = Duration.ZERO;

    Duration writeBinary = Duration.ZERO;

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

    @Override
    public String toString()
    {
        return "ProgressReporter{" +
            "writeMetaData=" + writeMetaData +
            ", creatingDumpEntries=" + creatingDumpEntries +
            ", writeVersion=" + writeVersion +
            ", writeBinary=" + writeBinary +
            '}';
    }
}
