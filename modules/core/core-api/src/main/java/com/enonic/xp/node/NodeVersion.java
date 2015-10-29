package com.enonic.xp.node;

import java.time.Instant;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.security.acl.AccessControlList;

@Beta
public class NodeVersion
{
    private final NodeVersionId id;

    private final NodeId nodeId;

    private final NodeType nodeType;

    private final Instant timestamp;

    private final PropertyTree data;

    private final IndexConfigDocument indexConfigDocument;

    private final ChildOrder childOrder;

    private final Long manualOrderValue;

    private final AccessControlList permissions;

    private final boolean inheritPermissions;

    private final AttachedBinaries attachedBinaries;

    private NodeVersion( Builder builder )
    {
        id = builder.id;
        nodeId = builder.nodeId;
        nodeType = builder.nodeType;
        timestamp = builder.timestamp;
        data = builder.data;
        indexConfigDocument = builder.indexConfigDocument;
        childOrder = builder.childOrder;
        manualOrderValue = builder.manualOrderValue;
        permissions = builder.permissions;
        inheritPermissions = builder.inheritPermissions;
        attachedBinaries = builder.attachedBinaries;
    }

    public static NodeVersion from( final Node node )
    {
        return NodeVersion.create().
            id( node.getNodeVersionId() ).
            nodeId( node.id() ).
            nodeType( node.getNodeType() ).
            data( node.data() ).
            indexConfigDocument( node.getIndexConfigDocument() ).
            childOrder( node.getChildOrder() ).
            manualOrderValue( node.getManualOrderValue() ).
            permissions( node.getPermissions() ).
            inheritPermissions( node.inheritsPermissions() ).
            attachedBinaries( node.getAttachedBinaries() ).
            timestamp( Instant.now() ).
            build();
    }

    public NodeVersionId getId()
    {
        return id;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodeType getNodeType()
    {
        return nodeType;
    }

    public Instant getTimestamp()
    {
        return timestamp;
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

    public Long getManualOrderValue()
    {
        return manualOrderValue;
    }

    public AccessControlList getPermissions()
    {
        return permissions;
    }

    public boolean isInheritPermissions()
    {
        return inheritPermissions;
    }

    public AttachedBinaries getAttachedBinaries()
    {
        return attachedBinaries;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private NodeVersionId id;

        private NodeId nodeId;

        private NodeType nodeType = NodeType.DEFAULT_NODE_COLLECTION;

        private Instant timestamp = Instant.now();

        private PropertyTree data = new PropertyTree();

        private IndexConfigDocument indexConfigDocument;

        private ChildOrder childOrder;

        private Long manualOrderValue;

        private AccessControlList permissions = AccessControlList.empty();

        private boolean inheritPermissions;

        private AttachedBinaries attachedBinaries = AttachedBinaries.empty();

        private Builder()
        {
        }

        public Builder id( NodeVersionId id )
        {
            this.id = id;
            return this;
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder nodeType( NodeType nodeType )
        {
            this.nodeType = nodeType;
            return this;
        }

        public Builder timestamp( Instant timestamp )
        {
            this.timestamp = timestamp;
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

        public Builder inheritPermissions( boolean inheritPermissions )
        {
            this.inheritPermissions = inheritPermissions;
            return this;
        }

        public Builder attachedBinaries( AttachedBinaries attachedBinaries )
        {
            this.attachedBinaries = attachedBinaries;
            return this;
        }

        public NodeVersion build()
        {
            return new NodeVersion( this );
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

        final NodeVersion that = (NodeVersion) o;

        if ( inheritPermissions != that.inheritPermissions )
        {
            return false;
        }
        if ( id != null ? !id.equals( that.id ) : that.id != null )
        {
            return false;
        }
        if ( nodeId != null ? !nodeId.equals( that.nodeId ) : that.nodeId != null )
        {
            return false;
        }
        if ( nodeType != null ? !nodeType.equals( that.nodeType ) : that.nodeType != null )
        {
            return false;
        }
        if ( timestamp != null ? !timestamp.equals( that.timestamp ) : that.timestamp != null )
        {
            return false;
        }
        if ( data != null ? !data.equals( that.data ) : that.data != null )
        {
            return false;
        }
        if ( indexConfigDocument != null ? !indexConfigDocument.equals( that.indexConfigDocument ) : that.indexConfigDocument != null )
        {
            return false;
        }
        if ( childOrder != null ? !childOrder.equals( that.childOrder ) : that.childOrder != null )
        {
            return false;
        }
        if ( manualOrderValue != null ? !manualOrderValue.equals( that.manualOrderValue ) : that.manualOrderValue != null )
        {
            return false;
        }
        if ( permissions != null ? !permissions.equals( that.permissions ) : that.permissions != null )
        {
            return false;
        }
        return !( attachedBinaries != null ? !attachedBinaries.equals( that.attachedBinaries ) : that.attachedBinaries != null );

    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + ( nodeId != null ? nodeId.hashCode() : 0 );
        result = 31 * result + ( nodeType != null ? nodeType.hashCode() : 0 );
        result = 31 * result + ( timestamp != null ? timestamp.hashCode() : 0 );
        result = 31 * result + ( data != null ? data.hashCode() : 0 );
        result = 31 * result + ( indexConfigDocument != null ? indexConfigDocument.hashCode() : 0 );
        result = 31 * result + ( childOrder != null ? childOrder.hashCode() : 0 );
        result = 31 * result + ( manualOrderValue != null ? manualOrderValue.hashCode() : 0 );
        result = 31 * result + ( permissions != null ? permissions.hashCode() : 0 );
        result = 31 * result + ( inheritPermissions ? 1 : 0 );
        result = 31 * result + ( attachedBinaries != null ? attachedBinaries.hashCode() : 0 );
        return result;
    }
}
