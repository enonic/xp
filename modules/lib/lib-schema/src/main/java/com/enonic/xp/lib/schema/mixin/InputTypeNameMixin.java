package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.enonic.xp.inputtype.InputTypeName;

public abstract class InputTypeNameMixin
{
    @JsonCreator
    public static InputTypeName from( final String value )
    {
        return InputTypeName.from( value );
    }
}
