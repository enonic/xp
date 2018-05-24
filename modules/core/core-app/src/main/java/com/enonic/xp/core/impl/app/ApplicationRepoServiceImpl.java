package com.enonic.xp.core.impl.app;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.CreateNodeParams;
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

@Component
public class ApplicationRepoServiceImpl
    implements ApplicationRepoService
{
    final static NodePath APPLICATION_PATH = NodePath.create( NodePath.ROOT, "/applications" ).build();

    private NodeService nodeService;

    private IndexService indexService;

    @SuppressWarnings("unused")
    @Activate
    public void initialize( final BundleContext context )
    {
        ApplicationRepoInitializer.create().
            setIndexService( indexService ).
            setNodeService( nodeService ).
            build().
            initialize();
    }

    public Node createApplicationNode( final Application application, final ByteSource source )
    {
        final CreateNodeParams createNodeParams = ApplicationNodeTransformer.toCreateNodeParams( application, source );

        return this.nodeService.create( createNodeParams );
    }

    @Override
    public Node updateApplicationNode( final Application application, final ByteSource source )
    {
        final String appName = application.getKey().getName();

        final Node existingNode = doGetNodeByName( appName );

        if ( existingNode == null )
        {
            throw new RuntimeException(
                "Expected to find existing node in repository for application with key [" + application.getKey() + "]" );
        }

        return this.nodeService.update( ApplicationNodeTransformer.toUpdateNodeParams( application, source, existingNode ) );
    }

    @Override
    public void deleteApplicationNode( final Application application )
    {
        this.nodeService.deleteByPath( NodePath.create( APPLICATION_PATH, application.getKey().getName() ).build() );
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
        this.nodeService.refresh( RefreshMode.ALL );

        final FindNodesByParentResult byParent =
            ApplicationHelper.runAsAdmin( () -> this.nodeService.findByParent( FindNodesByParentParams.create().
                parentPath( APPLICATION_PATH ).
                build() ) );

        return this.nodeService.getByIds( byParent.getNodeIds() );
    }

    @Override
    public Node updateStartedState( final ApplicationKey appKey, final boolean started )
    {
        final Node applicationNode = this.nodeService.getByPath( NodePath.create( APPLICATION_PATH, appKey.getName() ).build() );

        if ( applicationNode == null )
        {
            throw new NodeNotFoundException( "Didnt find application node in repo" );
        }

        return this.nodeService.update( UpdateNodeParams.create().
            id( applicationNode.id() ).
            editor( toBeEdited -> toBeEdited.data.setBoolean( ApplicationPropertyNames.STARTED, started ) ).
            build() );
    }

    private Node doGetNodeByName( final String applicationName )
    {
        return this.nodeService.getByPath( NodePath.create( APPLICATION_PATH, applicationName ).build() );
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    @Reference
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
