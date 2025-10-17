package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

final class TimeType
    extends InputTypeBase
{
    public static final TimeType INSTANCE = new TimeType();

    private TimeType()
    {
        super( InputTypeName.TIME );
    }

    @Override
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        return ValueFactory.newLocalTime( value.asLocalTime() );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.LOCAL_TIME );
    }
}
