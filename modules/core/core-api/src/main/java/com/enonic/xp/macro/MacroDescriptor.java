package com.enonic.xp.macro;

import com.google.common.annotations.Beta;

import com.enonic.xp.resource.ResourceKey;

@Beta
public final class MacroDescriptor
{
    private final static String SITE_MACROS_PREFIX = "site/macros/";

    private final MacroKey key;

    private MacroDescriptor( final Builder builder )
    {
        this.key = builder.key;
    }

    public MacroKey getKey()
    {
        return key;
    }

    public static MacroDescriptor from( final String macroKey )
    {
        return create().key( MacroKey.from( macroKey ) ).build();
    }

    public static MacroDescriptor from( final MacroKey macroKey )
    {
        return create().key( macroKey ).build();
    }

    public ResourceKey toResourceKey()
    {
        return ResourceKey.from( key.getApplicationKey(), SITE_MACROS_PREFIX + key.getName() );
    }

    public static Builder create()
    {
        return new Builder();
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

        public MacroDescriptor build()
        {
            return new MacroDescriptor( this );
        }
    }

}
