package com.enonic.wem.api.item;


import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.support.ChangeTraceable;
import com.enonic.wem.api.support.illegaledit.IllegalEdit;
import com.enonic.wem.api.support.illegaledit.IllegalEditAware;
import com.enonic.wem.api.support.illegaledit.IllegalEditException;

public final class Item
    implements ChangeTraceable, IllegalEditAware<Item>
{
    private final ItemId id;

    // TODO: Class ItemName with restrictions; no whitespaces, not (only) underscore, likt 4.7 menu-item-name
    private final String name;

    private final ItemPath parent;

    private final ItemPath path;

    private final DateTime createdTime;

    private final UserKey creator;

    private final DateTime modifiedTime;

    private final UserKey modifier;

    // TODO: Remove
    private final Icon icon;

    private final RootDataSet rootDataSet;

    private final ItemIndexConfig itemIndexConfig;

    private Item( final Builder builder )
    {
        Preconditions.checkNotNull( builder.parent, "parent must be specified" );
        Preconditions.checkNotNull( builder.parent, "name must be specified" );

        this.id = builder.id;
        this.name = builder.name;
        this.parent = builder.parent;
        this.path = new ItemPath( this.parent, this.name );
        this.createdTime = builder.createdTime;
        this.creator = builder.creator;
        this.modifiedTime = builder.modifiedTime;
        this.modifier = builder.modifier;
        this.icon = builder.icon;
        this.itemIndexConfig = builder.itemIndexConfig;
        this.rootDataSet = new RootDataSet();
        if ( builder.dataSet != null )
        {
            for ( final Data data : builder.dataSet )
            {
                this.rootDataSet.add( data.copy() );
            }
        }
    }

    public ItemId id()
    {
        return id;
    }

    public String name()
    {
        return name;
    }

    public ItemPath parent()
    {
        return path;
    }

    public ItemPath path()
    {
        return path;
    }

    public DateTime getCreatedTime()
    {
        return createdTime;
    }

    public UserKey creator()
    {
        return creator;
    }

    public UserKey getCreator()
    {
        return creator;
    }

    public DateTime getModifiedTime()
    {
        return modifiedTime;
    }

    public UserKey modifier()
    {
        return modifier;
    }

    public UserKey getModifier()
    {
        return modifier;
    }

    public Icon icon()
    {
        return icon;
    }

    public RootDataSet rootDataSet()
    {
        return this.rootDataSet;
    }

    public Property property( final String path )
    {
        return rootDataSet.getProperty( path );
    }

    public ItemIndexConfig getItemIndexConfig()
    {
        return itemIndexConfig;
    }

    public DataSet dataSet( final String path )
    {
        return rootDataSet.getDataSet( path );
    }

    @Override
    public void checkIllegalEdit( final Item to )
        throws IllegalEditException
    {
        IllegalEdit.check( "id", this.id(), to.id(), Item.class );
        IllegalEdit.check( "name", this.name(), to.name(), Item.class );
        IllegalEdit.check( "parent", this.parent(), to.parent(), Item.class );
        IllegalEdit.check( "path", this.path(), to.path(), Item.class );
        IllegalEdit.check( "createdTime", this.getCreatedTime(), to.getCreatedTime(), Item.class );
        IllegalEdit.check( "creator", this.creator(), to.creator(), Item.class );
        IllegalEdit.check( "modifiedTime", this.getModifiedTime(), to.getModifiedTime(), Item.class );
        IllegalEdit.check( "modifier", this.modifier(), to.modifier(), Item.class );
    }

    public static Builder newItem()
    {
        return new Builder();
    }

    public static Builder newItem( final ItemId id )
    {
        return new Builder( id );
    }

    public static Builder newItem( final ItemId id, final String name )
    {
        return new Builder( id, name );
    }

    public static Builder newItem( final Item item )
    {
        return new Builder( item );
    }

    public static class Builder
    {
        private ItemId id;

        private String name;

        private ItemPath parent;

        private DateTime createdTime;

        private UserKey creator;

        private DateTime modifiedTime;

        private UserKey modifier;

        private Icon icon;

        private RootDataSet dataSet = new RootDataSet();

        private ItemIndexConfig itemIndexConfig;

        public Builder()
        {
        }

        public Builder( final Item item )
        {
            this.id = item.id;
            this.name = item.name;
            this.parent = item.parent;
            this.createdTime = item.createdTime;
            this.creator = item.creator;
            this.modifiedTime = item.modifiedTime;
            this.modifier = item.modifier;
            this.icon = item.icon;
            this.dataSet = item.rootDataSet;
            this.itemIndexConfig = item.itemIndexConfig;
        }

        public Builder( final ItemId id )
        {
            this.id = id;
        }

        public Builder( final ItemId id, final String name )
        {
            this.id = id;
            this.name = name;
        }

        public Builder id( final ItemId value )
        {
            this.id = value;
            return this;
        }

        public Builder name( final String value )
        {
            this.name = value;
            return this;
        }

        public Builder path( final String value )
        {
            this.parent = new ItemPath( value );
            return this;
        }

        public Builder parent( final ItemPath value )
        {
            this.parent = value;
            return this;
        }

        public Builder createdTime( final DateTime value )
        {
            this.createdTime = value;
            return this;
        }

        public Builder creator( final UserKey value )
        {
            this.creator = value;
            return this;
        }

        public Builder modifiedTime( final DateTime value )
        {
            this.modifiedTime = value;
            return this;
        }

        public Builder modifier( final UserKey value )
        {
            this.modifier = value;
            return this;
        }

        public Builder itemIndexConfig( final ItemIndexConfig itemIndexConfig )
        {
            this.itemIndexConfig = itemIndexConfig;
            return this;
        }

        public Builder icon( final Icon value )
        {
            this.icon = value;
            return this;
        }

        public Builder property( final String path, final String value )
        {
            if ( value != null )
            {
                this.dataSet.setProperty( path, new Value.String( value ) );
            }
            return this;
        }

        public Builder property( final String path, final Long value )
        {
            if ( value != null )
            {
                this.dataSet.setProperty( path, new Value.Long( value ) );
            }
            return this;
        }

        public Builder property( final String path, final DateTime value )
        {

            if ( value != null )
            {
                this.dataSet.setProperty( path, new Value.DateTime( value ) );
            }
            return this;
        }

        public Builder addDataSet( final DataSet value )
        {
            if ( value != null )
            {
                this.dataSet.add( value );
            }
            return this;
        }

        public Builder rootDataSet( final RootDataSet value )
        {
            this.dataSet = value;
            return this;
        }

        public Item build()
        {
            return new Item( this );
        }
    }
}
