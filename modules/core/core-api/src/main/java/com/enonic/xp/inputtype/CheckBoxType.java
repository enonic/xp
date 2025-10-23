package com.enonic.xp.inputtype;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

@PublicApi
final class CheckBoxType
    extends InputTypeBase
{
    public static final CheckBoxType INSTANCE = new CheckBoxType();

    private CheckBoxType()
    {
        super( InputTypeName.CHECK_BOX );
    }

    @Override
    public Value createValue( final Value value, final GenericValue config )
    {
        return ValueFactory.newBoolean( value.asBoolean() );
    }

    @Override
    public void validate( final Property property, final GenericValue config )
    {
        validateType( property, ValueTypes.BOOLEAN );
    }
}
