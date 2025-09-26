package com.enonic.xp.node;

import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.util.BinaryReference;

@PublicApi
public final class CreateNodeParams
{
    private final NodePath parent;

    private final NodeName name;

    private final PropertyTree data;

    private final IndexConfigDocument indexConfigDocument;

    private final ChildOrder childOrder;

    private final NodeId nodeId;

    private final AccessControlList permissions;

    private final boolean inheritPermissions;

    private final Long manualOrderValue;

    private final InsertManualStrategy insertManualStrategy;

    private final NodeType nodeType;

    private final BinaryAttachments binaryAttachments;

    private final RefreshMode refresh;

    private CreateNodeParams( Builder builder )
    {
        this.parent = builder.parent;
        this.name = builder.name;
        this.data = builder.data;
        this.indexConfigDocument = builder.indexConfigDocument;
        this.childOrder = builder.childOrder;
        this.nodeId = builder.nodeId;
        this.permissions = Objects.requireNonNullElse( builder.permissions, AccessControlList.empty() );
        this.inheritPermissions = builder.inheritPermissions;
        this.insertManualStrategy = Objects.requireNonNullElse( builder.insertManualStrategy, InsertManualStrategy.FIRST );
        this.manualOrderValue = builder.manualOrderValue;
        this.nodeType = builder.nodeType;
        this.binaryAttachments = builder.binaryAttachments.build();
        this.refresh = builder.refresh;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final CreateNodeParams source )
    {
        return new Builder( source );
    }

    public static Builder from( final Node node )
    {
        return new Builder().
            parent( node.parentPath() ).
            name( node.name() ).
            data( node.data() ).
            indexConfigDocument( node.getIndexConfigDocument() ).
            childOrder( node.getChildOrder() ).
            permissions( node.getPermissions() ).
            nodeType( node.getNodeType() );
    }

    public NodeName getName()
    {
        return name;
    }

    public NodePath getParent()
    {
        return parent;
    }

    public PropertyTree getData()
    {
        return data;
    }

    public IndexConfigDocument getIndexConfigDocument()
    {
        return indexConfigDocument;
    }

    public ChildOrder getChildOrder()
    {
        return childOrder;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public boolean isInheritPermissions()
    {
        return inheritPermissions;
    }

    public Long getManualOrderValue()
    {
        return manualOrderValue;
    }

    public InsertManualStrategy getInsertManualStrategy()
    {
        return insertManualStrategy;
    }

    public AccessControlList getPermissions()
    {
        return permissions;
    }

    public NodeType getNodeType()
    {
        return nodeType;
    }

    public BinaryAttachments getBinaryAttachments()
    {
        return binaryAttachments;
    }

    public RefreshMode getRefresh()
    {
        return refresh;
    }

    public static final class Builder
    {
        private NodePath parent;

        private NodeName name;

        private PropertyTree data = new PropertyTree();

        private IndexConfigDocument indexConfigDocument;

        private ChildOrder childOrder;

        private NodeId nodeId;

        private AccessControlList permissions;

        private boolean inheritPermissions;

        private InsertManualStrategy insertManualStrategy;

        private Long manualOrderValue;

        private NodeType nodeType;

        private BinaryAttachments.Builder binaryAttachments = BinaryAttachments.create();

        private RefreshMode refresh;

        private Builder()
        {
        }

        private Builder( final CreateNodeParams createNodeParams )
        {
            this.parent = createNodeParams.parent;
            this.name = createNodeParams.name;
            this.data = createNodeParams.data;
            this.indexConfigDocument = createNodeParams.indexConfigDocument;
            this.childOrder = createNodeParams.childOrder;
            this.nodeId = createNodeParams.nodeId;
            this.permissions = createNodeParams.permissions;
            this.inheritPermissions = createNodeParams.inheritPermissions;
            this.insertManualStrategy = createNodeParams.insertManualStrategy;
            this.manualOrderValue = createNodeParams.manualOrderValue;
            this.nodeType = createNodeParams.nodeType;
            createNodeParams.binaryAttachments.stream().forEach( binaryAttachments::add );
            this.refresh = createNodeParams.refresh;
        }

        public Builder setNodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId != null ? NodeId.from( nodeId.toString().toLowerCase() ) : null;
            return this;
        }

        public Builder parent( final NodePath parent )
        {
            this.parent = parent;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = NodeName.from( name );
            return this;
        }

        public Builder name( final NodeName name )
        {
            this.name = name;
            return this;
        }

        public Builder data( final PropertyTree data )
        {
            this.data = data;
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

        public Builder insertManualStrategy( final InsertManualStrategy insertManualStrategy )
        {
            this.insertManualStrategy = insertManualStrategy;
            return this;
        }

        public Builder manualOrderValue( final Long manualOrderValue )
        {
            this.manualOrderValue = manualOrderValue;
            return this;
        }

        public Builder nodeType( final NodeType nodeType )
        {
            this.nodeType = nodeType;
            return this;
        }

        public Builder attachBinary( final BinaryReference binaryReference, final ByteSource byteSource )
        {
            this.binaryAttachments.add( new BinaryAttachment( binaryReference, byteSource ) );
            return this;
        }

        public Builder setBinaryAttachments( final BinaryAttachments binaryAttachments )
        {
            this.binaryAttachments = BinaryAttachments.create();
            if ( binaryAttachments != null )
            {
                binaryAttachments.stream().forEach( this.binaryAttachments::add );
            }
            return this;
        }

        public Builder refresh( final RefreshMode refresh )
        {
            this.refresh = refresh;
            return this;
        }

        public CreateNodeParams build()
        {
            Objects.requireNonNull( parent, "parent is required" );
            Preconditions.checkArgument( parent.isAbsolute(), "Path to parent Node must be absolute: %s", parent );

            return new CreateNodeParams( this );
        }
    }
}
