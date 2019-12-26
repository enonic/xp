package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.security.acl.AccessControlList;

@PublicApi
public class NodeVersion
{
    private final NodeId id;

    private final NodeType nodeType;

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
        nodeType = builder.nodeType;
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
            id( node.id() ).
            nodeType( node.getNodeType() ).
            data( node.data() ).
            indexConfigDocument( node.getIndexConfigDocument() ).
            childOrder( node.getChildOrder() ).
            manualOrderValue( node.getManualOrderValue() ).
            permissions( node.getPermissions() ).
            inheritPermissions( node.inheritsPermissions() ).
            attachedBinaries( node.getAttachedBinaries() ).
            build();
    }

    public NodeId getId()
    {
        return id;
    }

    public NodeType getNodeType()
    {
        return nodeType;
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

    public static Builder create( NodeVersion source )
    {
        return new Builder( source );
    }


    public static final class Builder
    {
        private NodeId id;

        private NodeType nodeType = NodeType.DEFAULT_NODE_COLLECTION;

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

        private Builder( NodeVersion nodeVersion )
        {
            this.id = nodeVersion.id;
            this.nodeType = nodeVersion.nodeType;
            this.data = nodeVersion.data;
            this.indexConfigDocument = nodeVersion.indexConfigDocument;
            this.childOrder = nodeVersion.childOrder;
            this.manualOrderValue = nodeVersion.manualOrderValue;
            this.permissions = nodeVersion.permissions;
            this.inheritPermissions = nodeVersion.inheritPermissions;
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
        if ( nodeType != null ? !nodeType.equals( that.nodeType ) : that.nodeType != null )
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
        result = 31 * result + ( nodeType != null ? nodeType.hashCode() : 0 );
        result = 31 * result + ( data != null ? data.hashCode() : 0 );
        result = 31 * result + ( indexConfigDocument != null ? indexConfigDocument.hashCode() : 0 );
        result = 31 * result + ( childOrder != null ? childOrder.hashCode() : 0 );
        result = 31 * result + ( manualOrderValue != null ? manualOrderValue.hashCode() : 0 );
        result = 31 * result + ( permissions != null ? permissions.hashCode() : 0 );
        result = 31 * result + ( inheritPermissions ? 1 : 0 );
        result = 31 * result + ( attachedBinaries != null ? attachedBinaries.hashCode() : 0 );
        return result;
    }

    @Override
    public String toString()
    {
        return "NodeVersion{" +
            "id=" + id +
            ", nodeType=" + nodeType +
            ", data=" + data +
            ", indexConfigDocument=" + indexConfigDocument +
            ", childOrder=" + childOrder +
            ", manualOrderValue=" + manualOrderValue +
            ", permissions=" + permissions +
            ", inheritPermissions=" + inheritPermissions +
            ", attachedBinaries=" + attachedBinaries +
            '}';
    }
}
