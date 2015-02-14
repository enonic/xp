package com.enonic.xp.admin.impl.json.content;

import com.enonic.xp.content.ReorderChildContentsResult;

public class ReorderChildrenResultJson
{
    private final int movedChildren;

    public ReorderChildrenResultJson( final ReorderChildContentsResult result )
    {
        this.movedChildren = result.getMovedChildren();
    }

    @SuppressWarnings("UnusedDeclaration")
    public int getMovedChildren()
    {
        return movedChildren;
    }
}
