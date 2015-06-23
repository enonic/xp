package com.enonic.xp.core.impl.export.builder;

import com.enonic.xp.core.impl.export.ProcessNodeSettings;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.InsertManualStrategy;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;


public class CreateNodeParamsFactory
{
    private final Node newNode;

    private final NodePath nodeImportPath;

    private final ProcessNodeSettings processNodeSettings;

    private final BinaryAttachments binaryAttachments;

    private final boolean importNodeIds;

    private final boolean dryRun;

    private CreateNodeParamsFactory( Builder builder )
    {
        this.newNode = builder.newNode;
        this.nodeImportPath = builder.importPath;
        this.processNodeSettings = builder.processNodeSettings;
        this.binaryAttachments = builder.binaryAttachments;
        this.importNodeIds = builder.importNodeIds;
        this.dryRun = builder.dryRun;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public CreateNodeParams execute()
    {
        final String nodeName = this.nodeImportPath.getLastElement().toString();
        final NodePath parentPath = this.nodeImportPath.getParentPath();

        final ChildOrder childOrder = this.newNode.getChildOrder();

        final CreateNodeParams.Builder builder = CreateNodeParams.create().
            name( nodeName ).
            parent( parentPath ).
            childOrder( childOrder ).
            nodeType( this.newNode.getNodeType() ).
            data( this.newNode.data() ).
            indexConfigDocument( this.newNode.getIndexConfigDocument() ).
            inheritPermissions( true ).
            setBinaryAttachments( binaryAttachments );

        if ( importNodeIds && ( this.newNode.id() != null ) )
        {
            builder.setNodeId( this.newNode.id() );
        }

        setInsertManualSettings( builder );

        return builder.build();
    }

    private void setInsertManualSettings( final CreateNodeParams.Builder builder )
    {
        final InsertManualStrategy insertManualStrategy = this.processNodeSettings.getInsertManualStrategy();

        if ( insertManualStrategy != null )
        {
            builder.insertManualStrategy( insertManualStrategy );

            if ( insertManualStrategy.equals( InsertManualStrategy.MANUAL ) )
            {
                builder.manualOrderValue( this.processNodeSettings.getManualOrderValue() );
            }
        }
    }

    public static final class Builder
    {
        private Node newNode;

        private NodePath importPath;

        private ProcessNodeSettings processNodeSettings;

        private BinaryAttachments binaryAttachments;

        private boolean importNodeIds = true;

        private boolean dryRun = false;

        private Builder()
        {
        }

        public Builder newNode( Node newNode )
        {
            this.newNode = newNode;
            return this;
        }

        public Builder importPath( NodePath importPath )
        {
            this.importPath = importPath;
            return this;
        }

        public Builder processNodeSettings( ProcessNodeSettings processNodeSettings )
        {
            this.processNodeSettings = processNodeSettings;
            return this;
        }

        public Builder binaryAttachments( final BinaryAttachments binaryAttachments )
        {
            this.binaryAttachments = binaryAttachments;
            return this;
        }

        public Builder importNodeIds( final boolean importNodeIds )
        {
            this.importNodeIds = importNodeIds;
            return this;
        }

        public Builder dryRun( final boolean dryRun )
        {
            this.dryRun = dryRun;
            return this;
        }

        public CreateNodeParamsFactory build()
        {
            return new CreateNodeParamsFactory( this );
        }
    }
}
