package com.enonic.wem.api.form.inputtype;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.form.BreaksRequiredContractException;

final class Image
    extends InputType
{
    Image()
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
        return new Value.AttachmentName( ValueTypes.ATTACHMENT_NAME.convert( value ) );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }
}
