package com.enonic.wem.api.form.inputtype;


import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.form.BreaksRequiredContractException;

final class ImageSelector
    extends InputType
{
    ImageSelector()
    {
        super( ImageSelectorConfig.class );
    }

    public AbstractInputTypeConfigJsonSerializer getInputTypeConfigJsonSerializer()
    {
        return ImageSelectorConfigJsonSerializer.DEFAULT;
    }

    @Override
    public AbstractInputTypeConfigXmlSerializer getInputTypeConfigXmlSerializer()
    {
        return ImageSelectorConfigXmlSerializer.DEFAULT;
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {

    }

    @Override
    public Value newValue( final String value )
    {
        return Value.newContentId( ContentId.from( value ) );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }

}

