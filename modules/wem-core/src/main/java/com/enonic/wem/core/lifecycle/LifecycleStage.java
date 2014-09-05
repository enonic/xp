package com.enonic.wem.core.lifecycle;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public enum LifecycleStage
{
    L1,
    L2,
    L3,
    L4,
    L5;

    public static List<LifecycleStage> all()
    {
        return ImmutableList.copyOf( values() );
    }

    public static List<LifecycleStage> reverse()
    {
        return Lists.reverse( all() );
    }
}
