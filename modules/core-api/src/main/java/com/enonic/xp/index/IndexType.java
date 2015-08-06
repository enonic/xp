package com.enonic.xp.index;

import com.google.common.annotations.Beta;

@Beta
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
