package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.enonic.xp.inputtype.InputTypeName;

public abstract class InputTypeNameMapper
{
    @JsonCreator
    public static InputTypeName from( String value )
    {
        return InputTypeName.from( value );
    }
}
