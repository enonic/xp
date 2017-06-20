package com.enonic.xp.repo.impl.dump.reader;

public class EntryLoadResult
{
    private long versions;

    private EntryLoadResult( final Builder builder )
    {
        versions = builder.versions;
    }

    public long getVersions()
    {
        return versions;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private long versions = 0L;

        private Builder()
        {
        }

        public Builder addedVersion()
        {
            this.versions++;
            return this;
        }

        public Builder versions( final long val )
        {
            versions = val;
            return this;
        }

        public EntryLoadResult build()
        {
            return new EntryLoadResult( this );
        }
    }
}
