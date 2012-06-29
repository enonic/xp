package com.enonic.wem.core.content.config.field.type;


import com.enonic.wem.core.content.FieldValue;
import com.enonic.wem.core.content.config.field.type.value.ValueTypes;

public class Phone
    extends AbstractBaseFieldType
{
    Phone()
    {
        super( "phone", ValueTypes.SINGLE_LINED_STRING );
    }

    public FieldTypeJsonGenerator getJsonGenerator()
    {
        return BaseFieldTypeJsonGenerator.DEFAULT;
    }

    @Override
    public boolean validValue( final FieldValue fieldValue )
    {
        return getValueType().validValue( fieldValue );
    }
}

