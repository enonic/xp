package com.enonic.xp.lib.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryNotFoundException;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public abstract class BaseNodeHandler
    implements ScriptBean
{
    protected NodeService nodeService;

    protected RepositoryService repositoryService;

    public final Object execute()
    {
        return doExecute();
    }

    protected abstract Object doExecute();

    protected <T> T valueOrDefault( final T value, final T defValue )
    {
        return value == null ? defValue : value;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.nodeService = context.getService( NodeService.class ).get();
        this.repositoryService = context.getService( RepositoryService.class ).get();
    }

    protected Node doGetNode( final NodeKey nodeKey )
    {
        if ( !nodeKey.isId() )
        {
            return nodeService.getByPath( nodeKey.getAsPath() );
        }
        else
        {
            return nodeService.getById( nodeKey.getAsNodeId() );
        }
    }

    void validateRepo()
    {
        final RepositoryId repoId = ContextAccessor.current().getRepositoryId();

        final Repository repository = this.repositoryService.get( repoId );

        if ( repository == null )
        {
            throw new RepositoryNotFoundException( "Repository with id [" + repoId + "] not found" );
        }
    }
}
