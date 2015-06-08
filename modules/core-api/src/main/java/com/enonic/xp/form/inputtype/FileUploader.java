package com.enonic.xp.form.inputtype;


import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.form.BreaksRequiredContractException;
import com.enonic.xp.form.InvalidTypeException;
import com.enonic.xp.util.Reference;

final class FileUploader
    extends InputType
{
    FileUploader()
    {

    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {

    }

    @Override
    public void checkTypeValidity( final Property property )
        throws InvalidTypeException
    {
//        if ( !ValueTypes.REFERENCE.equals( property.getType() ) )
//        {
//            throw new InvalidTypeException( property, ValueTypes.REFERENCE );
//        }
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newReference( Reference.from( value ) );
    }
}

