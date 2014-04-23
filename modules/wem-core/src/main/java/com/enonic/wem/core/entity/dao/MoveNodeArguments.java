package com.enonic.wem.core.entity.dao;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;

public class MoveNodeArguments
{
    private final UserKey updater;

    private final EntityId nodeToMove;

    private final NodeName name;

    private final NodePath parentPath;


    private final EntityIndexConfig entityIndexConfig;

    private MoveNodeArguments( final Builder builder )
    {
        this.updater = builder.updater;
        this.entityIndexConfig = builder.entityIndexConfig;
        this.name = builder.name;
        this.parentPath = builder.parentPath;
        this.nodeToMove = builder.nodeToUpdate;
    }

    public UserKey updater()
    {
        return updater;
    }

    public EntityId nodeToMove()
    {
        return nodeToMove;
    }

    public NodeName name()
    {
        return name;
    }

    public NodePath parentPath()
    {
        return parentPath;
    }

    public EntityIndexConfig getEntityIndexConfig()
    {
        return entityIndexConfig;
    }

    public static Builder newMoveNode()
    {
        return new Builder();
    }

    public static class Builder
    {
        private UserKey updater;

        private EntityId nodeToUpdate;

        private NodeName name;

        private NodePath parentPath;

        private EntityIndexConfig entityIndexConfig;


        public Builder updater( final UserKey updater )
        {
            this.updater = updater;
            return this;
        }

        public Builder nodeToMove( final EntityId nodeToUpdate )
        {
            this.nodeToUpdate = nodeToUpdate;
            return this;
        }

        public Builder name( final NodeName name )
        {
            this.name = name;
            return this;
        }

        public Builder parentPath( final NodePath parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }


        public Builder entityIndexConfig( final EntityIndexConfig entityIndexConfig )
        {
            this.entityIndexConfig = entityIndexConfig;
            return this;
        }

        public MoveNodeArguments build()
        {
            return new MoveNodeArguments( this );
        }
    }

}
