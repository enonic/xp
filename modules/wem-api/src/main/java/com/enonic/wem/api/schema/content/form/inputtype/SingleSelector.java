package com.enonic.wem.api.schema.content.form.inputtype;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.data.type.InvalidValueTypeException;
import com.enonic.wem.api.content.data.type.ValueTypes;
import com.enonic.wem.api.schema.content.form.BreaksRequiredContractException;
import com.enonic.wem.api.schema.content.form.InvalidValueException;
import com.enonic.wem.api.schema.content.form.inputtype.config.AbstractInputTypeConfigJsonGenerator;
import com.enonic.wem.api.schema.content.form.inputtype.config.SingleSelectorConfigJsonGenerator;

public class SingleSelector
    extends BaseInputType
{
    public SingleSelector()
    {
        super( SingleSelectorConfig.class );
    }

    public AbstractInputTypeConfigJsonSerializer getInputTypeConfigJsonSerializer()
    {
        return SingleSelectorConfigJsonSerializer.DEFAULT;
    }

    @Override
    public AbstractInputTypeConfigJsonGenerator getInputTypeConfigJsonGenerator()
    {
        return SingleSelectorConfigJsonGenerator.DEFAULT;
    }

    @Override
    public AbstractInputTypeConfigXmlSerializer getInputTypeConfigXmlGenerator()
    {
        return SingleSelectorConfigXmlSerializer.DEFAULT;
    }

    @Override
    public void checkValidity( final Property property )
        throws InvalidValueTypeException, InvalidValueException
    {
        ValueTypes.TEXT.checkValidity( property );
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
        return new Value.Text( value );
    }

}
