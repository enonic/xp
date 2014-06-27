package com.enonic.wem.core.entity;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityComparisons;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.workspace.compare.WorkspaceCompareService;
import com.enonic.wem.core.workspace.compare.query.CompareEntitiesQuery;

public class CompareNodesCommand
{
    private final EntityIds entityIds;

    private final Workspace target;

    private final Context context;

    private final WorkspaceCompareService compareService;

    private CompareNodesCommand( Builder builder )
    {
        entityIds = builder.entityIds;
        target = builder.target;
        context = builder.context;
        compareService = builder.compareService;
    }

    public static Builder create( final Context context )
    {
        return new Builder( context );
    }

    public EntityComparisons execute()
    {
        return this.compareService.compare( CompareEntitiesQuery.create().
            source( context.getWorkspace() ).
            target( this.target ).
            setEntityIds( this.entityIds ).
            build() );
    }

    public static final class Builder
    {
        private EntityIds entityIds;

        private Workspace target;

        private final Context context;

        private WorkspaceCompareService compareService;

        private Builder( final Context context )
        {
            this.context = context;
        }

        public Builder ids( EntityIds entityIds )
        {
            this.entityIds = entityIds;
            return this;
        }

        public Builder target( Workspace target )
        {
            this.target = target;
            return this;
        }

        public Builder compareService( WorkspaceCompareService workspaceCompareService )
        {
            this.compareService = workspaceCompareService;
            return this;
        }

        public CompareNodesCommand build()
        {
            return new CompareNodesCommand( this );
        }
    }
}
