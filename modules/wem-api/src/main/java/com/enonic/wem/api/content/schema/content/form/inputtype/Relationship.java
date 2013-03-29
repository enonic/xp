package com.enonic.wem.api.content.schema.content.form.inputtype;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.data.type.DataTool;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.data.type.InvalidValueTypeException;
import com.enonic.wem.api.content.data.type.JavaType;
import com.enonic.wem.api.content.schema.content.form.BreaksRequiredContractException;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

public class Relationship
    extends BaseInputType
{
    public Relationship()
    {
        super( RelationshipConfig.class );
    }

    public AbstractInputTypeConfigJsonSerializer getInputTypeConfigJsonGenerator()
    {
        return RelationshipConfigJsonSerializer.DEFAULT;
    }

    @Override
    public AbstractInputTypeConfigXmlSerializer getInputTypeConfigXmlGenerator()
    {
        return RelationshipConfigXmlSerializer.DEFAULT;
    }

    @Override
    public void checkValidity( final Data data )
        throws InvalidValueTypeException, InvalidValueException
    {
        DataTool.checkDataType( data, DataTypes.CONTENT_REFERENCE );
    }

    @Override
    public void checkBreaksRequiredContract( final Data data )
        throws BreaksRequiredContractException
    {
        final String stringValue = data.getString();
        if ( StringUtils.isBlank( stringValue ) )
        {
            throw new BreaksRequiredContractException( data, this );
        }
    }

    @Override
    public Value newValue( final String value )
    {
        return Value.newValue().type( DataTypes.CONTENT_REFERENCE ).value( JavaType.CONTENT_ID.convertFrom( value ) ).build();
    }

}
