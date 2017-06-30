package com.enonic.xp.repo.impl.dump.reader;

import java.util.List;

import com.google.common.collect.Lists;

public class EntriesLoadResult
{
    private final long entriesLoaded;

    private final List<EntryLoadError> errors;

    private EntriesLoadResult( final Builder builder )
    {
        entriesLoaded = builder.entriesLoaded;
        errors = builder.errors;
    }

    public long getEntriesLoaded()
    {
        return entriesLoaded;
    }

    public List<EntryLoadError> getErrors()
    {
        return errors;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private long entriesLoaded;

        private final List<EntryLoadError> errors = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder entriesLoaded( final long val )
        {
            entriesLoaded = val;
            return this;
        }

        public Builder errors( final List<EntryLoadError> val )
        {
            errors.addAll( val );
            return this;
        }

        public EntriesLoadResult build()
        {
            return new EntriesLoadResult( this );
        }
    }
}
