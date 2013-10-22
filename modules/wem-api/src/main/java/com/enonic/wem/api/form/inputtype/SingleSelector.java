package com.enonic.wem.api.form.inputtype;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.form.BreaksRequiredContractException;

final class SingleSelector
    extends InputType
{
    SingleSelector()
    {
        super( SingleSelectorConfig.class );
    }

    public AbstractInputTypeConfigJsonSerializer getInputTypeConfigJsonSerializer()
    {
        return SingleSelectorConfigJsonSerializer.DEFAULT;
    }

    @Override
    public AbstractInputTypeConfigXmlSerializer getInputTypeConfigXmlSerializer()
    {
        return SingleSelectorConfigXmlSerializer.DEFAULT;
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {
        final String stringValue = (String) property.getObject();
        if ( StringUtils.isBlank( stringValue ) )
        {
            throw new BreaksRequiredContractException( property, this );
        }
    }

    @Override
    public Value newValue( final String value )
    {
        return new Value.String( value );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }

}
