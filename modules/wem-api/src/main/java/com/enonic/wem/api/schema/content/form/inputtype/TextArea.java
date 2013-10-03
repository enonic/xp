package com.enonic.wem.api.schema.content.form.inputtype;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.InvalidValueTypeException;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.schema.content.form.BreaksRequiredContractException;
import com.enonic.wem.api.schema.content.form.InvalidValueException;

final class TextArea
    extends InputType
{
    TextArea()
    {
        super( TextAreaConfig.class );
    }

    @Override
    public AbstractInputTypeConfigJsonSerializer getInputTypeConfigJsonSerializer()
    {
        return TextAreaConfigJsonSerializer.DEFAULT;
    }

    @Override
    public AbstractInputTypeConfigXmlSerializer getInputTypeConfigXmlSerializer()
    {
        return TextAreaConfigXmlSerializer.DEFAULT;
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

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return TextAreaConfig.newTextAreaConfig().columns( 50 ).rows( 10 ).build();
    }

}
