package com.enonic.wem.core.entity.dao;


import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.entity.Attachments;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.NodePath;

public class CreateNodeArguments
{
    private final UserKey creator;

    private final NodePath parent;

    private final String name;

    private final RootDataSet rootDataSet;

    private final Attachments attachments;

    private final EntityIndexConfig entityIndexConfig;

    private final boolean embed;

    CreateNodeArguments( Builder builder )
    {
        this.creator = builder.creator;
        this.parent = builder.parent;
        this.name = builder.name;
        this.rootDataSet = builder.rootDataSet;
        this.attachments = builder.attachments;
        this.entityIndexConfig = builder.entityIndexConfig;
        this.embed = builder.embed;
    }

    UserKey creator()
    {
        return this.creator;
    }

    NodePath parent()
    {
        return this.parent;
    }

    String name()
    {
        return this.name;
    }

    RootDataSet rootDataSet()
    {
        return this.rootDataSet;
    }

    Attachments attachments()
    {
        return this.attachments;
    }

    boolean embed()
    {
        return this.embed;
    }

    EntityIndexConfig entityIndexConfig()
    {
        return this.entityIndexConfig;
    }

    public static Builder newCreateNodeArgs()
    {
        return new Builder();
    }

    public static class Builder
    {
        private UserKey creator;

        private NodePath parent;

        private String name;

        private RootDataSet rootDataSet;

        private Attachments attachments = Attachments.empty();

        private EntityIndexConfig entityIndexConfig;

        private boolean embed = false;


        public Builder creator( UserKey value )
        {
            this.creator = value;
            return this;
        }

        public Builder parent( NodePath value )
        {
            this.parent = value;
            return this;
        }

        public Builder name( String value )
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

        public Builder entityIndexConfig( final EntityIndexConfig value )
        {
            this.entityIndexConfig = value;
            return this;
        }

        public Builder embed( final boolean embed )
        {
            this.embed = embed;
            return this;
        }

        public CreateNodeArguments build()
        {
            return new CreateNodeArguments( this );
        }

    }
}
