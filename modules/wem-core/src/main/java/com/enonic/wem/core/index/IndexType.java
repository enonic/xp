package com.enonic.wem.core.index;

public enum IndexType
{
    _DEFAULT_,
    WORKSPACE,
    VERSION,
    SEARCH,
    ENTITY,
    NODE;

    public String getName()
    {
        return this.name().toLowerCase();
    }

}
