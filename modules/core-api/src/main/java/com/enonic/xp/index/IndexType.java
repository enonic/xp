package com.enonic.xp.index;

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
