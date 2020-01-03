package com.enonic.xp.inputtype;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.form.Input;

@PublicApi
public interface InputType
{
    InputTypeName getName();

    @Deprecated
    Value createValue( String value, InputTypeConfig config );

    Value createValue( Value value, InputTypeConfig config );

    Value createDefaultValue( Input input );

    void validate( Property property, InputTypeConfig config );
}
