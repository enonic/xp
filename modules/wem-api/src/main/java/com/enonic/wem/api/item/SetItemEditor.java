package com.enonic.wem.api.item;


import org.joda.time.DateTime;

import com.google.common.base.Optional;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.data.RootDataSet;

public class SetItemEditor
    implements ItemEditor
{
    private final Optional<DateTime> readAt;

    private final String name;

    private final Icon icon;

    private final RootDataSet rootDataSet;

    private SetItemEditor( final Builder builder )
    {
        this.readAt = builder.readAt;
        this.name = builder.name;
        this.icon = builder.icon;
        this.rootDataSet = builder.rootDataSet;
    }

    @Override
    public Optional<DateTime> getReadAt()
    {
        return readAt;
    }

    @Override
    public Item edit( final Item toBeEdited )
    {
        Item.Builder builder = Item.newItem( toBeEdited );

        boolean changed = false;
        if ( !toBeEdited.name().equals( this.name ) )
        {
            builder.name( this.name );
            changed = true;
        }
        if ( !toBeEdited.icon().equals( this.icon ) )
        {
            builder.icon( this.icon );
            changed = true;
        }
        builder.rootDataSet( this.rootDataSet );
        changed = true;

        if ( changed )
        {
            return builder.build();
        }
        else
        {
            return null;
        }
    }

    public static Builder newSetItemEditor()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Optional<DateTime> readAt;

        private String name;

        private Icon icon;

        private RootDataSet rootDataSet;

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

        public Builder readAt( Optional<DateTime> value )
        {
            this.readAt = value;
            return this;
        }

        public SetItemEditor build()
        {
            return new SetItemEditor( this );
        }
    }
}
