package com.enonic.wem.core.content.type.configitem.fieldtype;


import com.enonic.wem.core.content.data.Value;
import com.enonic.wem.core.content.type.valuetype.ValueTypes;

public class Dropdown
    extends BaseFieldType
{
    Dropdown()
    {
        super( "dropdown", ValueTypes.SINGLE_LINED_STRING );
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
        return true;
    }

    public Class requiredConfigClass()
    {
        return DropdownConfig.class;
    }

    public FieldTypeConfigSerializerJson getFieldTypeConfigJsonGenerator()
    {
        return DropdownConfigSerializerJson.DEFAULT;
    }
}

