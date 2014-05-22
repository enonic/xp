package com.enonic.wem.api.context;

import com.enonic.wem.api.entity.Workspace;

public class Context
{
    private final Workspace workspace;

    public Context( final Workspace workspace )
    {
        this.workspace = workspace;
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }
}
