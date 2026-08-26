package com.enonic.xp.resource;

import com.enonic.xp.app.ApplicationKey;

import static java.util.Objects.requireNonNull;

/**
 * Parameters for creating or updating the style descriptor of an application, see {@link DynamicSchemaService#createOrUpdateStyles}.
 */
public final class CreateOrUpdateStylesParams
{
    private final ApplicationKey key;

    private final String resource;

    private CreateOrUpdateStylesParams( final Builder builder )
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
            requireNonNull( key, "key is required" );
            requireNonNull( resource, "resource is required" );
        }

        public CreateOrUpdateStylesParams build()
        {
            validate();
            return new CreateOrUpdateStylesParams( this );
        }
    }
}
