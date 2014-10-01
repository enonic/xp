package com.enonic.wem.core.entity;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.support.ChangeTraceable;
import com.enonic.wem.api.support.illegaledit.IllegalEdit;
import com.enonic.wem.api.support.illegaledit.IllegalEditAware;
import com.enonic.wem.api.support.illegaledit.IllegalEditException;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public final class Node
    extends Entity
    implements ChangeTraceable, IllegalEditAware<Node>
{
    private final NodeName name;

    private final NodePath parent;

    private final NodePath path;

    private final UserKey modifier;

    private final UserKey creator;

    private final boolean hasChildren;

    private Node( final BaseBuilder builder )
    {
        super( builder );

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

    @Override
    public void checkIllegalEdit( final Node to )
        throws IllegalEditException
    {
        // TODO: Unfortunately Java does not like us to also let super class implement checkIllegalEdit(Entity)
        // TODO: Therefor it's here... :(
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
        extends Entity.BaseBuilder
    {
        NodeName name;

        NodePath parent;

        UserKey modifier;

        UserKey creator;

        boolean hasChildren = false;

        BaseBuilder()
        {
        }

        BaseBuilder( final EntityId id )
        {
            super( id );
        }

        BaseBuilder( final Node node )
        {
            super( node );

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

    public static class EditBuilder
        extends Entity.EditBuilder<EditBuilder>
    {
        private final Node originalNode;

        private NodeName name;

        public EditBuilder( final Node original )
        {
            super( original );
            this.name = original.name;
            this.originalNode = original;
        }

        public EditBuilder name( final NodeName value )
        {
            changes.recordChange( newPossibleChange( "name" ).from( this.originalNode.name ).to( value ).build() );
            this.name = value;
            return this;
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

    public static class Builder
        extends Entity.Builder<Builder>
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
            super( id );
        }

        public Builder( final Node node )
        {
            super( node );
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

        if ( !super.equals( o ) )
        {
            return false;
        }

        final Node node = (Node) o;

        if ( creator != null ? !creator.equals( node.creator ) : node.creator != null )
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
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + ( parent != null ? parent.hashCode() : 0 );
        result = 31 * result + ( path != null ? path.hashCode() : 0 );
        result = 31 * result + ( modifier != null ? modifier.hashCode() : 0 );
        result = 31 * result + ( creator != null ? creator.hashCode() : 0 );
        return result;
    }
}
