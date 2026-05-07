package com.enonic.xp.app;

import static java.util.Objects.requireNonNull;


public final class CreateVirtualApplicationParams
{
    private final ApplicationKey key;

    private CreateVirtualApplicationParams( final Builder builder )
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

        public Builder key( final ApplicationKey key )
        {
            this.key = key;
            return this;
        }

        private void validate()
        {
            requireNonNull( key, "key is required" );
        }

        public CreateVirtualApplicationParams build()
        {
            validate();
            return new CreateVirtualApplicationParams( this );
        }
    }
}
