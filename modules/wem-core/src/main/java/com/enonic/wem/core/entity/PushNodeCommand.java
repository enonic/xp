package com.enonic.wem.core.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.entity.dao.PushNodeArguments;

public class PushNodeCommand
    extends AbstractNodeCommand
{
    private final Workspace target;

    private final EntityId id;

    private PushNodeCommand( final Builder builder )
    {
        super( builder );
        this.target = builder.target;
        this.id = builder.id;
    }

    public Workspace getTarget()
    {
        return target;
    }

    public EntityId getId()
    {
        return id;
    }

    public static Builder create( final Context context )
    {
        return new Builder( context );
    }

    Node execute()
    {
        return nodeDao.push( new PushNodeArguments( this.target, this.id ), this.context.getWorkspace() );
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Workspace target;

        private EntityId id;

        Builder( final Context context )
        {
            super( context );
        }

        public Builder to( final Workspace target )
        {
            this.target = target;
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
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( id );
        }
    }


}
