package com.enonic.wem.core.item.dao;


import com.enonic.wem.api.Icon;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.item.NodePath;

public class CreateNodeArguments
{
    private final UserKey creator;

    private final NodePath parent;

    private final String name;

    private final Icon icon;

    private final RootDataSet rootDataSet;

    CreateNodeArguments( Builder builder )
    {
        this.creator = builder.creator;
        this.parent = builder.parent;
        this.name = builder.name;
        this.icon = builder.icon;
        this.rootDataSet = builder.rootDataSet;
    }

    UserKey creator()
    {
        return creator;
    }

    NodePath parent()
    {
        return parent;
    }

    String name()
    {
        return name;
    }

    Icon icon()
    {
        return icon;
    }

    RootDataSet rootDataSet()
    {
        return rootDataSet;
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

        public CreateNodeArguments build()
        {
            return new CreateNodeArguments( this );
        }
    }
}
