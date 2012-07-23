package com.enonic.wem.core.content.config.field.type;

import com.enonic.wem.core.content.FieldValue;
import com.enonic.wem.core.content.config.field.type.value.ValueTypes;

public class TextLine
    extends AbstractBaseFieldType
{
    TextLine()
    {
        super( "textLine", ValueTypes.SINGLE_LINED_STRING );
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
