package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InvalidValueException;

final class ComboBoxType
    extends InputType
{
    public final static ComboBoxType INSTANCE = new ComboBoxType();

    private ComboBoxType()
    {
        super( InputTypeName.COMBOBOX );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
        validateNotBlank( property );
    }

    @Override
    public void checkTypeValidity( final Property property )
    {
        validateType( property, ValueTypes.STRING );
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newString( value );
    }

    @Override
    public void checkValidity( final InputTypeConfig config, final Property property )
    {
        final String valueAsString = property.getString();
        if ( valueAsString != null && !config.hasValue( "option.value", valueAsString ) )
        {
            throw new InvalidValueException( property, "Value is not a valid option" );
        }
    }
}
