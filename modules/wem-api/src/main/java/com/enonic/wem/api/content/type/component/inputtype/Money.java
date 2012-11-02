package com.enonic.wem.api.content.type.component.inputtype;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.datatype.InvalidValueTypeException;
import com.enonic.wem.api.content.type.component.BreaksRequiredContractException;
import com.enonic.wem.api.content.type.component.InvalidValueException;

public class Money
    extends BaseInputType
{
    public Money()
    {
    }

    @Override
    public void checkValidity( final Data data )
        throws InvalidValueTypeException, InvalidValueException
    {
        DataTypes.DECIMAL_NUMBER.checkValidity( data );
    }

    @Override
    public void ensureType( final Data data )
    {
        DataTypes.DECIMAL_NUMBER.ensureType( data );
    }

    @Override
    public void checkBreaksRequiredContract( final Data data )
    {
        final String stringValue = (String) data.getValue();
        if ( StringUtils.isBlank( stringValue ) )
        {
            throw new BreaksRequiredContractException( data, this );
        }
    }
}

