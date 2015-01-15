package com.enonic.wem.api.node;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.index.IndexConfig;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.index.PatternIndexConfigDocument;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlList;

public final class Node
{
    private final NodeId id;

    private final NodeName name;

    private final NodePath parent;

    private final NodeType nodeType;

    private final NodePath path;

    private final PrincipalKey creator;

    private final Instant createdTime;

    private final PrincipalKey modifier;

    private final Instant modifiedTime;

    private final boolean hasChildren;

    private final PropertyTree data;

    private final IndexConfigDocument indexConfigDocument;

    private final ChildOrder childOrder;

    private final Long manualOrderValue;

    private final AccessControlList permissions;

    private final boolean inheritPermissions;

    private final AttachedBinaries attachedBinaries;

    private final PrincipalKey owner;

    private final Locale language;

    private Node( final Builder builder )
    {
        Preconditions.checkNotNull( builder.permissions, "permissions are required" );
        Preconditions.checkNotNull( builder.data, "data are required" );

        this.id = builder.id;
        this.name = builder.name;
        this.parent = builder.parent;
        this.nodeType = builder.nodeType;
        this.creator = builder.creator;
        this.createdTime = builder.createdTime;
        this.modifier = builder.modifier;
        this.modifiedTime = builder.modifiedTime;
        this.hasChildren = builder.hasChildren;
        this.data = builder.data != null ? builder.data : new PropertyTree();
        this.childOrder = builder.childOrder;
        this.manualOrderValue = builder.manualOrderValue;
        this.permissions = builder.permissions == null ? AccessControlList.empty() : builder.permissions;
        this.inheritPermissions = builder.inheritPermissions;
        this.attachedBinaries = builder.attachedBinaries;
        this.owner = builder.owner;
        this.language = builder.language;

        this.path = this.parent != null && this.name != null ? new NodePath( this.parent, this.name ) : null;

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

    public PrincipalKey creator()
    {
        return creator;
    }

    public PrincipalKey getCreator()
    {
        return creator;
    }

    public PrincipalKey modifier()
    {
        return modifier;
    }

    public PrincipalKey getModifier()
    {
        return modifier;
    }

    public boolean getHasChildren()
    {
        return hasChildren;
    }

    public NodeId id()
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

    public PrincipalKey getOwner()
    {
        return owner;
    }

    public Locale getLanguage()
    {
        return language;
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

        private Instant createdTime;

        private Instant modifiedTime;

        private PropertyTree data = new PropertyTree();

        private IndexConfigDocument indexConfigDocument;

        private NodeName name;

        private NodePath parent;

        private PrincipalKey modifier;

        private PrincipalKey creator;

        boolean hasChildren = false;

        private ChildOrder childOrder;

        private Long manualOrderValue;

        private AccessControlList permissions = AccessControlList.empty();

        private boolean inheritPermissions;

        private NodeType nodeType = NodeType.DEFAULT_NODE_COLLECTION;

        private AttachedBinaries attachedBinaries = AttachedBinaries.empty();

        private PrincipalKey owner;

        private Locale language;

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
            this.parent = node.parent;
            this.nodeType = node.nodeType;
            this.creator = node.creator;
            this.createdTime = node.createdTime;
            this.modifier = node.modifier;
            this.modifiedTime = node.modifiedTime;
            this.hasChildren = node.hasChildren;
            this.data = node.data;
            this.indexConfigDocument = node.indexConfigDocument;
            this.childOrder = node.childOrder;
            this.manualOrderValue = node.manualOrderValue;
            this.permissions = node.permissions;
            this.inheritPermissions = node.inheritPermissions;
            this.attachedBinaries = node.attachedBinaries;
            this.owner = node.owner;
            this.language = node.language;
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
            this.parent = new NodePath( value );
            return this;
        }

        public Builder parent( final NodePath value )
        {
            this.parent = value;
            return this;
        }

        public Builder creator( final PrincipalKey value )
        {
            this.creator = value;
            return this;
        }

        public Builder modifier( final PrincipalKey value )
        {
            this.modifier = value;
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

        public Builder owner( final PrincipalKey owner )
        {
            this.owner = owner;
            return this;
        }

        public Builder language( final Locale language )
        {
            this.language = language;
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
            Objects.equals( parent, node.parent ) &&
            Objects.equals( hasChildren, node.hasChildren ) &&
            Objects.equals( inheritPermissions, node.inheritPermissions ) &&
            Objects.equals( creator, node.creator ) &&
            Objects.equals( modifier, node.modifier ) &&
            Objects.equals( createdTime, node.createdTime ) &&
            Objects.equals( modifiedTime, node.modifiedTime ) &&
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
        return Objects.hash( id, name, parent, nodeType, hasChildren, inheritPermissions, creator, modifier, createdTime, modifiedTime,
                             manualOrderValue, childOrder, permissions, data, indexConfigDocument, attachedBinaries );
    }
}
