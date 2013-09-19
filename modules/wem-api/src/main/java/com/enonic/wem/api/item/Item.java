package com.enonic.wem.api.item;


import org.joda.time.DateTime;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;

public final class Item
{
    private final ItemId id;

    private final String name;

    private final ItemPath path;

    private final DateTime createdTime;

    private final DateTime modifiedTime;

    private final RootDataSet rootDataSet;

    private Item( final Builder builder )
    {
        this.id = builder.id;
        this.name = builder.name;
        this.path = builder.path;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.rootDataSet = new RootDataSet();
        for ( final Data data : builder.dataSet )
        {
            this.rootDataSet.add( data.copy() );
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

    public ItemPath path()
    {
        return path;
    }

    public DateTime createdTime()
    {
        return createdTime;
    }

    public DateTime modifiedTime()
    {
        return modifiedTime;
    }

    public Property property( final String path )
    {
        return rootDataSet.getProperty( path );
    }

    public DataSet dataSet( final String path )
    {
        return rootDataSet.getDataSet( path );
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

        private ItemPath path;

        private DateTime createdTime;

        private DateTime modifiedTime;

        private RootDataSet dataSet = new RootDataSet();

        public Builder( final Item item )
        {
            this.id = item.id;
            this.name = item.name;
            this.dataSet = item.rootDataSet;
        }

        public Builder( final ItemId id, final String name )
        {
            this.id = id;
            this.name = name;
        }

        public Builder name( final String value )
        {
            this.name = value;
            return this;
        }

        public Builder path( final String value )
        {
            this.path = new ItemPath( value );
            return this;
        }

        public Builder createdTime( final DateTime value )
        {
            this.createdTime = value;
            return this;
        }

        public Builder modifiedTime( final DateTime value )
        {
            this.modifiedTime = value;
            return this;
        }

        public Builder property( final String path, final Value value )
        {
            this.dataSet.setProperty( path, value );
            return this;
        }

        public Builder addDataSet( final DataSet value )
        {
            this.dataSet.add( value );
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
