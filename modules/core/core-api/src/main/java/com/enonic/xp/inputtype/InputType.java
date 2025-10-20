package com.enonic.xp.inputtype;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;

@PublicApi
public interface InputType
{
    InputTypeName getName();

    Value createValue( Value value, InputTypeConfig config );

    void validate( Property property, InputTypeConfig config );
}
