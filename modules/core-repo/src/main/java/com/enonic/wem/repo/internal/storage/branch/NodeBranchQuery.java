package com.enonic.wem.repo.internal.storage.branch;

public class NodeBranchQuery
{
    private final int size;

    private final int from;

    private NodeBranchQuery( Builder builder )
    {
        this.size = builder.size;
        this.from = builder.from;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public int getSize()
    {
        return size;
    }

    public int getFrom()
    {
        return from;
    }

    public static final class Builder
    {
        private int size;

        private int from;

        private Builder()
        {
        }

        public Builder size( int size )
        {
            this.size = size;
            return this;
        }

        public Builder from( int from )
        {
            this.from = from;
            return this;
        }

        public NodeBranchQuery build()
        {
            return new NodeBranchQuery( this );
        }
    }
}
