package com.enonic.wem.repo.internal.index;

public enum IndexType
{
    _DEFAULT_,
    WORKSPACE,
    VERSION,
    SEARCH,
    ENTITY;

    public String getName()
    {
        return this.name().toLowerCase();
    }

}
