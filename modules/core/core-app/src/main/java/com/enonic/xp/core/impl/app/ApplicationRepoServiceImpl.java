package com.enonic.xp.core.impl.app;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.util.BinaryReference;

public class ApplicationRepoServiceImpl
    implements ApplicationRepoService
{
    static final NodePath APPLICATION_PATH = new NodePath( NodePath.ROOT, NodeName.from( "applications" ) );

    private final NodeService nodeService;

    public ApplicationRepoServiceImpl( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    @Override
    public Node upsertApplicationNode( final AppInfo application, final ByteSource source )
    {
        if ( doGetNodeByName( application.name ) != null )
        {
            return this.nodeService.update( ApplicationNodeTransformer.toUpdateNodeParams( application, source ) );
        }
        else
        {
            return this.nodeService.create( ApplicationNodeTransformer.toCreateNodeParams( application, source ) );
        }
    }

    @Override
    public void deleteApplicationNode( final ApplicationKey applicationKey )
    {
        this.nodeService.delete( DeleteNodeParams.create()
                                           .nodePath( new NodePath( APPLICATION_PATH, NodeName.from( applicationKey.getName() ) ) )
                                           .refresh( RefreshMode.ALL )
                                           .build() );
    }

    @Override
    public ByteSource getApplicationSource( final NodeId nodeId )
    {
        return this.nodeService.getBinary( nodeId, BinaryReference.from( ApplicationNodeTransformer.APPLICATION_BINARY_REF ) );
    }

    @Override
    public Node getApplicationNode( final ApplicationKey applicationKey )
    {
        return doGetNodeByName( applicationKey.getName() );
    }

    @Override
    public Nodes getApplications()
    {
        final FindNodesByParentResult byParent =
            ApplicationHelper.runAsAdmin( () -> this.nodeService.findByParent( FindNodesByParentParams.create().
                parentPath( APPLICATION_PATH ).
                build() ) );

        return this.nodeService.getByIds( byParent.getNodeIds() );
    }

    @Override
    public Node updateStartedState( final ApplicationKey appKey, final boolean started )
    {
        final Node applicationNode = doGetNodeByName( appKey.getName() );

        if ( applicationNode == null )
        {
            throw new NodeNotFoundException( "Didnt find application node in repo" );
        }

        return this.nodeService.update( UpdateNodeParams.create()
                                                         .id( applicationNode.id() )
                                                         .editor(
                                                             toBeEdited -> toBeEdited.data.setBoolean( ApplicationPropertyNames.STARTED,
                                                                                                       started ) )
                                                          .refresh( RefreshMode.ALL )
                                                         .build() );
    }

    private Node doGetNodeByName( final String applicationName )
    {
        return this.nodeService.getByPath( new NodePath( APPLICATION_PATH, NodeName.from( applicationName ) ) );
    }
}
