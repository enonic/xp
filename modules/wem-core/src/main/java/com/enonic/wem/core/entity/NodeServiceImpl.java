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
import com.enonic.wem.util.Exceptions;

public class NodeServiceImpl
    implements NodeService
{
    @Inject
    private IndexService indexService;

    @Inject
    private JcrSessionProvider jcrSessionProvider;

    @Override
    public CreateNodeResult create( CreateNodeParams params )
    {
        final Session session = getNewSession();
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
    {
        final Session session = getNewSession();
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
    {
        final Session session = getNewSession();
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
    {
        final Session session = getNewSession();
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
    {
        final Session session = getNewSession();
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
    {
        final Session session = getNewSession();
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
    {
        final Session session = getNewSession();
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
    {
        final Session session = getNewSession();
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
    {
        final Session session = getNewSession();
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
    {
        final Session session = getNewSession();
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

    private Session getNewSession()
    {
        try
        {
            return this.jcrSessionProvider.login();
        }
        catch ( final Exception e )
        {
            throw Exceptions.newRutime( "Error creating new JCR session" ).withCause( e );
        }
    }
}
