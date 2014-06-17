package com.enonic.wem.core.workspace.compare.query;

import com.enonic.wem.api.entity.Workspace;

public class CompareWorkspacesQuery
{
    private final Workspace source;

    private final Workspace target;

    public CompareWorkspacesQuery( final Workspace source, final Workspace target )
    {
        this.target = target;
        this.source = source;
    }

    public Workspace getSource()
    {
        return source;
    }

    public Workspace getTarget()
    {
        return target;
    }
}
