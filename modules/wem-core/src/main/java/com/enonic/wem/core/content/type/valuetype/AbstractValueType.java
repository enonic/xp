package com.enonic.wem.core.content.type.valuetype;


public abstract class AbstractValueType
    implements ValueType
{
    private final String name;

    private BasalValueType basalValueType;

    public AbstractValueType( BasalValueType basalValueType )
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
