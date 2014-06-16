package com.enonic.wem.core.workspace.diff.query;

import com.enonic.wem.api.entity.Workspace;

public class WorkspacesDiffQuery
{
    private final Workspace source;

    private final Workspace target;

    public WorkspacesDiffQuery( final Workspace source, final Workspace target )
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
