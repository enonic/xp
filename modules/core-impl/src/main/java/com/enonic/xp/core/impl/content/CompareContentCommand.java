package com.enonic.xp.core.impl.content;

import com.enonic.wem.api.content.CompareContentResult;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.node.NodeComparison;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.api.branch.Branch;

public class CompareContentCommand
{
    private final ContentId contentId;

    private final Branch target;

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

        private Branch target;

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

        public Builder target( final Branch target )
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
