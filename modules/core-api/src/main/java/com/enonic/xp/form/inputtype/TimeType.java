package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

final class TimeType
    extends InputType
{
    public final static TimeType INSTANCE = new TimeType();

    private TimeType()
    {
        super( InputTypeName.TIME );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
        validateNotBlank( property );
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newLocalTime( ValueTypes.LOCAL_TIME.convert( value ) );
    }

    @Override
    public void checkValidity( final InputTypeConfig config, final Property property )
    {
        validateType( property, ValueTypes.LOCAL_TIME );
    }
}

