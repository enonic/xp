package com.enonic.wem.api.entity;

import org.joda.time.DateTime;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;

public class Entity
{
    protected final EntityId id;

    protected final DateTime createdTime;

    protected final RootDataSet rootDataSet;

    protected final DateTime modifiedTime;

    protected final EntityIndexConfig entityIndexConfig;

    protected Entity( final Builder builder )
    {
        this.id = builder.id;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;

        this.rootDataSet = new RootDataSet();
        if ( builder.dataSet != null )
        {
            for ( final Data data : builder.dataSet )
            {
                this.rootDataSet.add( data.copy() );
            }
        }

        this.entityIndexConfig = builder.entityIndexConfig;
    }

    public static class Builder<B extends Builder>
    {
        protected EntityId id;

        protected DateTime createdTime;

        protected DateTime modifiedTime;

        protected RootDataSet dataSet = new RootDataSet();

        protected EntityIndexConfig entityIndexConfig;

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

        public B modifiedTime( final DateTime value )
        {
            this.modifiedTime = value;
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

        public B property( final String path, final Value value )
        {

            if ( value != null )
            {
                this.dataSet.setProperty( path, value );
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

        public B entityIndexConfig( final EntityIndexConfig entityIndexConfig )
        {
            this.entityIndexConfig = entityIndexConfig;
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
