package com.enonic.wem.api.item;


import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;

public final class Item<T>
{
    private final String name;

    private final RootDataSet rootDataSet;

    public String getName()
    {
        return name;
    }

    public RootDataSet getRootDataSet()
    {
        return rootDataSet;
    }

    public Item( final Builder builder )
    {
        this.name = builder.name;
        this.rootDataSet = builder.rootDataSet;
    }

    public static Builder newItem()
    {
        return new Builder();
    }

    public static Builder newItem( final Item item )
    {
        return new Builder( item );
    }

    public static class Builder
    {
        private String name;

        private RootDataSet rootDataSet = new RootDataSet();

        public Builder( final Item item )
        {
            this.rootDataSet = (RootDataSet) item.rootDataSet.copy();
        }

        public Builder()
        {

        }

        public Builder name( final String value )
        {
            this.name = value;
            return this;
        }

        public Builder property( final String name, final Value value )
        {
            this.rootDataSet.setProperty( name, value );
            return this;
        }

        public Builder addDataSet( final DataSet value )
        {
            this.rootDataSet.add( value );
            return this;
        }

        public Builder rootDataSet( final RootDataSet value )
        {
            this.rootDataSet = value;
            return this;
        }

        public Item build()
        {
            return new Item( this );
        }
    }
}
