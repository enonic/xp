package com.enonic.wem.repo.internal.storage.branch;

import com.enonic.xp.node.AbstractQuery;

public class NodeBranchQuery
    extends AbstractQuery
{
    private NodeBranchQuery( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractQuery.Builder<Builder>
    {
        private Builder()
        {
            super();
        }

        public NodeBranchQuery build()
        {
            return new NodeBranchQuery( this );
        }
    }
}
