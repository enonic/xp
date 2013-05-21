package com.enonic.wem.api.schema.content.form.inputtype;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.data.type.InvalidValueTypeException;
import com.enonic.wem.api.content.data.type.ValueTypes;
import com.enonic.wem.api.schema.content.form.BreaksRequiredContractException;
import com.enonic.wem.api.schema.content.form.InvalidValueException;

public class Image
    extends BaseInputType
{
    public Image()
    {
        super( ImageConfig.class );
    }

    public AbstractInputTypeConfigJsonSerializer getInputTypeConfigJsonGenerator()
    {
        return ImageConfigJsonSerializer.DEFAULT;
    }

    @Override
    public AbstractInputTypeConfigXmlSerializer getInputTypeConfigXmlGenerator()
    {
        return ImageConfigXmlSerializer.DEFAULT;
    }

    @Override
    public void checkValidity( final Property property )
        throws InvalidValueTypeException, InvalidValueException
    {
        ValueTypes.CONTENT_ID.checkValidity( property );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {

    }

    @Override
    public Value newValue( final String value )
    {
        return new Value.ContentId( ValueTypes.CONTENT_ID.convert( value ) );
    }

}

