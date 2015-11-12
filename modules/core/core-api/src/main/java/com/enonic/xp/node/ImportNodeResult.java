package com.enonic.xp.node;

import com.google.common.base.Preconditions;

public class ImportNodeResult
{
    private final Node node;

    private final boolean preExisting;


    private ImportNodeResult( Builder builder )
    {
        this.node = builder.node;
        this.preExisting = builder.preExisting;
    }

    public Node getNode()
    {
        return node;
    }

    public boolean isPreExisting()
    {
        return preExisting;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Node node;

        private boolean preExisting;

        private Builder()
        {
        }

        public Builder node( final Node node )
        {
            this.node = node;
            return this;
        }

        public Builder preExisting( final boolean preExisting )
        {
            this.preExisting = preExisting;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( node, "node cannot be null" );
        }

        public ImportNodeResult build()
        {
            this.validate();
            return new ImportNodeResult( this );
        }
    }
}
