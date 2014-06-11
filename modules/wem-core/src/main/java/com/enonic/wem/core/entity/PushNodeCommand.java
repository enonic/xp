package com.enonic.wem.core.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.entity.dao.PushNodeArguments;

public class PushNodeCommand
    extends AbstractNodeCommand
{
    private final Workspace to;

    private final EntityId id;

    private PushNodeCommand( final Builder builder )
    {
        super( builder );
        this.to = builder.to;
        this.id = builder.id;
    }

    public Workspace getTo()
    {
        return to;
    }

    public EntityId getId()
    {
        return id;
    }

    public static Builder create( final Context context )
    {
        return new Builder( context );
    }

    void execute()
    {
        nodeDao.push( new PushNodeArguments( this.to, this.id ), this.context.getWorkspace() );
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Workspace to;

        private EntityId id;

        Builder( final Context context )
        {
            super( context );
        }

        public Builder to( final Workspace toWorkspace )
        {
            this.to = toWorkspace;
            return this;
        }

        public Builder id( final EntityId entityId )
        {
            this.id = entityId;
            return this;
        }

        public PushNodeCommand build()
        {
            validate();
            return new PushNodeCommand( this );
        }

        private void validate()
        {
            Preconditions.checkNotNull( to );
            Preconditions.checkNotNull( id );
        }
    }


}
