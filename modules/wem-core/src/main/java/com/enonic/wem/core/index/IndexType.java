package com.enonic.wem.core.index;

public enum IndexType
{
    ACCOUNT,
    CONTENT,
    BINARIES;

    public String getIndexTypeName()
    {
        return this.name().toLowerCase();
    }

}
