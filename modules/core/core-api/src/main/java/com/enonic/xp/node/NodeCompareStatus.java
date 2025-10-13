package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public enum NodeCompareStatus
{
    NEW, NEW_TARGET, NEWER, OLDER, EQUAL, MOVED;
}
