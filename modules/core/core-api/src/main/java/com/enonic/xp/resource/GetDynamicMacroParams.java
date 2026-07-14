package com.enonic.xp.resource;

import com.enonic.xp.macro.MacroKey;

import static java.util.Objects.requireNonNull;


public final class GetDynamicMacroParams
{
    private final MacroKey key;

    private GetDynamicMacroParams( final Builder builder )
    {
        this.key = builder.key;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public MacroKey getKey()
    {
        return key;
    }

    public static final class Builder
    {
        private MacroKey key;

        private Builder()
        {
        }

        public Builder key( final MacroKey key )
        {
            this.key = key;
            return this;
        }

        private void validate()
        {
            requireNonNull( key, "key is required" );
        }

        public GetDynamicMacroParams build()
        {
            validate();
            return new GetDynamicMacroParams( this );
        }
    }
}
