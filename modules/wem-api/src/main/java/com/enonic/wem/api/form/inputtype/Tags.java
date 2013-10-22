package com.enonic.wem.api.form.inputtype;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.InvalidValueTypeException;
import com.enonic.wem.api.form.BreaksRequiredContractException;
import com.enonic.wem.api.form.InvalidValueException;

/**
 * TODO: An array of strings
 */
final class Tags
    extends InputType
{
    Tags()
    {
    }

    @Override
    public void checkValidity( final Property property )
        throws InvalidValueTypeException, InvalidValueException
    {
        // TODO
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {
        final String stringValue = (String) property.getObject();
        if ( StringUtils.isBlank( stringValue ) )
        {
            throw new BreaksRequiredContractException( property, this );
        }
    }

    @Override
    public Value newValue( final String value )
    {
        return new Value.String( value );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }

}

