package com.enonic.wem.api.content.schema.type.form.inputtype;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.datatype.InvalidValueTypeException;
import com.enonic.wem.api.content.schema.type.form.BreaksRequiredContractException;
import com.enonic.wem.api.content.schema.type.form.InvalidValueException;

public class Attachment
    extends BaseInputType
{
    Attachment()
    {
    }

    @Override
    public void checkValidity( final Data data )
        throws InvalidValueTypeException, InvalidValueException
    {
        DataTypes.BLOB.checkValidity( data );
    }

    @Override
    public void ensureType( final Data data )
    {
        DataTypes.BLOB.ensureType( data );
    }

    @Override
    public void checkBreaksRequiredContract( final Data data )
        throws BreaksRequiredContractException
    {

    }

}

