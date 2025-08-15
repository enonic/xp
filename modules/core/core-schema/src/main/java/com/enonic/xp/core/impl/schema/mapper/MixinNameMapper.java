package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.enonic.xp.schema.mixin.MixinName;

public abstract class MixinNameMapper
{
    @JsonCreator
    public static MixinName from( String value )
    {
        return MixinName.from( value );
    }
}
