package com.enonic.wem.core.workspace.diff;

import com.enonic.wem.core.workspace.diff.query.EntityDiffQuery;
import com.enonic.wem.core.workspace.diff.query.WorkspacesDiffQuery;

interface WorkspaceDiffService
{
    public WorkspacesDifferences getWorkspacesDifferences( final WorkspacesDiffQuery query );

    public void getDifferences( final EntityDiffQuery query );

}
