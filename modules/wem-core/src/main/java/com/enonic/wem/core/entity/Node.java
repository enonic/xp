package com.enonic.wem.core.entity;


import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.index.PatternIndexConfigDocument;
import com.enonic.wem.api.support.ChangeTraceable;
import com.enonic.wem.api.support.Changes;
import com.enonic.wem.api.support.illegaledit.IllegalEdit;
import com.enonic.wem.api.support.illegaledit.IllegalEditAware;
import com.enonic.wem.api.support.illegaledit.IllegalEditException;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public final class Node
    //extends Entity
    implements ChangeTraceable, IllegalEditAware<Node>
{
    private final EntityId id;

    private final NodeName name;

    private final NodePath parent;

    private final NodePath path;

    private final UserKey modifier;

    private final UserKey creator;

    private final boolean hasChildren;

    private final Instant createdTime;

    private final RootDataSet data;

    private final Instant modifiedTime;

    private final IndexConfigDocument indexConfigDocument;

    private final Attachments attachments;

    public NodeName name()
    {
        return name;
    }

    public NodePath parent()
    {
        return parent;
    }

    public NodePath path()
    {
        return path;
    }

    public UserKey creator()
    {
        return creator;
    }

    public UserKey getCreator()
    {
        return creator;
    }

    public UserKey modifier()
    {
        return modifier;
    }

    public UserKey getModifier()
    {
        return modifier;
    }

    public boolean getHasChildren()
    {
        return hasChildren;
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

    public IndexConfigDocument getIndexConfigDocument()
    {
        return indexConfigDocument;
    }

    private Node( final BaseBuilder builder )
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

        if ( builder.indexConfigDocument != null )
        {
            this.indexConfigDocument = builder.indexConfigDocument;
        }
        else
        {
            this.indexConfigDocument = PatternIndexConfigDocument.create().
                defaultConfig( IndexConfig.BY_TYPE ).
                build();
        }

        this.creator = builder.creator;

        this.name = builder.name;
        this.parent = builder.parent;

        this.path = this.parent != null && this.name != null ? new NodePath( this.parent, this.name ) : null;

        this.modifier = builder.modifier;

        this.hasChildren = builder.hasChildren;
    }

    public void validateForIndexing()
    {
        Preconditions.checkNotNull( this.id, "Id must be set" );
        Preconditions.checkNotNull( this.indexConfigDocument, "EntityIndexConfig must be set" );
    }


    @Override
    public void checkIllegalEdit( final Node to )
        throws IllegalEditException
    {
        IllegalEdit.check( "id", this.id(), to.id(), Node.class );
        IllegalEdit.check( "createdTime", this.getCreatedTime(), to.getCreatedTime(), Node.class );
        IllegalEdit.check( "modifiedTime", this.getModifiedTime(), to.getModifiedTime(), Node.class );

        IllegalEdit.check( "parent", this.parent(), to.parent(), Node.class );
        IllegalEdit.check( "creator", this.creator(), to.creator(), Node.class );
        IllegalEdit.check( "modifier", this.modifier(), to.modifier(), Node.class );
    }

    public String toString()
    {
        return this.path.toString();
    }

    public static Builder newNode()
    {
        return new Builder();
    }

    public static Builder newNode( final EntityId id )
    {
        return new Builder( id );
    }

    public static Builder newNode( final Node node )
    {
        return new Builder( node );
    }

    public static EditBuilder editNode( final Node original )
    {
        return new EditBuilder( original );
    }


    public static class BaseBuilder
    {
        NodeName name;

        NodePath parent;

        UserKey modifier;

        UserKey creator;

        boolean hasChildren = false;

        EntityId id;

        Instant createdTime;

        Instant modifiedTime;

        RootDataSet data = new RootDataSet();

        Attachments attachments;

        IndexConfigDocument indexConfigDocument;


        BaseBuilder()
        {
        }

        BaseBuilder( final Node node )
        {
            this.id = node.id;
            this.createdTime = node.createdTime;
            this.modifiedTime = node.modifiedTime;
            this.data = node.data;
            this.indexConfigDocument = node.indexConfigDocument;
            this.name = node.name;
            this.parent = node.parent;
            this.creator = node.creator;
            this.modifier = node.modifier;
        }

        BaseBuilder( final EntityId id, final NodeName name )
        {
            this.id = id;
            this.name = name;
        }
    }

    public static class Builder
        extends BaseBuilder
    {
        private NodeName name;

        private NodePath parent;

        private UserKey modifier;

        private UserKey creator;

        boolean hasChildren = false;

        public Builder()
        {
            super();
        }

        public Builder( final EntityId id )
        {
            this.id = id;
        }

        public Builder( final Node node )
        {
            this.id = node.id;
            this.createdTime = node.createdTime;
            this.modifiedTime = node.modifiedTime;
            this.data = node.data;
            this.attachments = node.attachments;
            this.name = node.name;
            this.parent = node.parent;
            this.modifier = node.modifier;
            this.creator = node.creator;
        }

        public Builder( final EntityId id, final NodeName name )
        {
            this.id = id;
            this.name = name;
        }

        public Builder name( final NodeName value )
        {
            this.name = value;
            return this;
        }

        public Builder path( final String value )
        {
            this.parent = new NodePath( value );
            return this;
        }

        public Builder parent( final NodePath value )
        {
            this.parent = value;
            return this;
        }

        public Builder creator( final UserKey value )
        {
            this.creator = value;
            return this;
        }

        public Builder modifier( final UserKey value )
        {
            this.modifier = value;
            return this;
        }

        public Builder hasChildren( final boolean hasChildren )
        {
            this.hasChildren = hasChildren;
            return this;
        }

        public Builder id( final EntityId id )
        {
            this.id = id;
            return this;
        }

        public Builder createdTime( final Instant value )
        {
            this.createdTime = value;
            return this;
        }

        public Builder modifiedTime( final Instant value )
        {
            this.modifiedTime = value;
            return this;
        }

        public Builder property( final String path, final String value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, Value.newString( value ) );
            }
            return this;
        }

        public Builder property( final String path, final Long value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, Value.newLong( value ) );
            }
            return this;
        }

        public Builder property( final String path, final Instant value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, Value.newInstant( value ) );
            }
            return this;
        }

        public Builder property( final String path, final Value value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, value );
            }
            return this;
        }

        public Builder addDataSet( final DataSet value )
        {
            if ( value != null )
            {
                this.data.add( value );
            }
            return this;
        }

        public Builder rootDataSet( final RootDataSet value )
        {
            this.data = value;
            return this;
        }

        public Builder indexConfigDocument( final IndexConfigDocument indexConfigDocument )
        {
            this.indexConfigDocument = indexConfigDocument;
            return this;
        }


        public Builder attachments( final Attachments value )
        {
            this.attachments = value;
            return this;
        }

        public Node build()
        {
            BaseBuilder baseBuilder = new BaseBuilder();
            baseBuilder.id = this.id;
            baseBuilder.createdTime = this.createdTime;
            baseBuilder.modifiedTime = this.modifiedTime;
            baseBuilder.data = this.data;
            baseBuilder.attachments = this.attachments;
            baseBuilder.name = this.name;
            baseBuilder.parent = this.parent;
            baseBuilder.creator = this.creator;
            baseBuilder.modifier = this.modifier;
            baseBuilder.indexConfigDocument = this.indexConfigDocument;
            baseBuilder.hasChildren = this.hasChildren;

            return new Node( baseBuilder );
        }
    }


    public static class EditBuilder
        extends BaseBuilder
    {
        private final Node originalNode;

        private NodeName name;

        final Changes.Builder changes = new Changes.Builder();

        public EditBuilder( final Node original )
        {
            this.name = original.name;
            this.originalNode = original;
        }

        public EditBuilder name( final NodeName value )
        {
            changes.recordChange( newPossibleChange( "name" ).from( this.originalNode.name ).to( value ).build() );
            this.name = value;
            return this;
        }

        public EditBuilder property( final String path, final String value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, Value.newString( value ) );
                changes.recordChange( newPossibleChange( "data" ).from( this.originalNode.data() ).to( this.data ).build() );
            }
            return this;
        }

        public EditBuilder property( final String path, final Long value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, Value.newLong( value ) );
                changes.recordChange( newPossibleChange( "data" ).from( this.originalNode.data() ).to( this.data ).build() );
            }
            return this;
        }

        public EditBuilder property( final String path, final Instant value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, Value.newInstant( value ) );
                changes.recordChange( newPossibleChange( "data" ).from( this.originalNode.data() ).to( this.data ).build() );
            }
            return this;
        }

        public EditBuilder property( final String path, final Value value )
        {
            if ( value != null )
            {
                this.data.setProperty( path, value );
                changes.recordChange( newPossibleChange( "data" ).from( this.originalNode.data() ).to( this.data ).build() );
            }
            return this;
        }

        public EditBuilder addDataSet( final DataSet value )
        {
            if ( value != null )
            {
                this.data.add( value );
                changes.recordChange( newPossibleChange( "data" ).from( this.originalNode.data() ).to( this.data ).build() );
            }
            return this;
        }

        public EditBuilder rootDataSet( final RootDataSet value )
        {
            this.data = value;
            changes.recordChange( newPossibleChange( "data" ).from( this.originalNode.data() ).to( this.data ).build() );
            return this;
        }

        public EditBuilder attachments( final Attachments value )
        {
            this.attachments = value;
            changes.recordChange(
                newPossibleChange( "attachments" ).from( this.originalNode.attachments() ).to( this.attachments ).build() );
            return this;
        }

        public EditBuilder entityIndexConfig( final IndexConfigDocument indexConfigDocument )
        {
            changes.recordChange(
                newPossibleChange( "data" ).from( this.originalNode.indexConfigDocument ).to( this.indexConfigDocument ).build() );
            this.indexConfigDocument = indexConfigDocument;
            return this;
        }

        public boolean isChanges()
        {
            return this.changes.isChanges();
        }

        public Changes getChanges()
        {
            return this.changes.build();
        }

        public Node build()
        {
            Node.BaseBuilder baseBuilder = new BaseBuilder( this.originalNode );
            baseBuilder.data = this.data;
            baseBuilder.attachments = this.attachments;
            baseBuilder.indexConfigDocument = this.indexConfigDocument;

            baseBuilder.name = this.name;
            return new Node( baseBuilder );
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

        final Node node = (Node) o;

        if ( hasChildren != node.hasChildren )
        {
            return false;
        }
        if ( attachments != null ? !attachments.equals( node.attachments ) : node.attachments != null )
        {
            return false;
        }
        if ( createdTime != null ? !createdTime.equals( node.createdTime ) : node.createdTime != null )
        {
            return false;
        }
        if ( creator != null ? !creator.equals( node.creator ) : node.creator != null )
        {
            return false;
        }
        if ( data != null ? !data.equals( node.data ) : node.data != null )
        {
            return false;
        }
        if ( id != null ? !id.equals( node.id ) : node.id != null )
        {
            return false;
        }
        if ( indexConfigDocument != null ? !indexConfigDocument.equals( node.indexConfigDocument ) : node.indexConfigDocument != null )
        {
            return false;
        }
        if ( modifiedTime != null ? !modifiedTime.equals( node.modifiedTime ) : node.modifiedTime != null )
        {
            return false;
        }
        if ( modifier != null ? !modifier.equals( node.modifier ) : node.modifier != null )
        {
            return false;
        }
        if ( name != null ? !name.equals( node.name ) : node.name != null )
        {
            return false;
        }
        if ( parent != null ? !parent.equals( node.parent ) : node.parent != null )
        {
            return false;
        }
        if ( path != null ? !path.equals( node.path ) : node.path != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        result = 31 * result + ( parent != null ? parent.hashCode() : 0 );
        result = 31 * result + ( path != null ? path.hashCode() : 0 );
        result = 31 * result + ( modifier != null ? modifier.hashCode() : 0 );
        result = 31 * result + ( creator != null ? creator.hashCode() : 0 );
        result = 31 * result + ( hasChildren ? 1 : 0 );
        result = 31 * result + ( createdTime != null ? createdTime.hashCode() : 0 );
        result = 31 * result + ( data != null ? data.hashCode() : 0 );
        result = 31 * result + ( modifiedTime != null ? modifiedTime.hashCode() : 0 );
        result = 31 * result + ( indexConfigDocument != null ? indexConfigDocument.hashCode() : 0 );
        result = 31 * result + ( attachments != null ? attachments.hashCode() : 0 );
        return result;
    }
}
