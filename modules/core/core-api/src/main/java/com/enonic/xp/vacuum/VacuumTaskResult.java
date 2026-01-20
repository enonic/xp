package com.enonic.xp.vacuum;

public final class VacuumTaskResult
{
    private final long processed;

    private final long deleted;

    private final long inUse;

    private final long failed;

    private final long skipped;

    private final String taskName;

    private VacuumTaskResult( final Builder builder )
    {
        processed = builder.processed;
        deleted = builder.deleted;
        inUse = builder.inUse;
        failed = builder.failed;
        skipped = builder.skipped;
        taskName = builder.taskName;
    }

    public long getProcessed()
    {
        return processed;
    }

    public long getDeleted()
    {
        return deleted;
    }

    public long getInUse()
    {
        return inUse;
    }

    public long getFailed()
    {
        return failed;
    }

    /**
     * Returns the number of versions that were skipped during vacuum.
     *
     * @return the number of skipped versions
     */
    public long getSkipped()
    {
        return skipped;
    }

    public String getTaskName()
    {
        return taskName;
    }

    @Override
    public String toString()
    {
        return "VacuumTaskResult{" + "processed=" + processed + ", deleted=" + deleted + ", failed=" + failed + ", skipped=" + skipped + '}';
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private long processed;

        private long deleted;

        private long inUse;

        private long failed;

        private long skipped;

        private String taskName;

        private Builder()
        {
        }

        public Builder processed()
        {
            processed++;
            return this;
        }

        public Builder deleted()
        {
            deleted++;
            return this;
        }


        public Builder inUse()
        {
            inUse++;
            return this;
        }

        public Builder failed()
        {
            failed++;
            return this;
        }

        public Builder skipped()
        {
            skipped++;
            return this;
        }

        public Builder taskName( final String val )
        {
            this.taskName = val;
            return this;
        }

        public VacuumTaskResult build()
        {
            return new VacuumTaskResult( this );
        }
    }


}
