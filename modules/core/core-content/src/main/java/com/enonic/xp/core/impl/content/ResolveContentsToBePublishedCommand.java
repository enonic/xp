package com.enonic.xp.core.impl.content;

import java.util.Set;

import com.google.common.base.Preconditions;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.node.Node;
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

    private final CompareContentResults.Builder resultBuilder;

    private final boolean includeDependencies;

    private ResolveContentsToBePublishedCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
        this.excludedContentIds = builder.excludedContentIds;
        this.resultBuilder = CompareContentResults.create();
        this.excludeChildrenIds = builder.excludeChildrenIds;
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
        final NodeIds nodeIds = excludedContentIds != null ? ContentNodeHelper.toNodeIds( excludedContentIds ) : NodeIds.empty();

        final boolean includeChildren = excludeChildrenIds == null || !this.excludeChildrenIds.contains( contentId );

        return nodeService.resolveSyncWork( SyncWorkResolverParams.create()
                                                .includeChildren( includeChildren )
                                                .includeDependencies( this.includeDependencies )
                                                .nodeId( NodeId.from( contentId ) )
                                                .excludedNodeIds( nodeIds )
                                                .branch( ContentConstants.BRANCH_MASTER )
                                                .statusesToStopDependenciesSearch( Set.of( CompareStatus.EQUAL ) )
                                                .filter( ( ids ) -> nodeService.getByIds( ids )
                                                    .stream()
                                                    .filter( node -> !node.path()
                                                        .getParentPath()
                                                        .toString()
                                                        .startsWith( ArchiveConstants.ARCHIVE_ROOT_PATH.toString() ) )
                                                    .map( Node::id )
                                                    .collect( NodeIds.collector() ) )
                                                .build() );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentIds contentIds;

        private ContentIds excludedContentIds;

        private ContentIds excludeChildrenIds;

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

        public Builder excludeChildrenIds( final ContentIds excludeChildrenIds )
        {
            this.excludeChildrenIds = excludeChildrenIds;
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
            Preconditions.checkNotNull( contentIds );
        }

        public ResolveContentsToBePublishedCommand build()
        {
            validate();
            return new ResolveContentsToBePublishedCommand( this );
        }

    }
}
