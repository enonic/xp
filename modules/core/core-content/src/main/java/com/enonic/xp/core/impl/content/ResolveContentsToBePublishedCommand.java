package com.enonic.xp.core.impl.content;

import java.util.LinkedHashSet;
import java.util.Set;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCompareStatus;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.SyncWorkResolverParams;

import static java.util.Objects.requireNonNull;

public class ResolveContentsToBePublishedCommand
    extends AbstractContentCommand
{
    private final ContentIds contentIds;

    private final ContentIds excludedContentIds;

    private final ContentIds excludeDescendantsOf;

    private final CompareContentResults.Builder resultBuilder;

    private final boolean includeDependencies;

    private ResolveContentsToBePublishedCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
        this.excludedContentIds = builder.excludedContentIds;
        this.resultBuilder = CompareContentResults.create();
        this.excludeDescendantsOf = builder.excludeDescendantsOf;
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
        final Set<ContentId> resolved = new LinkedHashSet<>();

        for ( final ContentId contentId : this.contentIds )
        {
            final ResolveSyncWorkResult syncWorkResult =
                getWorkResult( contentId, !this.excludeDescendantsOf.contains( contentId ) );

            addToResult( syncWorkResult ).forEach( resolved::add );
        }

        if ( this.includeDependencies )
        {
            resolveDefaultPageTemplates( resolved );
        }
    }

    /**
     * Pulls in the page template a content renders with when it holds no reference to one. Nothing on the content points at that template,
     * so the reference walk the sync work resolution does cannot reach it, and publishing without it renders a 404.
     */
    private void resolveDefaultPageTemplates( final Set<ContentId> resolved )
    {
        Set<ContentId> pending = resolved;

        // a template drags in its own dependencies, which may in turn render with a default template of their own
        while ( !pending.isEmpty() )
        {
            final Set<ContentId> discovered = new LinkedHashSet<>();

            for ( final ContentId templateId : findDefaultPageTemplates( pending ) )
            {
                if ( !resolved.add( templateId ) || this.excludedContentIds.contains( templateId ) )
                {
                    continue;
                }

                // children of a template are not part of what the content needs to render, only the template itself and its dependencies
                for ( final ContentId contentId : addToResult( getWorkResult( templateId, false ) ) )
                {
                    if ( resolved.add( contentId ) )
                    {
                        discovered.add( contentId );
                    }
                }
            }

            pending = discovered;
        }
    }

    private ContentIds findDefaultPageTemplates( final Set<ContentId> contentIds )
    {
        return FindDefaultPageTemplatesCommand.create()
            .contentIds( ContentIds.from( contentIds ) )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();
    }

    private ContentIds addToResult( final ResolveSyncWorkResult syncWorkResult )
    {
        final CompareContentResults results = CompareResultTranslator.translate( syncWorkResult.getNodeComparisons() );

        this.resultBuilder.addAll( results );

        return results.contentIds();
    }

    private ResolveSyncWorkResult getWorkResult( final ContentId contentId, final boolean includeChildren )
    {
        return nodeService.resolveSyncWork( SyncWorkResolverParams.create()
                                                .includeChildren( includeChildren )
                                                .includeDependencies( this.includeDependencies )
                                                .nodeId( NodeId.from( contentId ) )
                                                .excludedNodeIds( ContentNodeHelper.toNodeIds( excludedContentIds ) )
                                                .branch( ContentConstants.BRANCH_MASTER )
                                                .statusesToStopDependenciesSearch( Set.of( NodeCompareStatus.EQUAL ) )
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

        private ContentIds excludeDescendantsOf;

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

        public Builder excludeDescendantsOf( final ContentIds excludeDescendantsOf )
        {
            this.excludeDescendantsOf = excludeDescendantsOf;
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
            requireNonNull( contentIds, "contentIds is required" );
        }

        public ResolveContentsToBePublishedCommand build()
        {
            validate();
            return new ResolveContentsToBePublishedCommand( this );
        }

    }
}
