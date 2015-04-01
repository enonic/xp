package com.enonic.xp.core.impl.export.builder;

import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.UpdateNodeParams;

public class UpdateNodeParamsFactory
{
    private final Node newNode;

    private final BinaryAttachments binaryAttachments;

    private final Node existingNode;

    private final boolean dryRun;

    private UpdateNodeParamsFactory( final Builder builder )
    {
        this.newNode = builder.newNode;
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
            editor( editableNode -> editableNode.data = newNode.data() );

        return builder.build();
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private Node newNode;

        private BinaryAttachments binaryAttachments;

        private Node existingNode;

        private boolean dryRun = false;

        private Builder()
        {
        }

        public Builder newNode( Node newNode )
        {
            this.newNode = newNode;
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
