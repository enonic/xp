package com.enonic.wem.core.content.type.configitem.field.type;

import com.enonic.wem.core.content.data.Value;
import com.enonic.wem.core.content.type.valuetype.ValueTypes;

public class Tags
    extends BaseFieldType
{
    Tags()
    {
        super( "tags", ValueTypes.SINGLE_LINED_STRING );
    }

    public FieldTypeJsonGenerator getJsonGenerator()
    {
        return BaseFieldTypeJsonGenerator.DEFAULT;
    }

    @Override
    public boolean validValue( final Value fieldValue )
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

