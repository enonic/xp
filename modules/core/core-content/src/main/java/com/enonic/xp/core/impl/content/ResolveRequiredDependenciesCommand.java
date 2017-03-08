package com.enonic.xp.core.impl.content;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeComparisons;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;

public class ResolveRequiredDependenciesCommand
    extends AbstractContentCommand
{
    private final ContentIds contentIds;

    private final Branch target;

    private final ContentIds.Builder resultBuilder;

    private ResolveRequiredDependenciesCommand( final Builder builder )
    {
        super( builder );
        this.contentIds = builder.contentIds;
        this.target = builder.target;
        this.resultBuilder = ContentIds.create();
    }

    public static Builder create()
    {
        return new Builder();
    }

    ContentIds execute()
    {
        resolveDependencies();

        return resultBuilder.build();
    }

    private void resolveDependencies()
    {

        final NodeComparisons nodeComparisons = nodeService.compare( NodeIds.from( contentIds.asStrings() ), target );

        this.resultBuilder.addAll( getRequiredIds( nodeComparisons ) );
    }

    private ContentIds getRequiredIds( final NodeComparisons nodeComparisons )
    {
        final NodePaths parentPaths = getParentPaths( nodeComparisons.getComparisons() );
        final NodePaths resultPaths = nodeComparisons.getSourcePaths();

        final Set<ContentId> requiredIds = parentPaths.stream().
            filter( resultPaths::contains ).
            map( parentPath -> {
                final NodeComparison comparison = nodeComparisons.getBySourcePath( parentPath );

                if ( !CompareStatus.NEWER.equals( comparison.getCompareStatus() ) )
                {
                    return ContentId.from( comparison.getNodeId().toString() );
                }
                return null;
            } ).
            filter( Objects::nonNull ).
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

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentIds contentIds;

        private Branch target;

        public Builder contentIds( final ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder target( final Branch target )
        {
            this.target = target;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( contentIds );
        }

        public ResolveRequiredDependenciesCommand build()
        {
            validate();
            return new ResolveRequiredDependenciesCommand( this );
        }

    }
}
