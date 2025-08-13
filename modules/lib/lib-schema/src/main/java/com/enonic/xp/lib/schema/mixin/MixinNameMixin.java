package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.enonic.xp.schema.mixin.MixinName;

public abstract class MixinNameMixin
{
    @JsonCreator
    public static MixinName from( final String value )
    {
        return MixinName.from( value );
    }
}
