package com.enonic.wem.api.node;

import java.util.Objects;

import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.security.acl.AccessControlList;

public class CreateNodeParams
{
    private final NodePath parent;

    private final String name;

    private final PropertyTree data;

    private final Attachments attachments;

    private final IndexConfigDocument indexConfigDocument;

    private final ChildOrder childOrder;

    private final NodeId nodeId;

    private final AccessControlList accessControlList;

    private final boolean inheritPermissions;

    private CreateNodeParams( Builder builder )
    {
        this.parent = builder.parent;
        this.name = builder.name;
        this.data = builder.data;
        this.attachments = builder.attachments;
        this.indexConfigDocument = builder.indexConfigDocument;
        this.childOrder = builder.childOrder;
        this.nodeId = builder.nodeId;
        this.accessControlList = builder.accessControlList;
        this.inheritPermissions = builder.inheritPermissions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder from( final Node node )
    {
        return new Builder().
            indexConfigDocument( node.getIndexConfigDocument() ).
            accessControlList( node.getAccessControlList() ).
            data( node.data() ).
            attachments( node.attachments() ).
            childOrder( node.getChildOrder() ).
            name( node.name().toString() ).
            parent( node.parent() );
    }

    public String getName()
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

    public Attachments getAttachments()
    {
        return attachments;
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

    public AccessControlList getAccessControlList()
    {
        return accessControlList;
    }

    public boolean inheritPermissions()
    {
        return inheritPermissions;
    }

    public static final class Builder
    {
        private NodePath parent;

        private String name;

        private PropertyTree data;

        private Attachments attachments;

        private IndexConfigDocument indexConfigDocument;

        private ChildOrder childOrder;

        private NodeId nodeId;

        private AccessControlList accessControlList;

        private boolean inheritPermissions;

        private Builder()
        {
            this.inheritPermissions = true;
        }

        public Builder setNodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder parent( final NodePath parent )
        {
            this.parent = parent;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder data( final PropertyTree data )
        {
            this.data = data;
            return this;
        }

        public Builder attachments( final Attachments attachments )
        {
            this.attachments = attachments;
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

        public Builder accessControlList( final AccessControlList accessControlList )
        {
            this.accessControlList = accessControlList;
            return this;
        }

        public Builder inheritPermissions( final boolean inheritPermissions )
        {
            this.inheritPermissions = inheritPermissions;
            return this;
        }

        public CreateNodeParams build()
        {
            return new CreateNodeParams( this );
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

        final CreateNodeParams that = (CreateNodeParams) o;
        return Objects.equals( nodeId, that.nodeId ) &&
            Objects.equals( parent, that.parent ) &&
            Objects.equals( name, that.name ) &&
            Objects.equals( data, that.data ) &&
            Objects.equals( attachments, that.attachments ) &&
            Objects.equals( indexConfigDocument, that.indexConfigDocument ) &&
            Objects.equals( childOrder, that.childOrder ) &&
            Objects.equals( accessControlList, that.accessControlList ) &&
            ( inheritPermissions == that.inheritPermissions );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( parent, name, data, attachments, indexConfigDocument, childOrder, nodeId, accessControlList,
                             inheritPermissions );
    }
}
