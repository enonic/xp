package com.enonic.xp.form.inputtype;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.BreaksRequiredContractException;
import com.enonic.xp.form.InvalidTypeException;

final class ComboBox
    extends InputType
{
    public ComboBox()
    {
        super( "ComboBox", ComboBoxConfig.class, true );
    }

    @Override
    public InputTypeConfigJsonSerializer getInputTypeConfigJsonSerializer()
    {
        return ComboBoxConfigJsonSerializer.DEFAULT;
    }

    @Override
    public InputTypeConfigXmlSerializer getInputTypeConfigXmlSerializer()
    {
        return ComboBoxConfigXmlSerializer.DEFAULT;
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {
        final String stringValue = property.getString();
        if ( StringUtils.isBlank( stringValue ) )
        {
            throw new BreaksRequiredContractException( property, this );
        }
    }

    @Override
    public void checkTypeValidity( final Property property )
        throws InvalidTypeException
    {
        if ( !ValueTypes.STRING.equals( property.getType() ) )
        {
            throw new InvalidTypeException( property, ValueTypes.STRING );
        }
    }

    // TODO: This should probably consider config
    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newString( value );
    }
}
