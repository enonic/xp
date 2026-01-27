package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

@PublicApi
public class NodeIdExistsException
    extends RuntimeException
{
    private final NodeId nodeId;

    private final NodePath nodePath;

    private final RepositoryId repositoryId;

    private final Branch branch;

    public NodeIdExistsException( final NodeId nodeId, final NodePath nodePath, final RepositoryId repositoryId, final Branch branch )
    {
        super( buildMessage( nodeId, nodePath, repositoryId, branch ) );
        this.nodeId = nodeId;
        this.nodePath = nodePath;
        this.repositoryId = repositoryId;
        this.branch = branch;
    }

    private static String buildMessage( NodeId nodeId, NodePath nodePath, RepositoryId repositoryId, Branch branch )
    {
        final StringBuilder message =
            new StringBuilder().append( "Node " ).append( nodeId ).append( " already exists at " ).append( nodePath );

        if ( repositoryId != null )
        {
            message.append( " repository: " ).append( repositoryId );
        }
        if ( branch != null )
        {
            message.append( " branch: " ).append( branch );
        }

        return message.toString();
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Branch getBranch()
    {
        return branch;
    }


}
