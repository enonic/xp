package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

final class Long
    extends InputType
{
    public Long()
    {
        super( InputTypeName.LONG, null, false );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
        validateNotNull( property, property.getLong() );
    }

    @Override
    public void checkTypeValidity( final Property property )
    {
        validateType( property, ValueTypes.LONG );
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newLong( ValueTypes.LONG.convert( value ) );
    }
}
