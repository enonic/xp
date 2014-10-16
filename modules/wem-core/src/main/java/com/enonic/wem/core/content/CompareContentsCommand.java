package com.enonic.wem.core.content;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.CompareContentResults;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.entity.NodeComparisons;
import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.NodeIds;
import com.enonic.wem.core.entity.NodeService;

public class CompareContentsCommand
{
    private final ContentIds contentIds;

    private final Workspace target;

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
        final NodeIds nodeIds = getAsNodeIds( this.contentIds );
        final NodeComparisons comparisons = this.nodeService.compare( nodeIds, this.target );

        return CompareResultTranslator.translate( comparisons );
    }

    private NodeIds getAsNodeIds( final ContentIds contentIds )
    {
        final Set<NodeId> nodeIds = Sets.newHashSet();

        final Iterator<ContentId> iterator = contentIds.iterator();

        while ( iterator.hasNext() )
        {
            nodeIds.add( NodeId.from( iterator.next().toString() ) );
        }

        return NodeIds.from( nodeIds );
    }


    public static final class Builder
    {
        private ContentIds contentIds;

        private Workspace target;

        private NodeService nodeService;

        private Builder()
        {
        }

        public Builder contentIds( ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder target( Workspace target )
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
