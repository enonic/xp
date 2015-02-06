package com.enonic.wem.api.form.inputtype;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.ValueTypes;
import com.enonic.wem.api.form.BreaksRequiredContractException;

final class ContentSelector
    extends InputType
{
    ContentSelector()
    {
        super( ContentSelectorConfig.class );
    }

    public AbstractInputTypeConfigJsonSerializer getInputTypeConfigJsonSerializer()
    {
        return ContentSelectorConfigJsonSerializer.DEFAULT;
    }

    @Override
    public AbstractInputTypeConfigXmlSerializer getInputTypeConfigXmlSerializer()
    {
        return ContentSelectorConfigXmlSerializer.DEFAULT;
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {
        final String stringValue = property.getString();
        if ( StringUtils.isBlank( stringValue ) )
        {
            throw new BreaksRequiredContractException( property, this );
        }
    }

    @Override
    public Value newValue( final String value )
    {
        return Value.newReference( ValueTypes.REFERENCE.convert( value ) );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }

}
