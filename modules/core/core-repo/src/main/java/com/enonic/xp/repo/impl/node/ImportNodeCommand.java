package com.enonic.xp.repo.impl.node;

import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.InsertManualStrategy;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.blob.BlobStore;

public class ImportNodeCommand
    extends AbstractNodeCommand
{
    private final InsertManualStrategy insertManualStrategy;

    private final BinaryAttachments binaryAttachments;

    private final Node importNode;

    private final BlobStore binaryBlobStore;

    private final boolean dryRun;

    private final boolean importPermissions;

    private ImportNodeCommand( Builder builder )
    {
        super( builder );
        this.insertManualStrategy = builder.insertManualStrategy;
        this.binaryAttachments = builder.binaryAttachments;
        this.importNode = builder.importNode;
        this.binaryBlobStore = builder.binaryBlobStore;
        this.dryRun = builder.dryRun;
        this.importPermissions = builder.importPermissions;

    }

    public static Builder create()
    {
        return new Builder();
    }

    public Node execute()
    {
        final boolean exists = CheckNodeExistsCommand.create( this ).
            nodePath( this.importNode.path() ).
            build().
            execute();

        if ( !exists )
        {
            return createNode();
        }
        else
        {
            return updateNode( GetNodeByPathCommand.create( this ).
                nodePath( this.importNode.path() ).
                build().
                execute() );
        }
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
            manualOrderValue( this.importNode.getManualOrderValue() ).
            name( this.importNode.name().toString() ).
            parent( this.importNode.parentPath() ).
            inheritPermissions( this.importNode.inheritsPermissions() ).
            permissions( this.importNode.getPermissions() ).
            setNodeId( this.importNode.id() ).
            build();

        return CreateNodeCommand.create( this ).
            params( createNodeParams ).
            timestamp( this.importNode.getTimestamp() ).
            binaryBlobStore( binaryBlobStore ).
            build().
            execute();
    }

    private Node updateNode( final Node existingNode )
    {
        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            dryRun( this.dryRun ).
            id( existingNode.id() ).
            setBinaryAttachments( this.binaryAttachments ).
            editor( editableNode -> {
                editableNode.data = this.importNode.data();
                if ( this.importPermissions )
                {
                    editableNode.inheritPermissions = this.importNode.inheritsPermissions();
                    editableNode.permissions = this.importNode.getPermissions();
                }
            } ).build();

        return UpdateNodeCommand.create( this ).
            params( updateNodeParams ).
            binaryBlobStore( binaryBlobStore ).
            build().
            execute();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private InsertManualStrategy insertManualStrategy;

        private BinaryAttachments binaryAttachments;

        private Node importNode;

        private BlobStore binaryBlobStore;

        private boolean dryRun;

        private boolean importPermissions;


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

        public Builder dryRun( boolean dryRun )
        {
            this.dryRun = dryRun;
            return this;
        }

        public Builder importPermissions( boolean importPermissions )
        {
            this.importPermissions = importPermissions;
            return this;
        }

        public ImportNodeCommand build()
        {
            return new ImportNodeCommand( this );
        }
    }
}
