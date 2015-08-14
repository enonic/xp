package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

final class RadioButtonsType
    extends InputTypeBase
{
    public final static RadioButtonsType INSTANCE = new RadioButtonsType();

    private RadioButtonsType()
    {
        super( InputTypeName.RADIO_BUTTONS );
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        return Value.newString( value );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.STRING );

        final String valueAsString = property.getString();
        final boolean flag = ( valueAsString != null ) && config.hasValue( "option.value", valueAsString );
        validateValue( property, flag, "Value is not a valid option" );
    }
}
