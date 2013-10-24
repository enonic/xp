package com.enonic.wem.api.item;

import org.joda.time.DateTime;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;

public class Entity
{
    protected final EntityId id;

    protected final DateTime createdTime;

    protected final UserKey creator;

    protected final RootDataSet rootDataSet;

    protected Entity( final Builder builder )
    {
        this.id = builder.id;
        this.createdTime = builder.createdTime;
        this.creator = builder.creator;

        this.rootDataSet = new RootDataSet();
        if ( builder.dataSet != null )
        {
            for ( final Data data : builder.dataSet )
            {
                this.rootDataSet.add( data.copy() );
            }
        }
    }

    public static class Builder<B extends Builder>
    {
        protected EntityId id;

        protected DateTime createdTime = DateTime.now();

        protected UserKey creator;

        private RootDataSet dataSet = new RootDataSet();

        public Builder()
        {
        }

        public Builder( final EntityId id )
        {
            this.id = id;
        }

        public B id( final EntityId id )
        {
            this.id = id;
            return getThisBuilder();
        }

        public B createdTime( final DateTime value )
        {
            this.createdTime = value;
            return getThisBuilder();
        }

        public B creator( final UserKey value )
        {
            this.creator = value;
            return getThisBuilder();
        }

        public B property( final String path, final String value )
        {
            if ( value != null )
            {
                this.dataSet.setProperty( path, new Value.String( value ) );
            }
            return getThisBuilder();
        }

        public B property( final String path, final Long value )
        {
            if ( value != null )
            {
                this.dataSet.setProperty( path, new Value.Long( value ) );
            }
            return getThisBuilder();
        }

        public B property( final String path, final DateTime value )
        {

            if ( value != null )
            {
                this.dataSet.setProperty( path, new Value.DateTime( value ) );
            }
            return getThisBuilder();
        }

        public B addDataSet( final DataSet value )
        {
            if ( value != null )
            {
                this.dataSet.add( value );
            }
            return getThisBuilder();
        }

        public B rootDataSet( final RootDataSet value )
        {
            this.dataSet = value;
            return getThisBuilder();
        }


        public Entity build()
        {
            return new Entity( this );
        }

        @SuppressWarnings("unchecked")
        private B getThisBuilder()
        {
            return (B) this;
        }

    }

}
