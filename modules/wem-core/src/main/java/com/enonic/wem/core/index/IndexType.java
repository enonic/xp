package com.enonic.wem.core.index;

public enum IndexType
{
    ITEM,
    ACCOUNT,
    CONTENT,
    BINARIES;

    public String getIndexTypeName()
    {
        return this.name().toLowerCase();
    }

}
