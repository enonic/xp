package com.enonic.wem.export.internal.builder;

import com.enonic.wem.api.node.BinaryAttachments;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.UpdateNodeParams;
import com.enonic.wem.export.internal.xml.XmlNode;

public class UpdateNodeParamsFactory
{
    private final XmlNode xmlNode;

    private final BinaryAttachments binaryAttachments;

    private final Node existingNode;

    private final boolean dryRun;

    private UpdateNodeParamsFactory( final Builder builder )
    {
        this.xmlNode = builder.xmlNode;
        this.binaryAttachments = builder.binaryAttachments;
        this.existingNode = builder.existingNode;
        this.dryRun = builder.dryRun;
    }

    public UpdateNodeParams execute()
    {
        final UpdateNodeParams.Builder builder = UpdateNodeParams.create().
            dryRun( this.dryRun ).
            id( this.existingNode.id() ).
            setBinaryAttachments( binaryAttachments ).
            editor( editableNode -> {
                editableNode.data = PropertyTreeXmlBuilder.build( xmlNode.getData() );
            } );

        return builder.build();
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private XmlNode xmlNode;

        private BinaryAttachments binaryAttachments;

        private Node existingNode;

        private boolean dryRun = false;

        private Builder()
        {
        }

        public Builder xmlNode( XmlNode xmlNode )
        {
            this.xmlNode = xmlNode;
            return this;
        }

        public Builder existingNode( final Node node )
        {
            this.existingNode = node;
            return this;
        }

        public Builder binaryAttachments( BinaryAttachments binaryAttachments )
        {
            this.binaryAttachments = binaryAttachments;
            return this;
        }

        public Builder dryRun( final boolean dryRun )
        {
            this.dryRun = dryRun;
            return this;
        }

        public UpdateNodeParamsFactory build()
        {
            return new UpdateNodeParamsFactory( this );
        }
    }
}
