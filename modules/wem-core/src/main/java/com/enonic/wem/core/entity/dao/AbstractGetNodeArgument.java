package com.enonic.wem.core.entity.dao;

import com.enonic.wem.api.entity.Workspace;

public abstract class AbstractGetNodeArgument
{
    private final Workspace workspace;

    protected AbstractGetNodeArgument( final Workspace workspace )
    {
        this.workspace = workspace;
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }
}
