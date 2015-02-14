package com.enonic.xp.core.index;

public enum IndexType
{
    SEARCH,
    BRANCH,
    VERSION;

    public String getName()
    {
        return this.name().toLowerCase();
    }

}
