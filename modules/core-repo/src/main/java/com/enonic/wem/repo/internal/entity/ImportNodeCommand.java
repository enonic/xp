package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.repo.internal.blob.BlobStore;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.InsertManualStrategy;
import com.enonic.xp.node.Node;

public class ImportNodeCommand
    extends AbstractNodeCommand
{

    private final InsertManualStrategy insertManualStrategy;

    private final BinaryAttachments binaryAttachments;

    private final Node importNode;

    private final BlobStore binaryBlobStore;

    private ImportNodeCommand( Builder builder )
    {
        super( builder );
        insertManualStrategy = builder.insertManualStrategy;
        binaryAttachments = builder.binaryAttachments;
        importNode = builder.importNode;
        binaryBlobStore = builder.binaryBlobStore;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Node execute()
    {
        final Node existingNode = doGetByPath( this.importNode.path(), false );

        if ( existingNode == null )
        {
            return createNode();
        }

        return null;
    }

    private Node createNode()
    {
        final CreateNodeParams createNodeParams = CreateNodeParams.create().
            setNodeId( this.importNode.id() ).
            nodeType( this.importNode.getNodeType() ).
            childOrder( this.importNode.getChildOrder() ).
            setBinaryAttachments( this.binaryAttachments ).
            data( this.importNode.data() ).
            indexConfigDocument( this.importNode.getIndexConfigDocument() ).
            insertManualStrategy( this.insertManualStrategy ).
            name( this.importNode.name().toString() ).
            parent( this.importNode.parentPath() ).
            inheritPermissions( this.importNode.inheritsPermissions() ).
            permissions( this.importNode.getPermissions() ).
            setNodeId( this.importNode.id() ).
            build();

        return doCreateNode( createNodeParams, this.binaryBlobStore, this.importNode.getTimestamp() );
    }


    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private InsertManualStrategy insertManualStrategy;

        private BinaryAttachments binaryAttachments;

        private Node importNode;

        private BlobStore binaryBlobStore;

        private Builder()
        {
        }

        public Builder insertManualStrategy( InsertManualStrategy insertManualStrategy )
        {
            this.insertManualStrategy = insertManualStrategy;
            return this;
        }

        public Builder binaryAttachments( BinaryAttachments binaryAttachments )
        {
            this.binaryAttachments = binaryAttachments;
            return this;
        }

        public Builder importNode( Node importNode )
        {
            this.importNode = importNode;
            return this;
        }

        public Builder binaryBlobStore( BlobStore binaryBlobStore )
        {
            this.binaryBlobStore = binaryBlobStore;
            return this;
        }

        public ImportNodeCommand build()
        {
            return new ImportNodeCommand( this );
        }
    }
}
