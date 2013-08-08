package com.enonic.wem.api.schema.content.form.inputtype;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.data.type.InvalidValueTypeException;
import com.enonic.wem.api.content.data.type.PropertyTool;
import com.enonic.wem.api.content.data.type.ValueTypes;
import com.enonic.wem.api.schema.content.form.BreaksRequiredContractException;
import com.enonic.wem.api.schema.content.form.InvalidValueException;
import com.enonic.wem.api.schema.content.form.inputtype.config.AbstractInputTypeConfigJsonGenerator;
import com.enonic.wem.api.schema.content.form.inputtype.config.RelationshipConfigJsonGenerator;

public class Relationship
    extends BaseInputType
{
    public Relationship()
    {
        super( RelationshipConfig.class );
    }

    public AbstractInputTypeConfigJsonSerializer getInputTypeConfigJsonSerializer()
    {
        return RelationshipConfigJsonSerializer.DEFAULT;
    }

    @Override
    public AbstractInputTypeConfigJsonGenerator getInputTypeConfigJsonGenerator()
    {
        return RelationshipConfigJsonGenerator.DEFAULT;
    }

    @Override
    public AbstractInputTypeConfigXmlSerializer getInputTypeConfigXmlGenerator()
    {
        return RelationshipConfigXmlSerializer.DEFAULT;
    }

    @Override
    public void checkValidity( final Property property )
        throws InvalidValueTypeException, InvalidValueException
    {
        PropertyTool.checkValueType( property, ValueTypes.CONTENT_ID );
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

}
