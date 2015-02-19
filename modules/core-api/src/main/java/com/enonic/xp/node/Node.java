package com.enonic.xp.node;

import java.time.Instant;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.security.acl.AccessControlList;

public class Node
{
    private final NodeId id;

    private final NodeName name;

    private final NodeId parentId;

    private final NodePath parentPath;

    private final NodeType nodeType;

    private final NodePath path;

    private final Instant timestamp;

    private final boolean hasChildren;

    private final PropertyTree data;

    private final IndexConfigDocument indexConfigDocument;

    private final ChildOrder childOrder;

    private final Long manualOrderValue;

    private final AccessControlList permissions;

    private final boolean inheritPermissions;

    private final AttachedBinaries attachedBinaries;

    private final NodeState nodeState;

    protected Node( final Builder builder )
    {
        Preconditions.checkNotNull( builder.permissions, "permissions are required" );
        Preconditions.checkNotNull( builder.data, "data are required" );

        this.id = builder.id;
        this.name = builder.name;
        this.parentId = builder.parentId;
        this.parentPath = builder.parentPath;
        this.nodeType = builder.nodeType;
        this.hasChildren = builder.hasChildren;
        this.data = builder.data;
        this.childOrder = builder.childOrder;
        this.manualOrderValue = builder.manualOrderValue;
        this.permissions = builder.permissions;
        this.inheritPermissions = builder.inheritPermissions;
        this.attachedBinaries = builder.attachedBinaries;
        this.nodeState = builder.nodeState;
        this.timestamp = builder.timestamp;

        this.path = this.parentPath != null && this.name != null ? new NodePath( this.parentPath, this.name ) : null;

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
    }

    public boolean isRoot()
    {
        return this instanceof RootNode;
    }

    public NodeName name()
    {
        return name;
    }

    public NodePath parentPath()
    {
        return parentPath;
    }

    public NodePath path()
    {
        return path;
    }

    public boolean getHasChildren()
    {
        return hasChildren;
    }

    public NodeId id()
    {
        return id;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public NodeId getParentId()
    {
        return parentId;
    }

    public PropertyTree data()
    {
        return this.data;
    }

    public IndexConfigDocument getIndexConfigDocument()
    {
        return indexConfigDocument;
    }

    public ChildOrder getChildOrder()
    {
        return childOrder;
    }

    public Long getManualOrderValue()
    {
        return manualOrderValue;
    }

    public AccessControlList getPermissions()
    {
        return permissions;
    }

    public boolean inheritsPermissions()
    {
        return inheritPermissions;
    }

    public NodeType getNodeType()
    {
        return nodeType;
    }

    public AttachedBinaries getAttachedBinaries()
    {
        return attachedBinaries;
    }

    public NodeState getNodeState()
    {
        return nodeState;
    }

    public void validateForIndexing()
    {
        Preconditions.checkNotNull( this.id, "Id must be set" );
        Preconditions.checkNotNull( this.indexConfigDocument, "EntityIndexConfig must be set" );
    }

    public String toString()
    {
        return this.path.toString();
    }

    public static Builder newNode()
    {
        return new Builder();
    }

    public static Builder newNode( final NodeId id )
    {
        return new Builder( id );
    }

    public static Builder newNode( final Node node )
    {
        return new Builder( node );
    }

    public static class Builder
    {
        private NodeId id;

        private PropertyTree data = new PropertyTree();

        private IndexConfigDocument indexConfigDocument;

        private NodeName name;

        private NodeId parentId;

        private NodePath parentPath;

        private Instant timestamp;

        boolean hasChildren = false;

        private ChildOrder childOrder;

        private Long manualOrderValue;

        private AccessControlList permissions = AccessControlList.empty();

        private boolean inheritPermissions;

        private NodeType nodeType = NodeType.DEFAULT_NODE_COLLECTION;

        private AttachedBinaries attachedBinaries = AttachedBinaries.empty();

        private NodeState nodeState = NodeState.DEFAULT;

        public Builder()
        {
            super();
        }

        public Builder( final NodeId id )
        {
            this.id = id;
        }

        public Builder( final Node node )
        {
            this.id = node.id;
            this.name = node.name;
            this.parentPath = node.parentPath;
            this.parentId = node.parentId;
            this.nodeType = node.nodeType;
            this.hasChildren = node.hasChildren;
            this.data = node.data;
            this.indexConfigDocument = node.indexConfigDocument;
            this.childOrder = node.childOrder;
            this.manualOrderValue = node.manualOrderValue;
            this.permissions = node.permissions;
            this.inheritPermissions = node.inheritPermissions;
            this.attachedBinaries = node.attachedBinaries;
            this.nodeState = node.nodeState;
            this.timestamp = node.timestamp;
        }

        public Builder( final NodeId id, final NodeName name )
        {
            this.id = id;
            this.name = name;
        }

        public Builder name( final String value )
        {
            this.name( NodeName.from( value ) );
            return this;
        }

        public Builder name( final NodeName value )
        {
            this.name = value;
            return this;
        }

        public Builder path( final String value )
        {
            this.parentPath = new NodePath( value );
            return this;
        }

        public Builder parentId( final NodeId value )
        {
            this.parentId = value;
            return this;
        }

        public Builder parentPath( final NodePath value )
        {
            this.parentPath = value;
            return this;
        }

        public Builder hasChildren( final boolean hasChildren )
        {
            this.hasChildren = hasChildren;
            return this;
        }

        public Builder id( final NodeId id )
        {
            this.id = id;
            return this;
        }

        public Builder data( final PropertyTree value )
        {
            this.data = value;
            return this;
        }

        public Builder indexConfigDocument( final IndexConfigDocument indexConfigDocument )
        {
            this.indexConfigDocument = indexConfigDocument;
            return this;
        }

        public Builder childOrder( final ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return this;
        }

        public Builder manualOrderValue( final Long manualOrderValue )
        {
            this.manualOrderValue = manualOrderValue;
            return this;
        }

        public Builder permissions( final AccessControlList permissions )
        {
            this.permissions = permissions;
            return this;
        }

        public Builder inheritPermissions( final boolean inheritPermissions )
        {
            this.inheritPermissions = inheritPermissions;
            return this;
        }

        public Builder nodeType( final NodeType nodeType )
        {
            this.nodeType = nodeType;
            return this;
        }

        public Builder attachedBinaries( final AttachedBinaries attachedBinaries )
        {
            this.attachedBinaries = attachedBinaries;
            return this;
        }

        public Builder nodeState( final NodeState nodeState )
        {
            this.nodeState = nodeState;
            return this;
        }

        public Builder timestamp( final Instant timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public Node build()
        {
            return new Node( this );
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

        return Objects.equals( id, node.id ) &&
            Objects.equals( name, node.name ) &&
            Objects.equals( nodeType, node.nodeType ) &&
            Objects.equals( parentPath, node.parentPath ) &&
            Objects.equals( hasChildren, node.hasChildren ) &&
            Objects.equals( inheritPermissions, node.inheritPermissions ) &&
            Objects.equals( manualOrderValue, node.manualOrderValue ) &&
            Objects.equals( childOrder, node.childOrder ) &&
            Objects.equals( permissions, node.permissions ) &&
            Objects.equals( data, node.data ) &&
            Objects.equals( attachedBinaries, node.attachedBinaries ) &&
            Objects.equals( indexConfigDocument, node.indexConfigDocument );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, name, parentPath, nodeType, hasChildren, inheritPermissions, manualOrderValue, childOrder, permissions,
                             data, indexConfigDocument, attachedBinaries );
    }
}
