package com.enonic.xp.core.impl.content;

import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ResolvePublishDependenciesResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.SyncWorkResolverParams;

public class ResolvePublishDependenciesCommand
    extends AbstractContentCommand
{
    private final ContentIds contentIds;

    private final ContentIds excludedContentIds;

    private final Branch target;

    private final ResolvePublishDependenciesResult.Builder resultBuilder;

    private final boolean includeChildren;

    private ResolvePublishDependenciesCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
        this.excludedContentIds = builder.excludedContentIds;
        this.target = builder.target;
        this.resultBuilder = ResolvePublishDependenciesResult.create();
        this.includeChildren = builder.includeChildren;
    }

    ResolvePublishDependenciesResult execute()
    {
        resolveDependencies();

        return resultBuilder.build();
    }

    private void resolveDependencies()
    {
        for ( final ContentId contentId : this.contentIds )
        {
            final NodeIds syncWorkResult = getWorkResult( contentId, excludedContentIds, includeChildren );

            this.resultBuilder.addAll( ContentNodeHelper.toContentIds( syncWorkResult ) );
        }
    }

    private NodeIds getWorkResult( final ContentId contentId, final ContentIds excludedContentIds, boolean includeChildren )
    {

       final NodeIds nodeIds = excludedContentIds != null ? NodeIds.from( excludedContentIds.
            stream().
            map( id -> NodeId.from( id.toString() ) ).
            collect( Collectors.toList() ) ) : NodeIds.empty();

        return nodeService.resolveSyncWork( SyncWorkResolverParams.create().
            includeChildren( includeChildren ).
            nodeId( NodeId.from( contentId.toString() ) ).
            excludedNodeIds(nodeIds ).
            branch( this.target ).
            build() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentIds contentIds;

        private ContentIds excludedContentIds;

        private Branch target;

        private boolean includeChildren = true;

        public Builder contentIds( final ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder excludedContentIds( final ContentIds excludedContentIds )
        {
            this.excludedContentIds = excludedContentIds;
            return this;
        }

        public Builder target( final Branch target )
        {
            this.target = target;
            return this;
        }

        public Builder includeChildren( final boolean includeChildren )
        {
            this.includeChildren = includeChildren;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( contentIds );
        }

        public ResolvePublishDependenciesCommand build()
        {
            validate();
            return new ResolvePublishDependenciesCommand( this );
        }

    }
}
