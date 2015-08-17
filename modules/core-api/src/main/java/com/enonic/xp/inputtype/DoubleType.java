package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

final class DoubleType
    extends InputTypeBase
{
    public final static DoubleType INSTANCE = new DoubleType();

    private DoubleType()
    {
        super( InputTypeName.DOUBLE );
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        return ValueFactory.newDouble( ValueTypes.DOUBLE.convert( value ) );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.DOUBLE );
    }
}
