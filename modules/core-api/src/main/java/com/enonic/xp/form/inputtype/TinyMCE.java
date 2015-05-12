package com.enonic.xp.form.inputtype;


import org.apache.commons.lang.StringUtils;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.BreaksRequiredContractException;
import com.enonic.xp.form.InvalidTypeException;

final class TinyMCE
    extends InputType
{
    TinyMCE()
    {
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
        if ( !ValueTypes.HTML_PART.equals( property.getType() ) )
        {
            throw new InvalidTypeException( property, ValueTypes.HTML_PART );
        }
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newHtmlPart( value );
    }
}
