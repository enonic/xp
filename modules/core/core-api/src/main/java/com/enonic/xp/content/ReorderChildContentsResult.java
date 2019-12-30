package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
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
