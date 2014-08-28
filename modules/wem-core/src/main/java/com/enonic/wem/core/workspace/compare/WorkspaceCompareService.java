package com.enonic.wem.core.workspace.compare;

import com.enonic.wem.api.entity.NodeComparison;
import com.enonic.wem.api.entity.NodeComparisons;
import com.enonic.wem.core.workspace.compare.query.CompareEntitiesQuery;
import com.enonic.wem.core.workspace.compare.query.CompareEntityQuery;
import com.enonic.wem.core.workspace.compare.query.CompareWorkspacesQuery;

public interface WorkspaceCompareService
{
    public WorkspaceComparison compareWorkspaces( final CompareWorkspacesQuery query );

    public NodeComparisons compare( final CompareEntitiesQuery query );

    public NodeComparison compare( final CompareEntityQuery query );

}
