package com.enonic.xp.repo.impl.node;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.node.FindNodesWithVersionDifferenceParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.branch.storage.NodeBranchMetadata;
import com.enonic.xp.repo.impl.search.SearchService;

public class ResolveSyncWorkCommand
    extends AbstractNodeCommand
{
    private final Branch target;

    private final NodePath repositoryRoot;

    private final boolean includeChildren;

    private final Node publishRootNode;

    private final Set<NodeId> processedIds;

    private boolean allPossibleNodesAreIncluded;

    private final NodeIds.Builder result;

    private static final Logger LOG = LoggerFactory.getLogger( ResolveSyncWorkCommand.class );

    private ResolveSyncWorkCommand( final Builder builder )
    {
        super( builder );
        this.target = builder.target;
        this.includeChildren = builder.includeChildren;
        this.repositoryRoot = builder.repositoryRoot;
        this.result = NodeIds.create();
        this.processedIds = Sets.newHashSet();
        this.allPossibleNodesAreIncluded = false;

        final Node publishRootNode = doGetById( builder.nodeId );

        if ( publishRootNode == null )
        {
            throw new NodeNotFoundException( "Root node for publishing not found. Id: " + builder.nodeId );
        }

        this.publishRootNode = publishRootNode;

        if ( this.repositoryRoot.equals( this.publishRootNode.path() ) )
        {
            this.allPossibleNodesAreIncluded = true;
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeIds execute()
    {
        final NodeVersionDiffResult diff = getInitialDiff();

        for ( final NodeId nodeId : diff.getNodesWithDifferences() )
        {
            resolveDiff( nodeId );
        }

        return result.build();
    }

    private NodeVersionDiffResult getInitialDiff()
    {
        if ( !includeChildren )
        {
            return NodeVersionDiffResult.create().
                add( this.publishRootNode.id() ).
                build();
        }

        return findNodesWithVersionDifference( this.publishRootNode.path() );
    }

    private NodeVersionDiffResult findNodesWithVersionDifference( final NodePath nodePath )
    {
        return FindNodesWithVersionDifferenceCommand.create().
            query( FindNodesWithVersionDifferenceParams.create().
                target( target ).
                source( ContextAccessor.current().getBranch() ).
                nodePath( nodePath ).
                size( SearchService.GET_ALL_SIZE_FLAG ).
                build() ).
            searchService( this.searchService ).
            build().
            execute();
    }

    private void resolveDiff( final NodeId nodeId )
    {
        if ( isProcessed( nodeId ) )
        {
            return;
        }

        this.processedIds.add( nodeId );

        doResolveDiff( nodeId );
    }

    private void doResolveDiff( final NodeId nodeId )
    {
        final NodeComparison comparison = getNodeComparison( nodeId );

        if ( nodeNotChanged( comparison ) || nodeNotInSource( comparison ) )
        {
            return;
        }

        this.result.add( nodeId );

        if ( !allPossibleNodesAreIncluded )
        {
            final Node node = GetNodeByIdCommand.create( this ).
                id( nodeId ).
                build().
                execute();

            ensureThatParentExists( nodeId );
            if ( !nodePendingDelete( comparison ) )
            {
                includeReferences( node );
            }
            if ( nodePendingDelete( comparison ) )
            {
                includeChildren( node );
            }
        }
    }

    private boolean nodeNotInSource( final NodeComparison comparison )
    {
        return comparison.getCompareStatus() == CompareStatus.NEW_TARGET;
    }

    private boolean nodeNotChanged( final NodeComparison comparison )
    {
        return comparison.getCompareStatus() == CompareStatus.EQUAL;
    }

    private boolean nodePendingDelete( final NodeComparison comparison )
    {
        return comparison.getCompareStatus() == CompareStatus.PENDING_DELETE;
    }

    private void ensureThatParentExists( final NodeId nodeId )
    {
        final Context context = ContextAccessor.current();

        final NodePath parentPath = this.storageService.getParentPath( nodeId, InternalContext.from( context ) );

        if ( parentPath != null && !parentPath.equals( NodePath.ROOT ) )
        {
            final NodeId parentId = this.storageService.getIdForPath( parentPath, InternalContext.from( context ) );

            final NodeComparison nodeComparison = getNodeComparison( parentId );

            if ( shouldBeResolvedDiffFor( nodeComparison ) )
            {
                resolveDiff( parentId );
            }
        }
    }

    private void includeReferences( final Node node )
    {

        final ImmutableList<Property> references = node.data().getProperties( ValueTypes.REFERENCE );

        final NodeIds.Builder referredNodeIds = NodeIds.create();

        references.stream().filter( Property::hasNotNullValue ).forEach( reference -> {
            referredNodeIds.add( reference.getReference().getNodeId() );
        } );

        for ( final NodeId referredNodeId : referredNodeIds.build() )
        {
            if ( !this.processedIds.contains( referredNodeId ) )
            {
                final NodeBranchMetadata nodeBranchMetadata =
                    this.storageService.getBranchNodeVersion( referredNodeId, InternalContext.from( ContextAccessor.current() ) );

                if ( nodeBranchMetadata != null )
                {
                    resolveDiff( referredNodeId );
                }
                else
                {
                    LOG.warn( "Node with id: " + referredNodeId + " referred to from node " + node.path() + " not found" );
                }
            }
        }
    }

    private void includeChildren( final Node node )
    {
        findNodesWithVersionDifference( node.path() ).
            getNodesWithDifferences().
            stream().
            filter( childNodeId -> !this.processedIds.contains( childNodeId ) ).
            forEach( childNodeId -> resolveDiff( childNodeId ) );
    }

    private boolean shouldBeResolvedDiffFor( final NodeComparison nodeComparison )
    {
        final CompareStatus status = nodeComparison.getCompareStatus();
        return status == CompareStatus.NEW || status == CompareStatus.MOVED;
    }

    private boolean isProcessed( final NodeId nodeId )
    {
        return this.processedIds.contains( nodeId );
    }

    private NodeComparison getNodeComparison( final NodeId nodeId )
    {
        return CompareNodeCommand.create().
            target( this.target ).
            storageService( this.storageService ).
            nodeId( nodeId ).
            build().
            execute();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId nodeId;

        private Branch target;

        private boolean includeChildren = true;

        private NodePath repositoryRoot = NodePath.ROOT;

        private Builder()
        {
        }

        public Builder repositoryRoot( final NodePath repositoryRoot )
        {
            this.repositoryRoot = repositoryRoot;
            return this;
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
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

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( nodeId, "nodeId must be provided" );
            Preconditions.checkNotNull( target, "target branch must be provided" );
        }

        public ResolveSyncWorkCommand build()
        {
            validate();
            return new ResolveSyncWorkCommand( this );
        }
    }

}