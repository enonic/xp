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
        super( FileUploaderConfig.class, false );
    }

    @Override
    public AbstractInputTypeConfigJsonSerializer getInputTypeConfigJsonSerializer()
    {
        return FileUploaderConfigJsonSerializer.DEFAULT;
    }

    @Override
    public AbstractInputTypeConfigXmlSerializer getInputTypeConfigXmlSerializer()
    {
        return FileUploaderConfigXmlSerializer.DEFAULT;
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

    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return FileUploaderConfig.create().build();
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newReference( Reference.from( value ) );
    }
}

