package com.enonic.wem.core.content.config.field.type;


import com.enonic.wem.core.content.FieldValue;
import com.enonic.wem.core.content.config.field.type.value.ValueTypes;

public class RadioButtonsTmp
    extends AbstractBaseFieldType
{
    RadioButtonsTmp()
    {
        super( "radioButtons", ValueTypes.SINGLE_LINED_STRING );
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
