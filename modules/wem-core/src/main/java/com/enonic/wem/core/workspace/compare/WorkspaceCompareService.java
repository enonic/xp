package com.enonic.wem.core.workspace.compare;

import com.enonic.wem.api.entity.EntityComparison;
import com.enonic.wem.api.entity.EntityComparisons;
import com.enonic.wem.core.workspace.compare.query.CompareEntitiesQuery;
import com.enonic.wem.core.workspace.compare.query.CompareEntityQuery;
import com.enonic.wem.core.workspace.compare.query.CompareWorkspacesQuery;

public interface WorkspaceCompareService
{
    public WorkspaceComparison compareWorkspaces( final CompareWorkspacesQuery query );

    public EntityComparisons compare( final CompareEntitiesQuery query );

    public EntityComparison compare( final CompareEntityQuery query );

}
