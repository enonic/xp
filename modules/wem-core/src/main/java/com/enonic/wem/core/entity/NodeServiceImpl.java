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
import com.enonic.wem.api.entity.RenameNodeParams;
import com.enonic.wem.api.entity.UpdateNodeParams;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.jcr.provider.JcrSessionProvider;

public class NodeServiceImpl
    implements NodeService
{
    @Inject
    private IndexService indexService;

    @Inject
    private JcrSessionProvider jcrSessionProvider;

    @Override
    public CreateNodeResult create( CreateNodeParams params )
        throws Exception
    {
        Session session = this.jcrSessionProvider.login();
        try
        {
            return CreateNodeCommand.create().
                params( params ).
                indexService( this.indexService ).
                session( session ).
                build().
                execute();
        }
        finally
        {
            session.logout();
        }
    }

    @Override
    public UpdateNodeResult update( final UpdateNodeParams params )
        throws Exception
    {
        Session session = this.jcrSessionProvider.login();
        try
        {
            return UpdateNodeCommand.create().
                params( params ).
                indexService( this.indexService ).
                session( session ).
                build().
                execute();
        }
        finally
        {
            session.logout();
        }
    }

    @Override
    public boolean rename( final RenameNodeParams params )
        throws Exception
    {
        Session session = this.jcrSessionProvider.login();
        try
        {
            return new RenameNodeCommand().params( params ).indexService( this.indexService ).session( session ).execute();
        }
        finally
        {
            session.logout();
        }
    }

    @Override
    public Node getById( GetNodeByIdParams params )
        throws Exception
    {
        Session session = this.jcrSessionProvider.login();
        try
        {
            return new GetNodeByIdCommand().params( params ).session( session ).execute();
        }
        finally
        {
            session.logout();
        }
    }

    @Override
    public Nodes getByIds( GetNodesByIdsParams params )
        throws Exception
    {
        Session session = this.jcrSessionProvider.login();
        try
        {
            return new GetNodesByIdsCommand().params( params ).session( session ).execute();
        }
        finally
        {
            session.logout();
        }
    }

    @Override
    public Node getByPath( GetNodeByPathParams params )
        throws Exception
    {
        Session session = this.jcrSessionProvider.login();
        try
        {
            return new GetNodeByPathCommand().params( params ).session( session ).execute();
        }
        finally
        {
            session.logout();
        }
    }

    @Override
    public Nodes getByPaths( GetNodesByPathsParams params )
        throws Exception
    {
        Session session = this.jcrSessionProvider.login();
        try
        {
            return new GetNodesByPathsCommand().params( params ).session( session ).execute();
        }
        finally
        {
            session.logout();
        }
    }

    @Override
    public Nodes getByParent( GetNodesByParentParams params )
        throws Exception
    {
        Session session = this.jcrSessionProvider.login();
        try
        {
            return new GetNodesByParentCommand().params( params ).session( session ).execute();
        }
        finally
        {
            session.logout();
        }
    }

    @Override
    public Node deleteById( DeleteNodeByIdParams params )
        throws Exception
    {
        Session session = this.jcrSessionProvider.login();
        try
        {
            return new DeleteNodeByIdCommand().params( params ).indexService( this.indexService ).session( session ).execute();
        }
        finally
        {
            session.logout();
        }
    }

    @Override
    public Node deleteByPath( DeleteNodeByPathParams params )
        throws Exception
    {
        Session session = this.jcrSessionProvider.login();
        try
        {
            return DeleteNodeByPathCommand.create().
                params( params ).
                indexService( this.indexService ).
                session( session ).
                build().
                execute();
        }
        finally
        {
            session.logout();
        }
    }
}
