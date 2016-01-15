package com.enonic.xp.core.impl.app;

import java.util.concurrent.Callable;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.BinaryReference;

@Component
public class ApplicationRepoServiceImpl
    implements ApplicationRepoService
{
    final static NodePath APPLICATION_PATH = NodePath.create( NodePath.ROOT, "/applications" ).build();

    private NodeService nodeService;

    private IndexService indexService;

    @Activate
    public void activate( final BundleContext context )
    {
        if ( indexService.isMaster() )
        {
            new ApplicationRepoInitializer( this.nodeService ).initialize();
        }
    }

    public Node createApplicationNode( final Application application, final ByteSource source )
    {
        final CreateNodeParams createNodeParams = ApplicationNodeTransformer.toCreateNodeParams( application, source );

        return callWithContext( () -> this.nodeService.create( createNodeParams ) );
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

        return callWithContext(
            () -> this.nodeService.update( ApplicationNodeTransformer.toUpdateNodeParams( application, source, existingNode ) ) );
    }

    @Override
    public ByteSource getApplicationSource( final NodeId nodeId )
    {
        return callWithContext(
            () -> this.nodeService.getBinary( nodeId, BinaryReference.from( ApplicationNodeTransformer.APPLICATION_BINARY_REF ) ) );
    }

    @Override
    public Node getApplicationNode( final String applicationName )
    {
        return doGetNodeByName( applicationName );
    }

    private Node doGetNodeByName( final String applicationName )
    {
        return callWithContext( () -> this.nodeService.getByPath( NodePath.create( APPLICATION_PATH, applicationName ).build() ) );
    }

    private <T> T callWithContext( Callable<T> runnable )
    {
        return this.getContext().callWith( runnable );
    }

    private Context getContext()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return ContextBuilder.from( ApplicationConstants.CONTEXT_APPLICATIONS ).authInfo( authInfo ).build();
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
