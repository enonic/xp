package com.enonic.xp.resource;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;

@PublicApi
public final class UpdateDynamicCmsParams
{
    private final ApplicationKey key;

    private final String resource;

    private UpdateDynamicCmsParams( final Builder builder )
    {
        this.key = builder.key;
        this.resource = builder.resource;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ApplicationKey getKey()
    {
        return key;
    }

    public String getResource()
    {
        return resource;
    }

    public static final class Builder
    {
        private ApplicationKey key;

        private String resource;

        private Builder()
        {
        }

        public Builder key( final ApplicationKey key )
        {
            this.key = key;
            return this;
        }

        public Builder resource( final String resource )
        {
            this.resource = resource;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( key, "key is required" );
            Objects.requireNonNull( resource, "resource is required" );
        }

        public UpdateDynamicCmsParams build()
        {
            validate();
            return new UpdateDynamicCmsParams( this );
        }
    }
}


