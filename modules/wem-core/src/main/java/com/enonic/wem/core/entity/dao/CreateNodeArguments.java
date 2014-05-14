package com.enonic.wem.core.entity.dao;


import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.entity.Attachments;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Workspace;

public class CreateNodeArguments
{
    private final UserKey creator;

    private final NodePath parent;

    private final String name;

    private final RootDataSet rootDataSet;

    private final Attachments attachments;

    private final EntityIndexConfig entityIndexConfig;

    private final Workspace workspace;

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
        this.workspace = builder.workspace;
    }

    public UserKey creator()
    {
        return this.creator;
    }

    public NodePath parent()
    {
        return this.parent;
    }

    public String name()
    {
        return this.name;
    }

    public RootDataSet rootDataSet()
    {
        return this.rootDataSet;
    }

    public Attachments attachments()
    {
        return this.attachments;
    }

    public boolean embed()
    {
        return this.embed;
    }

    public EntityIndexConfig entityIndexConfig()
    {
        return this.entityIndexConfig;
    }

    public Workspace workspace()
    {
        return this.workspace;
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

        private Workspace workspace;

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

        public Builder workspace( final Workspace workspace )
        {
            this.workspace = workspace;
            return this;
        }

        public CreateNodeArguments build()
        {
            return new CreateNodeArguments( this );
        }

    }
}
