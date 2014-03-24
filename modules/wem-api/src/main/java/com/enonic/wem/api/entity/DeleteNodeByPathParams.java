package com.enonic.wem.api.entity;

import com.google.common.base.Preconditions;

public class DeleteNodeByPathParams
{
    private final NodePath path;

    public DeleteNodeByPathParams( final NodePath path )
    {
        this.path = path;
    }

    public void validate()
    {
        Preconditions.checkNotNull( path, "path must be specified" );
    }

    public NodePath getPath()
    {
        return path;
    }
}
