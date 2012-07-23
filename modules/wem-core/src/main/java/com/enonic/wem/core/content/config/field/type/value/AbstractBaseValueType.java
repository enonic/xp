package com.enonic.wem.core.content.config.field.type.value;


public abstract class AbstractBaseValueType
    implements ValueType
{
    private final String name;

    public AbstractBaseValueType()
    {
        this.name = this.getClass().getName();
    }

    @Override
    public String toString()
    {
        return name;
    }
}
