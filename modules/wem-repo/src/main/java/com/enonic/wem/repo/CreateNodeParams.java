package com.enonic.wem.repo;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.security.acl.AccessControlList;

public class CreateNodeParams
{
    private final NodePath parent;

    private final String name;

    private final RootDataSet data;

    private final Attachments attachments;

    private final IndexConfigDocument indexConfigDocument;

    private final ChildOrder childOrder;

    private final NodeId nodeId;

    private final AccessControlList accessControlList;

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

    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getName()
    {
        return name;
    }

    public NodePath getParent()
    {
        return parent;
    }

    public RootDataSet getData()
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

    public static final class Builder
    {
        private NodePath parent;

        private String name;

        private RootDataSet data;

        private Attachments attachments;

        private IndexConfigDocument indexConfigDocument;

        private ChildOrder childOrder;

        private NodeId nodeId;

        private AccessControlList accessControlList;

        private Builder()
        {
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

        public Builder data( final RootDataSet data )
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

        if ( accessControlList != null ? !accessControlList.equals( that.accessControlList ) : that.accessControlList != null )
        {
            return false;
        }
        if ( attachments != null ? !attachments.equals( that.attachments ) : that.attachments != null )
        {
            return false;
        }
        if ( childOrder != null ? !childOrder.equals( that.childOrder ) : that.childOrder != null )
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
        if ( name != null ? !name.equals( that.name ) : that.name != null )
        {
            return false;
        }
        if ( !nodeId.equals( that.nodeId ) )
        {
            return false;
        }
        if ( parent != null ? !parent.equals( that.parent ) : that.parent != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        result = 31 * result + ( data != null ? data.hashCode() : 0 );
        result = 31 * result + ( attachments != null ? attachments.hashCode() : 0 );
        result = 31 * result + ( indexConfigDocument != null ? indexConfigDocument.hashCode() : 0 );
        result = 31 * result + ( childOrder != null ? childOrder.hashCode() : 0 );
        result = 31 * result + nodeId.hashCode();
        result = 31 * result + ( accessControlList != null ? accessControlList.hashCode() : 0 );
        return result;
    }
}
