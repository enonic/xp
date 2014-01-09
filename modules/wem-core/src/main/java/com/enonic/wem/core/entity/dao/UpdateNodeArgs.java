package com.enonic.wem.core.entity.dao;


import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.entity.Attachments;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityPropertyIndexConfig;
import com.enonic.wem.api.entity.NodeName;

public class UpdateNodeArgs
{
    private final UserKey updater;

    private final EntityId nodeToUpdate;

    private final NodeName name;

    private final RootDataSet rootDataSet;

    private final Attachments attachments;

    private final EntityPropertyIndexConfig entityIndexConfig;

    UpdateNodeArgs( Builder builder )
    {
        this.updater = builder.updater;
        this.nodeToUpdate = builder.nodeToUpdate;
        this.name = builder.name;
        this.rootDataSet = builder.rootDataSet;
        this.attachments = builder.attachments != null ? builder.attachments : Attachments.empty();
        this.entityIndexConfig = builder.entityIndexConfig;
    }

    UserKey updater()
    {
        return updater;
    }

    EntityId nodeToUpdate()
    {
        return nodeToUpdate;
    }

    NodeName name()
    {
        return name;
    }

    RootDataSet rootDataSet()
    {
        return rootDataSet;
    }

    Attachments attachments()
    {
        return attachments;
    }

    EntityPropertyIndexConfig entityIndexConfig()
    {
        return entityIndexConfig;
    }

    public static Builder newUpdateItemArgs()
    {
        return new Builder();
    }

    public static class Builder
    {
        private UserKey updater;

        private EntityId nodeToUpdate;

        private NodeName name;

        private RootDataSet rootDataSet;

        private Attachments attachments;

        private EntityPropertyIndexConfig entityIndexConfig;

        public Builder updater( UserKey value )
        {
            this.updater = value;
            return this;
        }

        public Builder nodeToUpdate( EntityId value )
        {
            this.nodeToUpdate = value;
            return this;
        }

        public Builder name( NodeName value )
        {
            this.name = value;
            return this;
        }

        public Builder rootDataSet( RootDataSet value )
        {
            this.rootDataSet = value;
            return this;
        }

        public Builder attachments( Attachments value )
        {
            this.attachments = value;
            return this;
        }

        public Builder entityIndexConfig( final EntityPropertyIndexConfig entityIndexConfig )
        {
            this.entityIndexConfig = entityIndexConfig;
            return this;
        }

        public UpdateNodeArgs build()
        {
            return new UpdateNodeArgs( this );
        }
    }
}
