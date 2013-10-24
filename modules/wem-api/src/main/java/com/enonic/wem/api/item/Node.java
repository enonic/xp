package com.enonic.wem.api.item;


import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
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

    private final DateTime modifiedTime;

    private final UserKey modifier;

    // TODO: Remove
    private final Icon icon;

    private final EntityIndexConfig entityIndexConfig;

    private Node( final Builder builder )
    {
        super( builder );
        Preconditions.checkNotNull( builder.parent, "parent must be specified" );
        Preconditions.checkNotNull( builder.parent, "name must be specified" );

        this.name = builder.name;
        this.parent = builder.parent;
        this.path = new NodePath( this.parent, this.name );

        this.modifiedTime = builder.modifiedTime;
        this.modifier = builder.modifier;
        this.icon = builder.icon;
        this.entityIndexConfig = builder.entityIndexConfig;
    }

    public EntityId id()
    {
        return id;
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

    public DateTime getCreatedTime()
    {
        return createdTime;
    }

    public UserKey creator()
    {
        return creator;
    }

    public UserKey getCreator()
    {
        return creator;
    }

    public DateTime getModifiedTime()
    {
        return modifiedTime;
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

    public RootDataSet rootDataSet()
    {
        return this.rootDataSet;
    }

    public Property property( final String path )
    {
        return rootDataSet.getProperty( path );
    }

    public EntityIndexConfig getEntityIndexConfig()
    {
        return entityIndexConfig;
    }

    public DataSet dataSet( final String path )
    {
        return rootDataSet.getDataSet( path );
    }

    @Override
    public void checkIllegalEdit( final Node to )
        throws IllegalEditException
    {
        IllegalEdit.check( "id", this.id(), to.id(), Node.class );
        IllegalEdit.check( "name", this.name(), to.name(), Node.class );
        IllegalEdit.check( "parent", this.parent(), to.parent(), Node.class );
        IllegalEdit.check( "path", this.path(), to.path(), Node.class );
        IllegalEdit.check( "createdTime", this.getCreatedTime(), to.getCreatedTime(), Node.class );
        IllegalEdit.check( "creator", this.creator(), to.creator(), Node.class );
        IllegalEdit.check( "modifiedTime", this.getModifiedTime(), to.getModifiedTime(), Node.class );
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

        private DateTime modifiedTime;

        private UserKey modifier;

        private Icon icon;

        private RootDataSet dataSet = new RootDataSet();

        private EntityIndexConfig entityIndexConfig;

        public Builder()
        {
        }

        public Builder( final EntityId id )
        {
            this.id = id;
        }

        public Builder( final Node node )
        {
            this.id = node.id;
            this.name = node.name;
            this.parent = node.parent;
            this.createdTime = node.createdTime;
            this.creator = node.creator;
            this.modifiedTime = node.modifiedTime;
            this.modifier = node.modifier;
            this.icon = node.icon;
            this.dataSet = node.rootDataSet;
            this.entityIndexConfig = node.entityIndexConfig;
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

        public Builder modifiedTime( final DateTime value )
        {
            this.modifiedTime = value;
            return this;
        }

        public Builder modifier( final UserKey value )
        {
            this.modifier = value;
            return this;
        }

        public Builder itemIndexConfig( final EntityIndexConfig entityIndexConfig )
        {
            this.entityIndexConfig = entityIndexConfig;
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
