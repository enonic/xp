package com.enonic.wem.core.item.dao;


import org.joda.time.DateTime;

import com.google.common.base.Optional;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.item.ItemId;

public class UpdateItemArgs
{
    private final UserKey updater;

    private final Optional<DateTime> readAt;

    private final ItemId itemToUpdate;

    private final String name;

    private final Icon icon;

    private final RootDataSet rootDataSet;

    UpdateItemArgs( Builder builder )
    {
        this.updater = builder.updater;
        this.itemToUpdate = builder.itemToUpdate;
        this.readAt = builder.readAt;
        this.name = builder.name;
        this.icon = builder.icon;
        this.rootDataSet = builder.rootDataSet;
    }

    UserKey updater()
    {
        return updater;
    }

    ItemId itemToUpdate()
    {
        return itemToUpdate;
    }

    Optional<DateTime> readAt()
    {
        return this.readAt;
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

        private ItemId itemToUpdate;

        private Optional<DateTime> readAt;

        private String name;

        private Icon icon;

        private RootDataSet rootDataSet;

        public Builder updater( UserKey value )
        {
            this.updater = value;
            return this;
        }

        public Builder itemToUpdate( ItemId value )
        {
            this.itemToUpdate = value;
            return this;
        }

        public Builder readAt( Optional<DateTime> value )
        {
            this.readAt = value;
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

        public UpdateItemArgs build()
        {
            return new UpdateItemArgs( this );
        }
    }
}
