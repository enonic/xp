package com.enonic.xp.form.inputtype;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.form.BreaksRequiredContractException;
import com.enonic.xp.form.InvalidTypeException;

final class SiteConfigurator
    extends InputType
{
    public SiteConfigurator()
    {
        super( "SiteConfigurator", null, false );
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
    public void checkTypeValidity( final Property property )
        throws InvalidTypeException
    {

    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newString( value );
    }
}
