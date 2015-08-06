package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

final class Date
    extends InputType
{
    public Date()
    {
        super( InputTypeName.DATE, DateConfig.class, false );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
        validateNotBlank( property );
    }

    @Override
    public void checkTypeValidity( final Property property )
    {
        validateType( property, ValueTypes.LOCAL_DATE );
    }

    @Override
    public InputTypeConfigSerializer getConfigSerializer()
    {
        return DateConfigSerializer.INSTANCE;
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newLocalDate( ValueTypes.LOCAL_DATE.convert( value ) );
    }
}
