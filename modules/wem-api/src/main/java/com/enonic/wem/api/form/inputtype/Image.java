package com.enonic.wem.api.form.inputtype;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.ValueOfUnexpectedClassException;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.form.BreaksRequiredContractException;
import com.enonic.wem.api.form.InvalidValueException;

final class Image
    extends InputType
{
    Image()
    {
    }

    @Override
    public void checkValidity( final Property property )
        throws ValueOfUnexpectedClassException, InvalidValueException
    {
        ValueTypes.ATTACHMENT_NAME.checkValidity( property );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {

    }

    @Override
    public Value newValue( final String value )
    {
        return new Value.AttachmentName( ValueTypes.ATTACHMENT_NAME.convert( value ) );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }
}
