package com.enonic.wem.core.content.type.datatype;


import com.enonic.wem.core.content.type.configitem.fieldtype.FieldType;

public abstract class AbstractDataType
    implements DataType
{
    private final String name;

    private BasalValueType basalValueType;

    private FieldType defaultFieldType;

    public AbstractDataType( BasalValueType basalValueType, FieldType defaultFieldType )
    {
        this.name = this.getClass().getName();
        this.basalValueType = basalValueType;
        this.defaultFieldType = defaultFieldType;
    }

    public FieldType getDefaultFieldType()
    {
        return defaultFieldType;
    }

    @Override
    public BasalValueType getBasalValueType()
    {
        return this.basalValueType;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
