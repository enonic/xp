package com.enonic.xp.form.inputtype;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

@Beta
final class CheckboxType
    extends InputType
{
    public final static CheckboxType INSTANCE = new CheckboxType();

    private CheckboxType()
    {
        super( InputTypeName.CHECKBOX );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
    }

    @Override
    public void checkTypeValidity( final Property property )
    {
        validateType( property, ValueTypes.BOOLEAN );
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newBoolean( ValueTypes.BOOLEAN.convert( value ) );
    }
}
