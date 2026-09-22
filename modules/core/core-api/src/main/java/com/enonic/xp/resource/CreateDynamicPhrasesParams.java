package com.enonic.xp.resource;

import com.enonic.xp.app.ApplicationKey;

import static java.util.Objects.requireNonNull;

/**
 * Parameters for creating an i18n phrases resource ({@code cms/i18n/phrases/<name>.properties}) of an application.
 */
public final class CreateDynamicPhrasesParams
{
    private final ApplicationKey key;

    private final String name;

    private final String resource;

    private CreateDynamicPhrasesParams( final Builder builder )
    {
        this.key = builder.key;
        this.name = builder.name;
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

    /**
     * Name of the phrases file without the {@code .properties} extension, e.g. {@code phrases} or {@code phrases_en}.
     */
    public String getName()
    {
        return name;
    }

    public String getResource()
    {
        return resource;
    }

    public static final class Builder
    {
        private ApplicationKey key;

        private String name;

        private String resource;

        private Builder()
        {
        }

        public Builder key( final ApplicationKey key )
        {
            this.key = key;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
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
            requireNonNull( name, "name is required" );
            requireNonNull( resource, "resource is required" );
        }

        public CreateDynamicPhrasesParams build()
        {
            validate();
            return new CreateDynamicPhrasesParams( this );
        }
    }
}
