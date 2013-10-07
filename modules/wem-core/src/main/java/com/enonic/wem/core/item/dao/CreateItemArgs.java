package com.enonic.wem.core.item.dao;


import com.enonic.wem.api.Icon;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.item.ItemPath;

public class CreateItemArgs
{
    private final UserKey creator;

    private final ItemPath parent;

    private final String name;

    private final Icon icon;

    private final RootDataSet rootDataSet;

    CreateItemArgs( Builder builder )
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

    ItemPath parent()
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

    public static Builder newCreateItemArgs()
    {
        return new Builder();
    }

    public static class Builder
    {
        private UserKey creator;

        private ItemPath parent;

        private String name;

        private Icon icon;

        private RootDataSet rootDataSet;

        public Builder creator( UserKey value )
        {
            this.creator = value;
            return this;
        }

        public Builder parent( ItemPath value )
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

        public CreateItemArgs build()
        {
            return new CreateItemArgs( this );
        }
    }
}
