package com.enonic.wem.core.workspace.query;

import com.enonic.wem.api.entity.Workspace;

public class WorkspaceDiffQuery
{
    private final Workspace source;

    private final Workspace target;

    public WorkspaceDiffQuery( final Workspace source, final Workspace target )
    {
        this.source = source;
        this.target = target;
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
