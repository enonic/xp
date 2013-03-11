package com.enonic.wem.api.content.schema.content.form.inputtype;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.data.type.InvalidValueTypeException;
import com.enonic.wem.api.content.schema.content.form.BreaksRequiredContractException;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

import static com.enonic.wem.api.content.data.type.DataTool.checkDataType;

public class Address
    extends BaseInputType
{
    public Address()
    {
    }

    @Override
    public void checkValidity( final Data data )
        throws InvalidValueTypeException, InvalidValueException
    {
        checkDataType( data, "street", DataTypes.TEXT );
        checkDataType( data, "postalCode", DataTypes.TEXT );
        checkDataType( data, "postalPlace", DataTypes.TEXT );
        checkDataType( data, "region", DataTypes.TEXT );
        checkDataType( data, "country", DataTypes.TEXT );
    }

    @Override
    public void checkBreaksRequiredContract( final Data data )
        throws BreaksRequiredContractException
    {

    }


    @Override
    public Value newValue( final String value )
    {
        return Value.newValue().type( DataTypes.SET ).value( value ).build();
    }
}

