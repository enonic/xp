package com.enonic.wem.core.content;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.CompareContentResults;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.NodeComparisons;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.entity.Workspace;

public class CompareContentsCommand
{
    private final ContentIds contentIds;

    private final Workspace target;

    private final Context context;

    private final NodeService nodeService;

    private CompareContentsCommand( Builder builder )
    {
        contentIds = builder.contentIds;
        target = builder.target;
        context = builder.context;
        nodeService = builder.nodeService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public CompareContentResults execute()
    {
        final EntityIds entityIds = getAsEntityIds( this.contentIds );
        final NodeComparisons comparisons = this.nodeService.compare( entityIds, this.target, this.context );

        return CompareResultTranslator.translate( comparisons );
    }

    private EntityIds getAsEntityIds( final ContentIds contentIds )
    {
        final Set<EntityId> entityIds = Sets.newHashSet();

        final Iterator<ContentId> iterator = contentIds.iterator();

        while ( iterator.hasNext() )
        {
            entityIds.add( EntityId.from( iterator.next().toString() ) );
        }

        return EntityIds.from( entityIds );
    }


    public static final class Builder
    {
        private ContentIds contentIds;

        private Workspace target;

        private Context context;

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

        public Builder context( Context context )
        {
            this.context = context;
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
