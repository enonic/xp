package com.enonic.xp.core.impl.content;

import com.enonic.wem.api.content.CompareContentResults;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.node.NodeComparisons;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.api.branch.Branch;

public class CompareContentsCommand
{
    private final ContentIds contentIds;

    private final Branch target;

    private final NodeService nodeService;

    private CompareContentsCommand( Builder builder )
    {
        contentIds = builder.contentIds;
        target = builder.target;
        nodeService = builder.nodeService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public CompareContentResults execute()
    {
        final NodeIds nodeIds = ContentNodeHelper.toNodeIds( this.contentIds );
        final NodeComparisons comparisons = this.nodeService.compare( nodeIds, this.target );

        return CompareResultTranslator.translate( comparisons );
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private Branch target;

        private NodeService nodeService;

        private Builder()
        {
        }

        public Builder contentIds( ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder target( Branch target )
        {
            this.target = target;
            return this;
        }

        public Builder nodeService( NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        public CompareContentsCommand build()
        {
            return new CompareContentsCommand( this );
        }
    }
}
