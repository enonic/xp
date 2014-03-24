package com.enonic.wem.api.entity;

import com.google.common.base.Preconditions;

public class GetNodeByPathParams
{
    private final NodePath path;

    public GetNodeByPathParams( final NodePath path )
    {
        this.path = path;
    }

    public void validate()
    {
        Preconditions.checkNotNull( path, "path must be specified" );
    }

    public NodePath getPath()
    {
        return this.path;
    }
}
