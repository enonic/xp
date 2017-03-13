package com.enonic.xp.core.impl.content;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.SyncWorkResolverParams;

public class ResolveContentsToBePublishedCommand
    extends AbstractContentCommand
{
    private final ContentIds contentIds;

    private final ContentIds excludedContentIds;

    private final ContentIds excludeChildrenIds;

    private final boolean includeOffline;

    private final Branch target;

    private final CompareContentResults.Builder resultBuilder;

    private final boolean includeDependencies;

    private ResolveContentsToBePublishedCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
        this.excludedContentIds = builder.excludedContentIds;
        this.target = builder.target;
        this.resultBuilder = CompareContentResults.create();
        this.excludeChildrenIds = builder.excludeChildrenIds;
        this.includeOffline = builder.includeOffline;
        this.includeDependencies = builder.includeDependencies;
    }

    public static Builder create()
    {
        return new Builder();
    }

    CompareContentResults execute()
    {
        resolveDependencies();

        return resultBuilder.build();
    }

    private void resolveDependencies()
    {
        for ( final ContentId contentId : this.contentIds )
        {
            final ResolveSyncWorkResult syncWorkResult = getWorkResult( contentId );

            this.resultBuilder.addAll( CompareResultTranslator.translate( syncWorkResult.getNodeComparisons() ) );
        }
    }

    private ResolveSyncWorkResult getWorkResult( final ContentId contentId )
    {
        final NodeIds nodeIds = excludedContentIds != null ? NodeIds.from( excludedContentIds.
            stream().
            map( id -> NodeId.from( id.toString() ) ).
            collect( Collectors.toList() ) ) : NodeIds.empty();

        final boolean includeChildren = excludeChildrenIds != null ? !this.excludeChildrenIds.contains( contentId ) : true;

        return nodeService.resolveSyncWork( SyncWorkResolverParams.create().
            includeChildren( includeChildren ).
            includeDependencies( this.includeDependencies ).
            nodeId( NodeId.from( contentId.toString() ) ).
            excludedNodeIds( nodeIds ).
            initialDiffFilter( this.includeOffline ? null : ( initialDiffNodeIds ) -> this.filterOfflineContents( initialDiffNodeIds ) ).
            branch( this.target ).
            build() );
    }

    private NodeIds filterOfflineContents( final NodeIds nodeIds )
    {
        final NodeComparisons nodeComparisons = nodeService.compare( nodeIds, this.target );
        final Set<NodeId> filteredNodeIdSet = nodeIds.stream().
            filter( nodeId -> {
                final NodeComparison nodeComparison = nodeComparisons.get( nodeId );
                if ( CompareStatus.NEW != nodeComparison.getCompareStatus() )
                {
                    return true;
                }
                final boolean hasFirstPublishProperty = nodeService.getById( nodeId ).data().getInstant(
                    PropertyPath.from( ContentPropertyNames.PUBLISH_INFO, ContentPropertyNames.PUBLISH_FIRST ) ) != null;
                if ( hasFirstPublishProperty )
                {
                    return false;
                }
                return true;
            } ).collect( Collectors.toSet() );
        return NodeIds.from( filteredNodeIdSet );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentIds contentIds;

        private ContentIds excludedContentIds;

        private ContentIds excludeChildrenIds;

        private boolean includeOffline;

        private Branch target;

        private boolean includeDependencies = true;

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

        public Builder excludeChildrenIds( final ContentIds excludeChildrenIds )
        {
            this.excludeChildrenIds = excludeChildrenIds;
            return this;
        }

        public Builder includeOffline( final boolean includeOffline )
        {
            this.includeOffline = includeOffline;
            return this;
        }

        public Builder includeDependencies( final boolean includeDependencies )
        {
            this.includeDependencies = includeDependencies;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( contentIds );
        }

        public ResolveContentsToBePublishedCommand build()
        {
            validate();
            return new ResolveContentsToBePublishedCommand( this );
        }

    }
}
