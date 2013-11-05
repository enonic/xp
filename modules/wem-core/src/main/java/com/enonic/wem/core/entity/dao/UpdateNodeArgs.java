package com.enonic.wem.core.entity.dao;


import com.enonic.wem.api.Icon;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.entity.EntityId;

public class UpdateNodeArgs
{
    private final UserKey updater;

    private final EntityId itemToUpdate;

    private final String name;

    private final Icon icon;

    private final RootDataSet rootDataSet;

    UpdateNodeArgs( Builder builder )
    {
        this.updater = builder.updater;
        this.itemToUpdate = builder.itemToUpdate;
        this.name = builder.name;
        this.icon = builder.icon;
        this.rootDataSet = builder.rootDataSet;
    }

    UserKey updater()
    {
        return updater;
    }

    EntityId itemToUpdate()
    {
        return itemToUpdate;
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

    public static Builder newUpdateItemArgs()
    {
        return new Builder();
    }

    public static class Builder
    {
        private UserKey updater;

        private EntityId itemToUpdate;

        private String name;

        private Icon icon;

        private RootDataSet rootDataSet;

        public Builder updater( UserKey value )
        {
            this.updater = value;
            return this;
        }

        public Builder itemToUpdate( EntityId value )
        {
            this.itemToUpdate = value;
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

        public UpdateNodeArgs build()
        {
            return new UpdateNodeArgs( this );
        }
    }
}
