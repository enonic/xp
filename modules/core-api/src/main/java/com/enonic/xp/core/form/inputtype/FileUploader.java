package com.enonic.xp.core.form.inputtype;


import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.data.Value;
import com.enonic.xp.core.form.BreaksRequiredContractException;
import com.enonic.xp.core.util.Reference;

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
    public Value newValue( final String value )
    {
        return Value.newReference( Reference.from( value ) );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }

}

