package com.enonic.wem.api.schema.content.form.inputtype;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.data.type.InvalidValueTypeException;
import com.enonic.wem.api.content.data.type.ValueTypes;
import com.enonic.wem.api.schema.content.form.BreaksRequiredContractException;
import com.enonic.wem.api.schema.content.form.InvalidValueException;

public class ImageUpload
    extends BaseInputType
{
    public ImageUpload()
    {
    }


    @Override
    public void checkValidity( final Property property )
        throws InvalidValueTypeException, InvalidValueException
    {
        ValueTypes.BINARY_ID.checkValidity( property );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {

    }

    @Override
    public Value newValue( final String value )
    {
        return new Value.BinaryId( ValueTypes.BINARY_ID.convert( value ) );
    }

}

