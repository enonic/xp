package com.enonic.wem.api.content.type.formitem.inputtype;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.datatype.InvalidValueTypeException;
import com.enonic.wem.api.content.type.formitem.BreaksRequiredContractException;
import com.enonic.wem.api.content.type.formitem.InvalidValueException;

public class Attachment
    extends BaseInputType
{
    Attachment()
    {
        super( "attachment" );
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
        if ( !data.hasValue() )
        {
            throw new BreaksRequiredContractException( data, this );
        }
    }
}

