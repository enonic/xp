package com.enonic.xp.app;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
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
            Objects.requireNonNull( key, "key is required" );
        }

        public CreateVirtualApplicationParams build()
        {
            validate();
            return new CreateVirtualApplicationParams( this );
        }
    }
}
