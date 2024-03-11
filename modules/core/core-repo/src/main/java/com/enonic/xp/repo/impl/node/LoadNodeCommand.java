package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.LoadNodeParams;
import com.enonic.xp.node.LoadNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeLoadException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;

public class LoadNodeCommand
    extends AbstractNodeCommand
{
    private final LoadNodeParams params;

    private LoadNodeCommand( final Builder builder )
    {
        super( builder );
        params = builder.params;
    }

    public LoadNodeResult execute()
    {
        verifyNodeProperties( params.getNode() );

        final Node nodeToLoad = params.getNode();

        if ( nodeToLoad.path().isRoot() )
        {
            return loadRootNode();
        }
        else
        {
            return loadNode();
        }
    }

    private void verifyNodeProperties( final Node node )
    {
        Preconditions.checkArgument( node.id() != null, "NodeId must be set when loading node" );
        Preconditions.checkArgument( node.name() != null, "Node name must be set when loading node" );
        Preconditions.checkArgument( node.isRoot() || node.parentPath() != null, "Node parentPath must be set when loading node" );
        Preconditions.checkArgument( node.getTimestamp() != null, "Node timestamp must be set when loading node" );
    }

    private LoadNodeResult loadNode()
    {
        verifyParentExists();
        deleteIfExistsAtPath();

        final StoreNodeParams storeNodeParams = StoreNodeParams.create().
            node( params.getNode() ).
            nodeCommitId( params.getNodeCommitId() ).
            overrideVersion().
            build();

        final Node loadedNode = this.nodeStorageService.store( storeNodeParams, InternalContext.from( ContextAccessor.current() ) ).node();

        return LoadNodeResult.create().
            node( loadedNode ).
            build();
    }

    private void deleteIfExistsAtPath()
    {
        DeleteNodeCommand.create()
            .nodePath( params.getNode().path() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute();
    }

    private void verifyParentExists()
    {
        final boolean parentExists = nodeExistsAtPath( this.params.getNode().parentPath() );

        if ( !parentExists )
        {
            throw new NodeLoadException(
                String.format( "Cannot load node with path [%s], parent does not exist", params.getNode().path() ) );
        }
    }

    private boolean nodeExistsAtPath( final NodePath nodePath )
    {
        return CheckNodeExistsCommand.create( this ).nodePath( nodePath ).mode( CheckNodeExistsCommand.Mode.SPEED ).build().execute();
    }

    private LoadNodeResult loadRootNode()
    {
        final CreateRootNodeParams createRootNodeParams = CreateRootNodeParams.create().
            permissions( params.getNode().getPermissions() ).
            childOrder( params.getNode().getChildOrder() ).
            build();

        final Node node = CreateRootNodeCommand.create( this ).
            params( createRootNodeParams ).
            build().
            execute();

        return LoadNodeResult.create().
            node( node ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private LoadNodeParams params;

        private Builder()
        {
        }

        public Builder params( final LoadNodeParams val )
        {
            params = val;
            return this;
        }

        public LoadNodeCommand build()
        {
            return new LoadNodeCommand( this );
        }
    }
}
