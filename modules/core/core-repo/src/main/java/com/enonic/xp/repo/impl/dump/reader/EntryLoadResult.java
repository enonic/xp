package com.enonic.xp.repo.impl.dump.reader;

import java.util.List;

import com.google.common.collect.Lists;

public class EntryLoadResult
{
    private final long versions;

    private final List<EntryLoadError> errors;

    private EntryLoadResult( final Builder builder )
    {
        this.versions = builder.versions;
        this.errors = builder.errors;
    }

    public long getVersions()
    {
        return versions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public List<EntryLoadError> getErrors()
    {
        return errors;
    }

    public static final class Builder
    {
        private long versions = 0L;

        private final List<EntryLoadError> errors = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder addedVersion()
        {
            this.versions++;
            return this;
        }

        public Builder error( EntryLoadError error )
        {
            this.errors.add( error );
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
