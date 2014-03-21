package com.enonic.wem.core.entity;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.entity.CreateNodeResult;
import com.enonic.wem.api.command.entity.UpdateNodeResult;
import com.enonic.wem.api.entity.CreateNodeParams;
import com.enonic.wem.api.entity.DeleteNodeByIdParams;
import com.enonic.wem.api.entity.DeleteNodeByPathParams;
import com.enonic.wem.api.entity.GetNodeByIdParams;
import com.enonic.wem.api.entity.GetNodeByPathParams;
import com.enonic.wem.api.entity.GetNodesByIdsParams;
import com.enonic.wem.api.entity.GetNodesByParentParams;
import com.enonic.wem.api.entity.GetNodesByPathsParams;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.UpdateNodeParams;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.jcr.provider.JcrSessionProvider;

public class NodeServiceImpl
    implements NodeService
{
    @Inject
    private IndexService indexService;

    private Session session;

    @Inject
    public void setJcrSessionProvider( final JcrSessionProvider jcrSessionProvider )
    {
        try
        {
            session = jcrSessionProvider.login();
        }
        catch ( final RuntimeException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            throw new SystemException( e, e.getMessage() );
        }
    }

    @Override
    public CreateNodeResult create( CreateNodeParams params )
    {
        return CreateNodeCommand.create().
            params( params ).
            indexService( this.indexService ).
            session( this.session ).
            build().
            execute();
    }

    @Override
    public UpdateNodeResult update( final UpdateNodeParams params )
    {
        return UpdateNodeCommand.create().
            params( params ).
            indexService( this.indexService ).
            session( this.session ).
            build().
            execute();
    }

    @Override
    public Node getById( GetNodeByIdParams params )
    {
        return new GetNodeByIdCommand().params( params ).session( this.session ).execute();
    }

    @Override
    public Nodes getByIds( GetNodesByIdsParams params )
    {
        return new GetNodesByIdsCommand().params( params ).session( this.session ).execute();
    }

    @Override
    public Node getByPath( GetNodeByPathParams params )
    {
        return new GetNodeByPathCommand().params( params ).session( this.session ).execute();
    }

    @Override
    public Nodes getByPaths( GetNodesByPathsParams params )
    {
        return new GetNodesByPathsCommand().params( params ).session( this.session ).execute();
    }

    @Override
    public Nodes getByParent( GetNodesByParentParams params )
    {
        return new GetNodesByParentCommand().params( params ).session( this.session ).execute();
    }

    @Override
    public Node deleteById( DeleteNodeByIdParams params )
    {
        return new DeleteNodeByIdCommand().params( params ).indexService( this.indexService ).execute();
    }

    @Override
    public Node deleteByPath( DeleteNodeByPathParams params )
    {
        return DeleteNodeByPathCommand.create().
            params( params ).
            indexService( this.indexService ).
            session( this.session ).
            build().
            execute();
    }
}
