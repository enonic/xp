package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.util.GenericValue;

final class DateType
    extends InputTypeBase
{
    public static final DateType INSTANCE = new DateType();

    private DateType()
    {
        super( InputTypeName.DATE );
    }

    @Override
    public Value createValue( final Value value, final GenericValue config )
    {
        return ValueFactory.newLocalDate( value.asLocalDate() );
    }


    @Override
    public void validate( final Property property, final GenericValue config )
    {
        validateType( property, ValueTypes.LOCAL_DATE );
    }
}
