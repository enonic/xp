package com.enonic.wem.api.entity;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.support.ChangeTraceable;
import com.enonic.wem.api.support.illegaledit.IllegalEdit;
import com.enonic.wem.api.support.illegaledit.IllegalEditAware;
import com.enonic.wem.api.support.illegaledit.IllegalEditException;

public final class Node
    extends Entity
    implements ChangeTraceable, IllegalEditAware<Node>
{
    private final String name;

    private final NodePath parent;

    private final NodePath path;

    private final UserKey modifier;

    private final UserKey creator;

    // TODO: Remove
    private final Icon icon;

    private Node( final Builder builder )
    {
        super( builder );

        this.creator = builder.creator;

        this.name = builder.name;
        this.parent = builder.parent;

        this.path = this.parent != null && this.name != null ? new NodePath( this.parent, this.name ) : null;

        this.modifier = builder.modifier;
        this.icon = builder.icon;
    }

    public void validateForIndexing()
    {
        Preconditions.checkNotNull( this.id, "Id must be set" );
        Preconditions.checkNotNull( this.entityIndexConfig, "EntityIndexConfig must be set" );
    }

    public void validateForPersistence()
    {
        Preconditions.checkNotNull( this.createdTime, "createdTime must be set" );
        Preconditions.checkNotNull( this.id, "Id must be set" );
        Preconditions.checkNotNull( this.name, "Name must be set" );
        Preconditions.checkNotNull( this.creator, "creator must be set" );
        Preconditions.checkNotNull( this.parent, "parent must be set" );
    }

    public String name()
    {
        return name;
    }

    public NodePath parent()
    {
        return path;
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

    public Icon icon()
    {
        return icon;
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

        IllegalEdit.check( "name", this.name(), to.name(), Node.class );
        IllegalEdit.check( "parent", this.parent(), to.parent(), Node.class );
        IllegalEdit.check( "path", this.path(), to.path(), Node.class );
        IllegalEdit.check( "creator", this.creator(), to.creator(), Node.class );
        IllegalEdit.check( "modifier", this.modifier(), to.modifier(), Node.class );
    }

    public static Builder newNode()
    {
        return new Builder();
    }

    public static Builder newNode( final EntityId id )
    {
        return new Builder( id );
    }

    public static Builder newNode( final EntityId id, final String name )
    {
        return new Builder( id, name );
    }

    public static Builder newNode( final Node node )
    {
        return new Builder( node );
    }

    public static class Builder
        extends Entity.Builder<Builder>
    {
        private String name;

        private NodePath parent;

        private UserKey modifier;

        private UserKey creator;

        private Icon icon;

        public Builder()
        {
        }

        public Builder( final EntityId id )
        {
            this.id = id;
        }

        public Builder( final Node node )
        {
            super( node );
            this.id = node.id;
            this.name = node.name;
            this.parent = node.parent;
            this.creator = node.creator;
            this.modifier = node.modifier;
            this.icon = node.icon;
        }

        public Builder( final EntityId id, final String name )
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

        public Builder icon( final Icon value )
        {
            this.icon = value;
            return this;
        }

        public Node build()
        {
            return new Node( this );
        }
    }
}
