package com.enonic.wem.core.content.type.valuetype;


public abstract class BaseValueType
    implements ValueType
{
    private final String name;

    public BaseValueType()
    {
        this.name = this.getClass().getName();
    }

    @Override
    public String toString()
    {
        return name;
    }
}
