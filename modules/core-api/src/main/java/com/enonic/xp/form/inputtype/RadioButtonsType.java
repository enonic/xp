package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InvalidValueException;

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
        if ( valueAsString != null && !config.hasValue( "option.value", valueAsString ) )
        {
            throw new InvalidValueException( property, "Value is not a valid option" );
        }
    }
}
