package com.enonic.wem.core.entity.dao;


import com.enonic.wem.api.Icon;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.NodePath;

public class CreateNodeArguments
{
    private final UserKey creator;

    private final NodePath parent;

    private final String name;

    private final Icon icon;

    private final RootDataSet rootDataSet;

    private final EntityIndexConfig entityIndexConfig;

    CreateNodeArguments( Builder builder )
    {
        this.creator = builder.creator;
        this.parent = builder.parent;
        this.name = builder.name;
        this.icon = builder.icon;
        this.rootDataSet = builder.rootDataSet;
        this.entityIndexConfig = builder.entityIndexConfig;
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

    Icon icon()
    {
        return this.icon;
    }

    RootDataSet rootDataSet()
    {
        return this.rootDataSet;
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

        private Icon icon;

        private RootDataSet rootDataSet;

        private EntityIndexConfig entityIndexConfig;

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

        public Builder icon( Icon value )
        {
            this.icon = value;
            return this;
        }

        public Builder rootDataSet( RootDataSet value )
        {
            this.rootDataSet = value;
            return this;
        }

        public Builder entityIndexConfig( final EntityIndexConfig value )
        {
            this.entityIndexConfig = value;
            return this;
        }

        public CreateNodeArguments build()
        {
            return new CreateNodeArguments( this );
        }
    }
}
