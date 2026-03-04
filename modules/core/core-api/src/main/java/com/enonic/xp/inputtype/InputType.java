package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.util.GenericValue;


public interface InputType
{
    InputTypeName getName();

    Value createValue( Value value, GenericValue config );

    void validate( Property property, GenericValue config );
}
