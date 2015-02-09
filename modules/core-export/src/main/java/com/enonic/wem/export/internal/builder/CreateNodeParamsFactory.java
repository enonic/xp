package com.enonic.wem.export.internal.builder;

import com.google.common.base.Strings;

import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.BinaryAttachments;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.InsertManualStrategy;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeType;
import com.enonic.wem.export.internal.ProcessNodeSettings;
import com.enonic.wem.export.internal.xml.XmlNode;

public class CreateNodeParamsFactory
{
    private final XmlNode xmlNode;

    private final NodePath nodeImportPath;

    private final ProcessNodeSettings processNodeSettings;

    private final BinaryAttachments binaryAttachments;

    private final boolean importNodeIds;

    private final boolean dryRun;

    private CreateNodeParamsFactory( Builder builder )
    {
        this.xmlNode = builder.xmlNode;
        this.nodeImportPath = builder.importPath;
        this.processNodeSettings = builder.processNodeSettings;
        this.binaryAttachments = builder.binaryAttachments;
        this.importNodeIds = builder.importNodeIds;
        this.dryRun = builder.dryRun;
    }

    public CreateNodeParams execute()
    {
        final String nodeName = this.nodeImportPath.getLastElement().toString();
        final NodePath parentPath = this.nodeImportPath.getParentPath();

        final ChildOrder childOrder = getChildOrder( xmlNode );

        final CreateNodeParams.Builder builder = CreateNodeParams.create().
            name( nodeName ).
            parent( parentPath ).
            childOrder( childOrder ).
            nodeType( NodeType.from( xmlNode.getNodeType() ) ).
            data( PropertyTreeXmlBuilder.build( xmlNode.getData() ) ).
            indexConfigDocument( IndexConfigDocumentXmlBuilder.build( xmlNode.getIndexConfigs() ) ).
            dryRun( this.dryRun ).
            setBinaryAttachments( binaryAttachments );

        if ( importNodeIds && !Strings.isNullOrEmpty( xmlNode.getId() ) )
        {
            builder.setNodeId( NodeId.from( xmlNode.getId() ) );
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

    private ChildOrder getChildOrder( final XmlNode xmlNode )
    {
        if ( Strings.isNullOrEmpty( xmlNode.getChildOrder() ) )
        {
            return null;
        }

        return ChildOrder.from( xmlNode.getChildOrder() );
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private XmlNode xmlNode;

        private NodePath importPath;

        private ProcessNodeSettings processNodeSettings;

        private BinaryAttachments binaryAttachments;

        private boolean importNodeIds = true;

        private boolean dryRun = false;

        private Builder()
        {
        }

        public Builder xmlNode( XmlNode xmlNode )
        {
            this.xmlNode = xmlNode;
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
