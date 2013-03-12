package com.enonic.wem.api.content.schema.content.form.inputtype;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.data.type.InvalidValueTypeException;
import com.enonic.wem.api.content.schema.content.form.BreaksRequiredContractException;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

public class Xml
    extends BaseInputType
{
    public Xml()
    {
    }

    @Override
    public void checkValidity( final Data data )
        throws InvalidValueTypeException, InvalidValueException
    {
        DataTypes.XML.checkValidity( data );
    }

    @Override
    public void checkBreaksRequiredContract( final Data data )
        throws BreaksRequiredContractException
    {
        final String stringValue = (String) data.getObject();
        if ( StringUtils.isBlank( stringValue ) )
        {
            throw new BreaksRequiredContractException( data, this );
        }
    }

    @Override
    public Value newValue( final String value )
    {
        return Value.newValue().type( DataTypes.TEXT ).value( value ).build();
    }

}
