package com.enonic.wem.core.workspace.query;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Workspace;

public class WorkspaceIdsQuery
    extends AbstractWorkspaceQuery
{
    private EntityIds entityIds;

    public WorkspaceIdsQuery( final Workspace workspace, final EntityIds entityIds )
    {
        super( workspace );
        this.entityIds = entityIds;
    }

    public Set<String> getEntityIdsAsStrings()
    {
        final Set<String> values = Sets.newHashSet();

        for ( final EntityId entityId : this.entityIds )
        {
            values.add( entityId.toString() );
        }

        return values;
    }
}
