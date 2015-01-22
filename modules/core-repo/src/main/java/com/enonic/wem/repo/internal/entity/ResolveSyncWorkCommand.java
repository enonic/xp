package com.enonic.wem.repo.internal.entity;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.CompareStatus;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.ValueTypes;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeComparison;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeVersionDiffQuery;
import com.enonic.wem.api.node.NodeVersionDiffResult;
import com.enonic.wem.api.node.ResolveSyncWorkResult;
import com.enonic.wem.api.workspace.Workspace;

public class ResolveSyncWorkCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private final Workspace target;

    private final boolean includeChildren;

    private final ResolveSyncWorkResult.Builder resultBuilder;

    private final Set<NodeId> processedIds;

    private ResolveSyncWorkCommand( final Builder builder )
    {
        super( builder );
        nodeId = builder.nodeId;
        target = builder.target;
        includeChildren = builder.includeChildren;
        resultBuilder = ResolveSyncWorkResult.create();
        processedIds = Sets.newHashSet();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ResolveSyncWorkResult execute()
    {
        final NodeVersionDiffResult diff = getInitialDiff();

        for ( final NodeId nodeId : diff.getNodesWithDifferences() )
        {
            resolveDiff( nodeId );
        }

        return resultBuilder.build();
    }

    private NodeVersionDiffResult getInitialDiff()
    {
        if ( !includeChildren )
        {
            return NodeVersionDiffResult.create().
                add( this.nodeId ).
                build();
        }

        final NodePath nodePath;

        if ( this.nodeId != null )
        {
            final Node node = getById( nodeId );
            nodePath = node.path();
        }
        else
        {
            nodePath = NodePath.ROOT;
        }

        return FindNodesWithVersionDifferenceCommand.create().
            versionService( this.versionService ).
            query( NodeVersionDiffQuery.create().
                target( target ).
                source( ContextAccessor.current().getWorkspace() ).
                nodePath( nodePath ).
                build() ).
            build().
            execute();
    }


    private void resolveDiff( final Node node )
    {
        if ( isProcessed( node.id() ) )
        {
            return;
        }

        this.processedIds.add( node.id() );

        doResolveDiff( node, node.id() );
    }

    private void resolveDiff( final NodeId nodeId )
    {
        if ( isProcessed( nodeId ) )
        {
            return;
        }

        this.processedIds.add( nodeId );

        final Node node = getById( nodeId );

        doResolveDiff( node, nodeId );
    }

    Node getById( final NodeId nodeId )
    {
        return doGetById( nodeId, false );
    }

    private void doResolveDiff( final Node node, final NodeId nodeId )
    {
        final NodeComparison comparison = getNodeComparison( nodeId );

        addResult( comparison );

        if ( !comparison.getCompareStatus().isDelete() )
        {
            ensureThatParentExists( node );
            includeReferences( node );
        }
    }

    private void ensureThatParentExists( final Node node )
    {
        if ( !node.parent().equals( NodePath.ROOT ) )
        {
            final Node thisParentNode = doGetByPath( node.parent(), false );

            final NodeComparison nodeComparison = getNodeComparison( thisParentNode.id() );

            if ( nodeComparison.getCompareStatus().getStatus().equals( CompareStatus.Status.NEW ) )
            {
                resolveDiff( thisParentNode );
            }
        }
    }

    private boolean isProcessed( final NodeId nodeId )
    {
        return this.processedIds.contains( nodeId );
    }

    private NodeComparison getNodeComparison( final NodeId nodeId )
    {
        return CompareNodeCommand.create().
            target( this.target ).
            workspaceService( this.workspaceService ).
            versionService( this.versionService ).
            nodeId( nodeId ).
            build().
            execute();
    }

    private void includeReferences( final Node node )
    {
        final Set<Property> references = node.data().getByValueType( ValueTypes.REFERENCE );

        for ( final Property reference : references )
        {
            final NodeId referredNodeId = reference.getReference().getNodeId();

            if ( !this.processedIds.contains( referredNodeId ) )
            {
                resolveDiff( referredNodeId );
            }
        }
    }

    private void addResult( final NodeComparison comparison )
    {
        final NodeId nodeId = comparison.getNodeId();

        if ( comparison.getCompareStatus().isConflict() )
        {
            resultBuilder.conflict( nodeId );
        }
        else if ( comparison.getCompareStatus().isDelete() )
        {
            resultBuilder.delete( nodeId );
        }
        else
        {
            resultBuilder.publish( nodeId );
        }
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId nodeId;

        private Workspace target;

        private boolean includeChildren = true;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder target( final Workspace target )
        {
            this.target = target;
            return this;
        }

        public Builder includeChildren( final boolean includeChildren )
        {
            this.includeChildren = includeChildren;
            return this;
        }

        public ResolveSyncWorkCommand build()
        {
            return new ResolveSyncWorkCommand( this );
        }
    }
}
