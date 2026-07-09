package com.enonic.xp.schema;

import com.enonic.xp.macro.MacroKey;

import static java.util.Objects.requireNonNull;


public final class CreateMacroParams
{
    private final MacroKey key;

    private final String resource;

    private CreateMacroParams( final Builder builder )
    {
        this.key = builder.key;
        this.resource = builder.resource;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public MacroKey getKey()
    {
        return key;
    }

    public String getResource()
    {
        return resource;
    }

    public static final class Builder
    {
        private MacroKey key;

        private String resource;

        private Builder()
        {
        }

        public Builder key( final MacroKey key )
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

        public CreateMacroParams build()
        {
            validate();
            return new CreateMacroParams( this );
        }
    }
}
