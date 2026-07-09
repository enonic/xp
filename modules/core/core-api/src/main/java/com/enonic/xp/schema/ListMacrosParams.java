package com.enonic.xp.schema;

import com.enonic.xp.app.ApplicationKey;

import static java.util.Objects.requireNonNull;


public final class ListMacrosParams
{
    private final ApplicationKey key;

    private ListMacrosParams( final Builder builder )
    {
        this.key = builder.key;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ApplicationKey getKey()
    {
        return key;
    }

    public static final class Builder
    {
        private ApplicationKey key;

        private Builder()
        {
        }

        public Builder applicationKey( final ApplicationKey key )
        {
            this.key = key;
            return this;
        }

        private void validate()
        {
            requireNonNull( key, "key is required" );
        }

        public ListMacrosParams build()
        {
            validate();
            return new ListMacrosParams( this );
        }
    }
}
