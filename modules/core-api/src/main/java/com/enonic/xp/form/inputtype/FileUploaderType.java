package com.enonic.xp.form.inputtype;


import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.util.Reference;

final class FileUploaderType
    extends InputType
{
    public FileUploaderType()
    {
        super( InputTypeName.FILE_UPLOADER );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
    }

    @Override
    public void checkTypeValidity( final Property property )
    {
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newReference( Reference.from( value ) );
    }
}
