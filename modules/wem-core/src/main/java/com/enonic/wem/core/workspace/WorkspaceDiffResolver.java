package com.enonic.wem.core.workspace;

import com.enonic.wem.api.entity.Workspace;

public interface WorkspaceDiffResolver
{
    public WorkspaceDiff resolve( final Workspace source, final Workspace target );

}
