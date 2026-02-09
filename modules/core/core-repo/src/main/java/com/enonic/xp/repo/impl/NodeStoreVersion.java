package com.enonic.xp.repo.impl;

import java.util.Objects;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.security.acl.AccessControlList;

public record NodeStoreVersion(NodeId id, NodeType nodeType, PropertyTree data, IndexConfigDocument indexConfigDocument,
                                ChildOrder childOrder, Long manualOrderValue, AccessControlList permissions,
                                AttachedBinaries attachedBinaries)
{
    public NodeStoreVersion
    {
        data = Objects.requireNonNullElseGet( data, PropertyTree::new );
        nodeType = Objects.requireNonNullElse( nodeType, NodeType.DEFAULT_NODE_COLLECTION );
        permissions = Objects.requireNonNullElse( permissions, AccessControlList.empty() );
        attachedBinaries = Objects.requireNonNullElse( attachedBinaries, AttachedBinaries.empty() );
    }
    public static NodeStoreVersion from( final Node node )
    {
        return NodeStoreVersion.create()
            .id( node.id() )
            .nodeType( node.getNodeType() )
            .data( node.data() )
            .indexConfigDocument( node.getIndexConfigDocument() )
            .childOrder( node.getChildOrder() )
            .manualOrderValue( node.getManualOrderValue() )
            .permissions( node.getPermissions() )
            .attachedBinaries( node.getAttachedBinaries() )
            .build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( NodeStoreVersion source )
    {
        return new Builder( source );
    }

    public static final class Builder
    {
        private NodeId id;

        private NodeType nodeType;

        private PropertyTree data;

        private IndexConfigDocument indexConfigDocument;

        private ChildOrder childOrder;

        private Long manualOrderValue;

        private AccessControlList permissions;

        private AttachedBinaries attachedBinaries;

        private Builder()
        {
        }

        private Builder( NodeStoreVersion nodeVersion )
        {
            this.id = nodeVersion.id;
            this.nodeType = nodeVersion.nodeType;
            this.data = nodeVersion.data.copy();
            this.indexConfigDocument = nodeVersion.indexConfigDocument;
            this.childOrder = nodeVersion.childOrder;
            this.manualOrderValue = nodeVersion.manualOrderValue;
            this.permissions = nodeVersion.permissions;
            this.attachedBinaries = nodeVersion.attachedBinaries;
        }

        public Builder id( NodeId id )
        {
            this.id = id;
            return this;
        }

        public Builder nodeType( NodeType nodeType )
        {
            this.nodeType = nodeType;
            return this;
        }

        public Builder data( PropertyTree data )
        {
            this.data = data;
            return this;
        }

        public Builder indexConfigDocument( IndexConfigDocument indexConfigDocument )
        {
            this.indexConfigDocument = indexConfigDocument;
            return this;
        }

        public Builder childOrder( ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return this;
        }

        public Builder manualOrderValue( Long manualOrderValue )
        {
            this.manualOrderValue = manualOrderValue;
            return this;
        }

        public Builder permissions( AccessControlList permissions )
        {
            this.permissions = permissions;
            return this;
        }

        public Builder attachedBinaries( AttachedBinaries attachedBinaries )
        {
            this.attachedBinaries = attachedBinaries;
            return this;
        }

        public NodeStoreVersion build()
        {
            return new NodeStoreVersion( this.id, this.nodeType, this.data, this.indexConfigDocument, this.childOrder,
                                         this.manualOrderValue, this.permissions, this.attachedBinaries );
        }
    }
}