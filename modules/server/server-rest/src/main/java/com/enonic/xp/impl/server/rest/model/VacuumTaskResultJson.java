package com.enonic.xp.impl.server.rest.model;

public class VacuumTaskResultJson
{
    private final long processed;

    private final long deleted;

    private final long inUse;

    private final long failed;

    private final String taskName;

    private VacuumTaskResultJson( final Builder builder )
    {
        processed = builder.processed;
        deleted = builder.deleted;
        inUse = builder.inUse;
        failed = builder.failed;
        this.taskName = builder.taskName;
    }

    @SuppressWarnings("unused")
    public long getProcessed()
    {
        return processed;
    }

    @SuppressWarnings("unused")
    public long getDeleted()
    {
        return deleted;
    }

    @SuppressWarnings("unused")
    public long getInUse()
    {
        return inUse;
    }

    @SuppressWarnings("unused")
    public long getFailed()
    {
        return failed;
    }

    @SuppressWarnings("unused")
    public String getTaskName()
    {
        return taskName;
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

        private String taskName;

        private Builder()
        {
        }

        Builder processed( final long val )
        {
            processed = val;
            return this;
        }

        Builder deleted( final long val )
        {
            deleted = val;
            return this;
        }

        Builder inUse( final long val )
        {
            inUse = val;
            return this;
        }

        Builder failed( final long val )
        {
            failed = val;
            return this;
        }

        Builder taskName( final String val )
        {
            this.taskName = val;
            return this;
        }

        public VacuumTaskResultJson build()
        {
            return new VacuumTaskResultJson( this );
        }
    }
}
