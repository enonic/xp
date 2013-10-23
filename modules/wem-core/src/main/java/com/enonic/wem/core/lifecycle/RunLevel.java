package com.enonic.wem.core.lifecycle;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public enum RunLevel
{
    L1,
    L2,
    L3,
    L4,
    L5;

    public static List<RunLevel> all()
    {
        return ImmutableList.copyOf( values() );
    }

    public static List<RunLevel> reverse()
    {
        return Lists.reverse( all() );
    }
}
