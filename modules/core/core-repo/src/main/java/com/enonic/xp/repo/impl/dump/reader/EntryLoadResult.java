package com.enonic.xp.repo.impl.dump.reader;

import java.util.List;

import com.google.common.collect.Lists;

public class EntryLoadResult
{
    private final long successful;

    private final List<EntryLoadError> errors;

    private EntryLoadResult( final Builder builder )
    {
        this.successful = builder.successful;
        this.errors = builder.errors;
    }

    public long getSuccessful()
    {
        return successful;
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
        private long successful = 0L;

        private final List<EntryLoadError> errors = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder successful()
        {
            this.successful++;
            return this;
        }

        public Builder error( EntryLoadError error )
        {
            this.errors.add( error );
            return this;
        }

        public EntryLoadResult build()
        {
            return new EntryLoadResult( this );
        }
    }
}
