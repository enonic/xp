package com.enonic.xp.inputtype;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

@PublicApi
final class InstantType
    extends InputTypeBase
{
    public static final InstantType INSTANCE = new InstantType();

    private InstantType()
    {
        super( InputTypeName.INSTANT );
    }

    @Override
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        return ValueFactory.newDateTime( value.asInstant() );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.DATE_TIME );
    }
}
