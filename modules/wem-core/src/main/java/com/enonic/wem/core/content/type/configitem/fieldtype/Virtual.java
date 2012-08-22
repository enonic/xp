package com.enonic.wem.core.content.type.configitem.fieldtype;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.datatype.DataType;
import com.enonic.wem.core.content.type.datatype.DataTypes;

public class Virtual
    implements FieldType
{
    private String className;

    Virtual()
    {
        this.className = this.getClass().getName();
    }

    public String getName()
    {
        return "virtual";
    }

    public String getClassName()
    {
        return className;
    }

    public DataType getDataType()
    {
        return DataTypes.COMPUTED;
    }

    public FieldTypeJsonGenerator getJsonGenerator()
    {
        return BaseFieldTypeJsonGenerator.DEFAULT;
    }

    public boolean requiresConfig()
    {
        return false;
    }

    public Class requiredConfigClass()
    {
        return null;
    }

    public FieldTypeConfigSerializerJson getFieldTypeConfigJsonGenerator()
    {
        return null;
    }

    @Override
    public boolean breaksRequiredContract( final Data data )
    {
        // TODO
        return false;
    }
}
