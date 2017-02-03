package com.enonic.xp.core.impl.content;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ResolveContentsToBePublishedCommandResult;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.SyncWorkResolverParams;

public class ResolveContentsToBePublishedCommand
    extends AbstractContentCommand
{
    private final ContentIds contentIds;

    private final ContentIds excludedContentIds;

    private final ContentIds excludeChildrenIds;

    private final Branch target;

    private final ResolveContentsToBePublishedCommandResult.Builder resultBuilder;

    private final boolean includeDependencies;

    private ResolveContentsToBePublishedCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
        this.excludedContentIds = builder.excludedContentIds;
        this.target = builder.target;
        this.resultBuilder = ResolveContentsToBePublishedCommandResult.create();
        this.excludeChildrenIds = builder.excludeChildrenIds;
        this.includeDependencies = builder.includeDependencies;
    }

    public static Builder create()
    {
        return new Builder();
    }

    ResolveContentsToBePublishedCommandResult execute()
    {
        resolveDependencies();

        return resultBuilder.build();
    }

    private void resolveDependencies()
    {
        for ( final ContentId contentId : this.contentIds )
        {
            final ResolveSyncWorkResult syncWorkResult = getWorkResult( contentId );

            this.resultBuilder.addCompareContentResults( CompareResultTranslator.translate( syncWorkResult.getNodeComparisons() ) );
            this.resultBuilder.addRequiredContentIds( getRequiredIds(syncWorkResult) );
        }
    }

    private ContentIds getRequiredIds( final ResolveSyncWorkResult result )
    {
        final NodePaths parentPaths = getParentPaths( result.getNodeComparisons().getComparisons() );
        final NodePaths resultPaths = result.getNodeComparisons().getSourcePaths();

        final Set<ContentId> requiredIds = parentPaths.stream().
            filter( resultPaths::contains ).
            map( parentPath ->
                 {
                     final NodeComparison comparison = result.getNodeComparisons().getBySourcePath( parentPath );

                     if ( !CompareStatus.NEWER.equals( comparison.getCompareStatus() ) )
                     {
                         return ContentId.from( comparison.getNodeId().toString() );
                     }
                     return null;
                 } ).
            filter(  contentId -> !contentIds.contains( contentId ) ).
            collect( Collectors.toSet() );

        return ContentIds.from( requiredIds );
    }

    private NodePaths getParentPaths( final Collection<NodeComparison> comparisons )
    {

        return getPathsFromComparisons( comparisons );

    }

    private NodePaths getPathsFromComparisons( final Collection<NodeComparison> comparisons )
    {
        final NodePaths.Builder parentPathsBuilder = NodePaths.create();

        for ( final NodeComparison comparison : comparisons )
        {
            addParentPaths( parentPathsBuilder, comparison.getSourcePath() );
        }

        return parentPathsBuilder.build();
    }

    private void addParentPaths( final NodePaths.Builder parentPathsBuilder, final NodePath path )
    {
        if ( path.isRoot() )
        {
            return;
        }

        for ( NodePath parentPath : path.getParentPaths() )
        {
            if ( parentPath.isRoot() )
            {
                return;
            }

            parentPathsBuilder.addNodePath( parentPath );
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
            branch( this.target ).
            build() );
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentIds contentIds;

        private ContentIds excludedContentIds;

        private ContentIds excludeChildrenIds;

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
