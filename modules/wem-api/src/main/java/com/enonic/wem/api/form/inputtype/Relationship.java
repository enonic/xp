package com.enonic.wem.api.form.inputtype;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.form.BreaksRequiredContractException;

final class Relationship
    extends InputType
{
    Relationship()
    {
        super( RelationshipConfig.class );
    }

    public AbstractInputTypeConfigJsonSerializer getInputTypeConfigJsonSerializer()
    {
        return RelationshipConfigJsonSerializer.DEFAULT;
    }

    @Override
    public AbstractInputTypeConfigXmlSerializer getInputTypeConfigXmlSerializer()
    {
        return RelationshipConfigXmlSerializer.DEFAULT;
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
        return new Value.ContentId( ValueTypes.CONTENT_ID.convert( value ) );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }

}
