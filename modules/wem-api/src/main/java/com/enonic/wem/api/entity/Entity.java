package com.enonic.wem.api.entity;

import java.time.Instant;

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

    protected final Instant createdTime;

    protected final RootDataSet data;

    protected final Instant modifiedTime;

    protected final NodeIndexConfig nodeIndexConfig;

    protected final Attachments attachments;

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

        this.attachments = builder.attachments;

        if ( builder.nodeIndexConfig != null )
        {
            this.nodeIndexConfig = builder.nodeIndexConfig;
        }
        else
        {
            this.nodeIndexConfig = NodePropertyIndexConfig.create().build();
        }

    }

    public EntityId id()
    {
        return id;
    }

    public Instant getCreatedTime()
    {
        return createdTime;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public RootDataSet data()
    {
        return this.data;
    }

    public Attachments attachments()
    {
        return this.attachments;
    }

    public DataSet dataSet( final String path )
    {
        return data.getDataSet( path );
    }

    public Property property( final String path )
    {
        return data.getProperty( path );
    }

    public NodeIndexConfig getNodeIndexConfig()
    {
        return nodeIndexConfig;
    }

    public static class BaseBuilder
    {
        EntityId id;

        Instant createdTime;

        Instant modifiedTime;

        RootDataSet data = new RootDataSet();

        Attachments attachments;

        NodeIndexConfig nodeIndexConfig;

        BaseBuilder()
        {
        }

        BaseBuilder( final Entity entity )
        {
            this.id = entity.id;
            this.createdTime = entity.createdTime;
            this.modifiedTime = entity.modifiedTime;
            this.data = entity.data;
            this.nodeIndexConfig = entity.nodeIndexConfig;
        }

        BaseBuilder( final EntityId id )
        {
            this.id = id;
        }
    }

    public static class EditBuilder<B extends EditBuilder>
        extends BaseBuilder
    {
        private final Entity originalEntity;

        protected final Changes.Builder changes = new Changes.Builder();

        public EditBuilder( final Entity original )
        {
            super( original );
            this.originalEntity = original;
        }

        public B property( final String path, final String value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, Value.newString( value ) );
                changes.recordChange( newPossibleChange( "data" ).from( this.originalEntity.data() ).to( this.data ).build() );
            }
            return getThisBuilder();
        }

        public B property( final String path, final Long value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, Value.newLong( value ) );
                changes.recordChange( newPossibleChange( "data" ).from( this.originalEntity.data() ).to( this.data ).build() );
            }
            return getThisBuilder();
        }

        public B property( final String path, final Instant value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, Value.newInstant( value ) );
                changes.recordChange( newPossibleChange( "data" ).from( this.originalEntity.data() ).to( this.data ).build() );
            }
            return getThisBuilder();
        }

        public B property( final String path, final Value value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, value );
                changes.recordChange( newPossibleChange( "data" ).from( this.originalEntity.data() ).to( this.data ).build() );
            }
            return getThisBuilder();
        }

        public B addDataSet( final DataSet value )
        {
            if ( value != null )
            {
                this.data.add( value );
                changes.recordChange( newPossibleChange( "data" ).from( this.originalEntity.data() ).to( this.data ).build() );
            }
            return getThisBuilder();
        }

        public B rootDataSet( final RootDataSet value )
        {
            this.data = value;
            changes.recordChange( newPossibleChange( "data" ).from( this.originalEntity.data() ).to( this.data ).build() );
            return getThisBuilder();
        }

        public B attachments( final Attachments value )
        {
            this.attachments = value;
            changes.recordChange(
                newPossibleChange( "attachments" ).from( this.originalEntity.attachments() ).to( this.attachments ).build() );
            return getThisBuilder();
        }

        public B entityIndexConfig( final NodeIndexConfig nodeIndexConfig )
        {
            changes.recordChange(
                newPossibleChange( "data" ).from( this.originalEntity.nodeIndexConfig ).to( this.nodeIndexConfig ).build() );
            this.nodeIndexConfig = nodeIndexConfig;
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
            this.id = entity.id;
            this.createdTime = entity.createdTime;
            this.modifiedTime = entity.modifiedTime;
            this.data = entity.data;
            this.nodeIndexConfig = entity.nodeIndexConfig;
            this.attachments = entity.attachments;
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

        public B createdTime( final Instant value )
        {
            this.createdTime = value;
            return getThisBuilder();
        }

        public B modifiedTime( final Instant value )
        {
            this.modifiedTime = value;
            return getThisBuilder();
        }

        public B property( final String path, final String value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, Value.newString( value ) );
            }
            return getThisBuilder();
        }

        public B property( final String path, final Long value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, Value.newLong( value ) );
            }
            return getThisBuilder();
        }

        public B property( final String path, final Instant value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, Value.newInstant( value ) );
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

        public B entityIndexConfig( final NodeIndexConfig nodeIndexConfig )
        {
            this.nodeIndexConfig = nodeIndexConfig;
            return getThisBuilder();
        }

        public B attachments( final Attachments value )
        {
            this.attachments = value;
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


    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Entity entity = (Entity) o;

        if ( attachments != null ? !attachments.equals( entity.attachments ) : entity.attachments != null )
        {
            return false;
        }
        if ( createdTime != null ? !createdTime.equals( entity.createdTime ) : entity.createdTime != null )
        {
            return false;
        }
        if ( data != null ? !data.equals( entity.data ) : entity.data != null )
        {
            return false;
        }
        if ( nodeIndexConfig != null ? !nodeIndexConfig.equals( entity.nodeIndexConfig ) : entity.nodeIndexConfig != null )
        {
            return false;
        }
        if ( id != null ? !id.equals( entity.id ) : entity.id != null )
        {
            return false;
        }
        if ( modifiedTime != null ? !modifiedTime.equals( entity.modifiedTime ) : entity.modifiedTime != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + ( createdTime != null ? createdTime.hashCode() : 0 );
        result = 31 * result + ( data != null ? data.hashCode() : 0 );
        result = 31 * result + ( modifiedTime != null ? modifiedTime.hashCode() : 0 );
        result = 31 * result + ( nodeIndexConfig != null ? nodeIndexConfig.hashCode() : 0 );
        result = 31 * result + ( attachments != null ? attachments.hashCode() : 0 );
        return result;
    }
}
