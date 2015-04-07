package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
public class ReorderChildContentsResult
{
    private final int movedChildren;

    public ReorderChildContentsResult( final int movedChildren )
    {
        this.movedChildren = movedChildren;
    }

    public int getMovedChildren()
    {
        return movedChildren;
    }
}
