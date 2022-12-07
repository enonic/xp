package com.enonic.xp.core.impl.app;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
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
    static final NodePath APPLICATION_PATH = NodePath.create( NodePath.ROOT, "/applications" ).build();

    private final NodeService nodeService;

    private final IndexService indexService;

    public ApplicationRepoServiceImpl( final NodeService nodeService, final IndexService indexService )
    {
        this.nodeService = nodeService;
        this.indexService = indexService;
    }

    public void initialize()
    {
        ApplicationRepoInitializer.create().
            setIndexService( indexService ).
            setNodeService( nodeService ).
            build().
            initialize();
    }

    @Override
    public Node createApplicationNode( final Application application, final ByteSource source )
    {
        return this.nodeService.create( ApplicationNodeTransformer.toCreateNodeParams( application, source ) );
    }

    @Override
    public Node updateApplicationNode( final Application application, final ByteSource source )
    {
        return this.nodeService.update( ApplicationNodeTransformer.toUpdateNodeParams( application, source ) );
    }

    @Override
    public void deleteApplicationNode( final ApplicationKey applicationKey )
    {
        this.nodeService.deleteByPath( NodePath.create( APPLICATION_PATH, applicationKey.getName() ).build() );
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
        return this.nodeService.getByPath( NodePath.create( APPLICATION_PATH, applicationName ).build() );
    }
}
