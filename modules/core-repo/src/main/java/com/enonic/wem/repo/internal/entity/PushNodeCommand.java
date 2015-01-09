package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.node.PushNodeException;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.workspace.StoreWorkspaceDocument;
import com.enonic.wem.repo.internal.workspace.WorkspaceContext;

public class PushNodeCommand
    extends AbstractNodeCommand
{
    private final Workspace target;

    private final NodeId id;

    private PushNodeCommand( final Builder builder )
    {
        super( builder );
        this.target = builder.target;
        this.id = builder.id;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public Node execute()
    {
        final Context context = ContextAccessor.current();

        final NodeVersionId currentVersion = this.queryService.get( id, IndexContext.from( context ) );

        final Node currentNode = nodeDao.getByVersionId( currentVersion );

        validate( currentNode, context );

        this.workspaceService.store( StoreWorkspaceDocument.create().
            nodeVersionId( currentVersion ).
            node( currentNode ).
            build(), WorkspaceContext.from( this.target, context.getRepositoryId() ) );

        this.indexService.store( currentNode, currentVersion, IndexContext.create().
            workspace( this.target ).
            repositoryId( context.getRepositoryId() ).
            authInfo( context.getAuthInfo() ).
            build() );

        return NodeHasChildResolver.create().
            queryService( this.queryService ).
            build().
            resolve( currentNode );
    }

    public void validate( final Node node, final Context currentContext )
    {
        validateParentExists( node, currentContext );
    }

    private void validateParentExists( final Node node, final Context currentContext )
    {
        if ( node.parent().equals( NodePath.ROOT ) )
        {
            return;
        }

        // Check if current node already exists in target

        final Context targetContext = createTargetContext( currentContext );

        final Node targetParent = targetContext.callWith( () -> doGetByPath( node.parent(), false ) );

        if ( targetParent == null )
        {
            throw new PushNodeException(
                "Push node failed, parent for node '" + node.path() + "'  not found in target " + target.getName() );
        }
    }

    private Context createTargetContext( final Context currentContext )
    {
        final ContextBuilder targetContext = ContextBuilder.create().
            repositoryId( currentContext.getRepositoryId() ).
            workspace( target );

        if ( currentContext.getAuthInfo() != null )
        {
            targetContext.authInfo( currentContext.getAuthInfo() );
        }

        return targetContext.build();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private Workspace target;

        private NodeId id;

        Builder()
        {
            super();
        }

        public Builder target( final Workspace target )
        {
            this.target = target;
            return this;
        }

        public Builder id( final NodeId nodeId )
        {
            this.id = nodeId;
            return this;
        }

        public PushNodeCommand build()
        {
            validate();
            return new PushNodeCommand( this );
        }

        protected void validate()
        {
            Preconditions.checkNotNull( target );
            Preconditions.checkNotNull( id );
        }
    }


}
