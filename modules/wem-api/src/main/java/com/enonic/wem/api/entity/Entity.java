package com.enonic.wem.api.entity;

import org.joda.time.DateTime;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.support.Changes;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public class Entity
{
    protected final EntityId id;

    protected final DateTime createdTime;

    protected final RootDataSet data;

    protected final DateTime modifiedTime;

    protected final EntityIndexConfig entityIndexConfig;

    protected Entity( final BaseBuilder builder )
    {
        this.id = builder.id;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;

        this.data = new RootDataSet();
        if ( builder.data != null )
        {
            for ( final Data data : builder.data )
            {
                this.data.add( data.copy() );
            }
        }

        if ( builder.entityIndexConfig != null )
        {
            this.entityIndexConfig = builder.entityIndexConfig;
        }
        else
        {
            this.entityIndexConfig = EntityIndexConfig.newEntityIndexConfig().build();
        }

    }

    public EntityId id()
    {
        return id;
    }

    public DateTime getCreatedTime()
    {
        return createdTime;
    }

    public DateTime getModifiedTime()
    {
        return modifiedTime;
    }

    public RootDataSet data()
    {
        return this.data;
    }

    public DataSet dataSet( final String path )
    {
        return data.getDataSet( path );
    }

    public Property property( final String path )
    {
        return data.getProperty( path );
    }

    public EntityIndexConfig getEntityIndexConfig()
    {
        return entityIndexConfig;
    }

    public static class BaseBuilder
    {
        EntityId id;

        DateTime createdTime;

        DateTime modifiedTime;

        RootDataSet data = new RootDataSet();

        EntityIndexConfig entityIndexConfig;

        BaseBuilder()
        {
        }

        BaseBuilder( final Entity entity )
        {
            this.id = entity.id;
            this.createdTime = entity.createdTime;
            this.modifiedTime = entity.modifiedTime;
            this.data = entity.data;
            this.entityIndexConfig = entity.entityIndexConfig;
        }

        BaseBuilder( final EntityId id )
        {
            this.id = id;
        }
    }

    public static class EditBuilder<B extends EditBuilder>
        extends BaseBuilder
    {
        private final Entity original;

        private final Changes.Builder changes = new Changes.Builder();

        public EditBuilder( final Entity original )
        {
            super( original );
            this.original = original;
        }

        public B property( final String path, final String value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, new Value.String( value ) );
                changes.recordChange( newPossibleChange( "data" ).from( this.original.data() ).to( this.data ).build() );
            }
            return getThisBuilder();
        }

        public B property( final String path, final Long value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, new Value.Long( value ) );
                changes.recordChange( newPossibleChange( "data" ).from( this.original.data() ).to( this.data ).build() );
            }
            return getThisBuilder();
        }

        public B property( final String path, final DateTime value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, new Value.DateTime( value ) );
                changes.recordChange( newPossibleChange( "data" ).from( this.original.data() ).to( this.data ).build() );
            }
            return getThisBuilder();
        }

        public B property( final String path, final Value value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, value );
                changes.recordChange( newPossibleChange( "data" ).from( this.original.data() ).to( this.data ).build() );
            }
            return getThisBuilder();
        }

        public B addDataSet( final DataSet value )
        {
            if ( value != null )
            {
                this.data.add( value );
                changes.recordChange( newPossibleChange( "data" ).from( this.original.data() ).to( this.data ).build() );
            }
            return getThisBuilder();
        }

        public B rootDataSet( final RootDataSet value )
        {
            this.data = value;
            changes.recordChange( newPossibleChange( "data" ).from( this.original.data() ).to( this.data ).build() );
            return getThisBuilder();
        }

        public B entityIndexConfig( final EntityIndexConfig entityIndexConfig )
        {
            changes.recordChange(
                newPossibleChange( "data" ).from( this.original.entityIndexConfig ).to( this.entityIndexConfig ).build() );
            this.entityIndexConfig = entityIndexConfig;
            return getThisBuilder();
        }

        public boolean isChanges()
        {
            return this.changes.isChanges();
        }

        public Changes getChanges()
        {
            return this.changes.build();
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


    public static class Builder<B extends Builder>
        extends BaseBuilder
    {
        public Builder()
        {
        }

        public Builder( final Entity entity )
        {
            this.createdTime = entity.createdTime;
            this.modifiedTime = entity.modifiedTime;
            this.data = entity.data;
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
                this.data.setProperty( path, new Value.String( value ) );
            }
            return getThisBuilder();
        }

        public B property( final String path, final Long value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, new Value.Long( value ) );
            }
            return getThisBuilder();
        }

        public B property( final String path, final DateTime value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, new Value.DateTime( value ) );
            }
            return getThisBuilder();
        }

        public B property( final String path, final Value value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, value );
            }
            return getThisBuilder();
        }

        public B addDataSet( final DataSet value )
        {
            if ( value != null )
            {
                this.data.add( value );
            }
            return getThisBuilder();
        }

        public B rootDataSet( final RootDataSet value )
        {
            this.data = value;
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
