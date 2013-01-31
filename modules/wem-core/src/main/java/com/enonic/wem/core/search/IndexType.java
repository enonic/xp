package com.enonic.wem.core.search;

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
