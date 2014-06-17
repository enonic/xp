package com.enonic.wem.core.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityComparison;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.workspace.compare.WorkspaceCompareService;
import com.enonic.wem.core.workspace.compare.query.CompareEntityQuery;

public class CompareNodeCommand
{
    private final EntityId id;

    private final Workspace target;

    private final Context context;

    private final WorkspaceCompareService workspaceCompareService;

    private CompareNodeCommand( final Builder builder )
    {
        this.id = builder.id;
        this.target = builder.target;
        this.context = builder.context;
        this.workspaceCompareService = builder.workspaceCompareService;
    }

    public static Builder create( final Context context )
    {
        return new Builder( context );
    }

    public EntityComparison execute()
    {
        return this.workspaceCompareService.compare( new CompareEntityQuery( this.id, this.context.getWorkspace(), this.target ) );
    }

    public static class Builder
    {
        private EntityId id;

        private Workspace target;

        private final Context context;

        private WorkspaceCompareService workspaceCompareService;

        public Builder( final Context context )
        {
            this.context = context;
        }

        public Builder id( final EntityId id )
        {
            this.id = id;
            return this;
        }

        public Builder target( final Workspace target )
        {
            this.target = target;
            return this;
        }

        public Builder compareService( final WorkspaceCompareService workspaceCompareService )
        {
            this.workspaceCompareService = workspaceCompareService;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( id );
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( context );
            Preconditions.checkNotNull( workspaceCompareService );
        }

        public CompareNodeCommand build()
        {
            validate();
            return new CompareNodeCommand( this );
        }
    }
}