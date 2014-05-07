package com.enonic.wem.core.index;

public enum IndexType
{
    ENTITY,
    NODE;

    public String getName()
    {
        return this.name().toLowerCase();
    }

}
