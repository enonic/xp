package com.enonic.wem.admin.json.content;

import com.enonic.wem.api.content.ReorderChildContentsResult;

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
