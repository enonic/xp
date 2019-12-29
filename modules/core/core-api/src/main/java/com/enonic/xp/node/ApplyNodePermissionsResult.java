package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class ApplyNodePermissionsResult
{
    private final Nodes succeedNodes;

    private final Nodes skippedNodes;

    private ApplyNodePermissionsResult( Builder builder )
    {
        this.succeedNodes = builder.succeedNodes.build();
        this.skippedNodes = builder.skippedNodes.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Nodes getSucceedNodes()
    {
        return succeedNodes;
    }

    public Nodes getSkippedNodes()
    {
        return skippedNodes;
    }

    public static final class Builder
    {
        private Nodes.Builder succeedNodes = Nodes.create();

        private Nodes.Builder skippedNodes = Nodes.create();

        private Builder()
        {
        }

        public Builder succeedNode( final Node succeedNode )
        {
            this.succeedNodes.add( succeedNode );
            return this;
        }

        public Builder skippedNode( final Node skippedNode )
        {
            this.skippedNodes.add( skippedNode );
            return this;
        }

        public ApplyNodePermissionsResult build()
        {
            return new ApplyNodePermissionsResult( this );
        }
    }
}
