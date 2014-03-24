package com.enonic.wem.api.entity;

import com.google.common.base.Preconditions;

public class GetNodesByPathsParams
{
    private final NodePaths paths;

    public GetNodesByPathsParams( final NodePaths paths )
    {
        this.paths = paths;
    }

    public void validate()
    {
        Preconditions.checkNotNull( paths, "paths must be specified" );
    }

    public NodePaths getPaths()
    {
        return this.paths;
    }
}
