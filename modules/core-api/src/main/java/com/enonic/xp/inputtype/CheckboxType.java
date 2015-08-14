package com.enonic.xp.inputtype;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

@Beta
final class CheckboxType
    extends InputTypeBase
{
    public final static CheckboxType INSTANCE = new CheckboxType();

    private CheckboxType()
    {
        super( InputTypeName.CHECKBOX );
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        return Value.newBoolean( ValueTypes.BOOLEAN.convert( value ) );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.BOOLEAN );
    }
}
