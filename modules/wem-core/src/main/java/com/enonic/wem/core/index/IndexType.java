package com.enonic.wem.core.index;

public enum IndexType
{
    ENTITY,
    NODE,
    RELATION,
    POLICY,
    CONTENT,
    BINARIES;

    public String getIndexTypeName()
    {
        return this.name().toLowerCase();
    }

}
