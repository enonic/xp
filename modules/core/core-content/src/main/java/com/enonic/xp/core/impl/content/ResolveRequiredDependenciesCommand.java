package com.enonic.xp.core.impl.content;

import java.util.Collection;
import java.util.Objects;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.node.NodeCompareStatus;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;

public class ResolveRequiredDependenciesCommand
    extends AbstractContentCommand
{
    private final ContentIds contentIds;


    private ResolveRequiredDependenciesCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    ContentIds execute()
    {
        final NodeComparisons nodeComparisons =
            nodeService.compare( ContentNodeHelper.toNodeIds( contentIds ), ContentConstants.BRANCH_MASTER );

        final NodePaths parentPaths = getParentPaths( nodeComparisons.getComparisons() );
        final NodePaths resultPaths = nodeComparisons.getSourcePaths();

        return parentPaths.stream()
            .filter( resultPaths::contains )
            .map( nodeComparisons::getBySourcePath )
            .filter( comparison -> NodeCompareStatus.NEWER != comparison.getCompareStatus() )
            .map( NodeComparison::getNodeId )
            .map( ContentId::from )
            .collect( ContentIds.collector() );
    }

    private NodePaths getParentPaths( final Collection<NodeComparison> comparisons )
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

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentIds contentIds;

        public Builder contentIds( final ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Objects.requireNonNull( contentIds, "contentIds is required" );
        }

        public ResolveRequiredDependenciesCommand build()
        {
            validate();
            return new ResolveRequiredDependenciesCommand( this );
        }

    }
}
