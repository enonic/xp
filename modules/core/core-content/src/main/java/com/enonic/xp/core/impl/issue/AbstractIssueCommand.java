package com.enonic.xp.core.impl.issue;

import com.enonic.xp.node.NodeService;

abstract class AbstractIssueCommand
{
    final NodeService nodeService;

    AbstractIssueCommand( final Builder builder )
    {
        this.nodeService = builder.nodeService;
    }

    public static class Builder<B extends Builder>
    {
        private NodeService nodeService;

        Builder()
        {

        }

        public Builder( final AbstractIssueCommand source )
        {
            this.nodeService = source.nodeService;
        }

        public B nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return (B) this;
        }
    }
}
