package com.enonic.xp.inputtype;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;

@Beta
public interface InputType
{
    InputTypeName getName();

    Value createValue( String value, InputTypeConfig config );

    Value createDefaultValue( InputTypeDefault defaultConfig );

    void validate( Property property, InputTypeConfig config );
}
