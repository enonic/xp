package com.enonic.xp.core.impl.content;

import com.enonic.xp.branch.BranchId;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;

public class CompareContentCommand
{
    private final ContentId contentId;

    private final BranchId target;

    private final NodeService nodeService;

    private CompareContentCommand( final Builder builder )
    {
        this.contentId = builder.contentId;
        this.target = builder.target;
        this.nodeService = builder.nodeService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public CompareContentResult execute()
    {
        final NodeId nodeId = NodeId.from( contentId.toString() );

        final NodeComparison compareResult = this.nodeService.compare( nodeId, this.target );

        return CompareResultTranslator.translate( compareResult );
    }

    public static class Builder
    {
        private ContentId contentId;

        private BranchId target;

        private NodeService nodeService;

        public Builder id( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder target( final BranchId target )
        {
            this.target = target;
            return this;
        }

        public CompareContentCommand build()
        {
            return new CompareContentCommand( this );
        }
    }

}
