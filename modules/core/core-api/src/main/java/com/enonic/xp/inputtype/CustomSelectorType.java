package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.util.GenericValue;

final class CustomSelectorType
    extends InputTypeBase
{
    public static final CustomSelectorType INSTANCE = new CustomSelectorType();

    private CustomSelectorType()
    {
        super( InputTypeName.CUSTOM_SELECTOR );
    }

    @Override
    public Value createValue( final Value value, final GenericValue config )
    {
        return ValueFactory.newString( value.asString() );
    }

    @Override
    public void validate( final Property property, final GenericValue config )
    {
        validateType( property, ValueTypes.STRING );
    }
}

