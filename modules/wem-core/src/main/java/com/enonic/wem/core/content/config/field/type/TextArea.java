package com.enonic.wem.core.content.config.field.type;

import com.enonic.wem.core.content.FieldValue;
import com.enonic.wem.core.content.config.field.type.value.ValueTypes;

public class TextArea
    extends AbstractBaseFieldType
{
    TextArea()
    {
        super( "textArea", ValueTypes.MULTI_LINED_STRING );
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

    public boolean requiresConfig()
    {
        return false;
    }

    public Class requiredConfigClass()
    {
        return null;
    }
}
