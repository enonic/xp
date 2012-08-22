package com.enonic.wem.core.content.type.datatype;


public abstract class AbstractDataType
    implements DataType
{
    private final String name;

    private BasalValueType basalValueType;

    public AbstractDataType( BasalValueType basalValueType )
    {
        this.name = this.getClass().getName();
        this.basalValueType = basalValueType;
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
