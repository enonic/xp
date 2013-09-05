package com.enonic.wem.api.item;


import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;

public final class Item
    extends DataSet
{
    private final ItemId id;

    public Item( final ItemId id, final String name, final DataSet dataSet )
    {
        super( name );
        this.id = id;
        for ( final Data data : dataSet )
        {
            add( data.copy() );
        }
    }

    private Item( final Builder builder )
    {
        super( builder.name );
        this.id = builder.id;
        for ( final Data data : builder.dataSet )
        {
            add( data.copy() );
        }
    }

    public ItemId getId()
    {
        return id;
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

        private DataSet dataSet = new RootDataSet();

        public Builder( final Item item )
        {
            this.name = item.getName();
            this.dataSet = item;
        }

        public Builder( final ItemId id, final String name )
        {
            this.id = id;
            this.name = name;
        }

        public Builder property( final String name, final Value value )
        {
            this.dataSet.setProperty( name, value );
            return this;
        }

        public Builder addDataSet( final DataSet value )
        {
            this.dataSet.add( value );
            return this;
        }

        public Item build()
        {
            return new Item( this );
        }
    }
}
