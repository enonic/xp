package com.enonic.xp.repo.impl.dump.reader;

import java.util.ArrayList;
import java.util.List;

public class EntriesLoadResult
{
    private final long successful;

    private final List<EntryLoadError> errors;

    private EntriesLoadResult( final Builder builder )
    {
        successful = builder.successful;
        errors = builder.errors;
    }

    public long getSuccessful()
    {
        return successful;
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
        private long successful;

        private final List<EntryLoadError> errors = new ArrayList<>();

        private Builder()
        {
        }

        public Builder add( final EntryLoadResult val )
        {
            this.successful += val.getSuccessful();
            this.errors.addAll( val.getErrors() );
            return this;
        }

        public EntriesLoadResult build()
        {
            return new EntriesLoadResult( this );
        }
    }
}
