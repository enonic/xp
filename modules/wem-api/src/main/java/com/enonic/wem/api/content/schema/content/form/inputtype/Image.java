package com.enonic.wem.api.content.schema.content.form.inputtype;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.data.type.InvalidValueTypeException;
import com.enonic.wem.api.content.data.type.JavaType;
import com.enonic.wem.api.content.schema.content.form.BreaksRequiredContractException;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

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
    public void checkValidity( final Data data )
        throws InvalidValueTypeException, InvalidValueException
    {
        DataTypes.CONTENT_ID.checkValidity( data );
    }

    @Override
    public void checkBreaksRequiredContract( final Data data )
        throws BreaksRequiredContractException
    {

    }

    @Override
    public Value newValue( final String value )
    {
        return Value.newValue().type( DataTypes.CONTENT_ID ).value( JavaType.CONTENT_ID.convertFrom( value ) ).build();
    }

}

