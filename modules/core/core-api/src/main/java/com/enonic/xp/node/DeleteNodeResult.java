package com.enonic.xp.node;

public final class DeleteNodeResult
{
    private final NodeBranchEntries nodeBranchEntries;

    private DeleteNodeResult( final Builder builder )
    {
        this.nodeBranchEntries = builder.nodeBranchEntries;
    }

    public static Builder create()
    {
        return new DeleteNodeResult.Builder();
    }

    public NodeBranchEntries getNodeBranchEntries()
    {
        return nodeBranchEntries;
    }

    public static class Builder
    {

        private NodeBranchEntries nodeBranchEntries;

        private Builder()
        {
        }

        public Builder nodeBranchEntries( final NodeBranchEntries nodeBranchEntries )
        {
            this.nodeBranchEntries = nodeBranchEntries;
            return this;
        }

        public DeleteNodeResult build()
        {
            return new DeleteNodeResult( this );
        }
    }
}
