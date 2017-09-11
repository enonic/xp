package com.enonic.xp.vacuum;

public class VacuumTaskResult
{
    private final long processed;

    private final long ignoredByTimestamp;

    private final long deleted;

    private final long found;

    private final long failed;

    private VacuumTaskResult( final Builder builder )
    {
        processed = builder.processed;
        ignoredByTimestamp = builder.ignoredByTimestamp;
        deleted = builder.deleted;
        found = builder.found;
        failed = builder.failed;
    }

    public long getProcessed()
    {
        return processed;
    }

    public long getIgnoredByTimestamp()
    {
        return ignoredByTimestamp;
    }

    public long getDeleted()
    {
        return deleted;
    }

    public long getFound()
    {
        return found;
    }

    public long getFailed()
    {
        return failed;
    }

    @Override
    public String toString()
    {
        return "VacuumTaskResult{" + "processed=" + processed + ", ignoredByTimestamp=" + ignoredByTimestamp + ", deleted=" + deleted +
            ", found=" + found + ", failed=" + failed + '}';
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private long processed;

        private long ignoredByTimestamp;

        private long deleted;

        private long found;

        private long failed;

        private Builder()
        {
        }

        public Builder processed()
        {
            processed++;
            return this;
        }

        public Builder ignoredByTimestamp()
        {
            ignoredByTimestamp++;
            return this;
        }

        public Builder deleted()
        {
            deleted++;
            return this;
        }


        public Builder inUse()
        {
            found++;
            return this;
        }

        public Builder failed()
        {
            failed++;
            return this;
        }

        public VacuumTaskResult build()
        {
            return new VacuumTaskResult( this );
        }
    }


}
