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
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component
public class ApplicationRepoServiceImpl
    implements ApplicationRepoService
{
    final static NodePath APPLICATION_PATH = NodePath.create( NodePath.ROOT, "/applications" ).build();

    private NodeService nodeService;

    @Activate
    public void activate( final BundleContext context )
    {
        new ApplicationRepoInitializer( this.nodeService ).initialize();
    }

    public Node createApplicationNode( final Application application, final ByteSource source )
    {
        final CreateNodeParams createNodeParams = ApplicationNodeTransformer.toNode( application, source );

        return callWithContext( () -> this.nodeService.create( createNodeParams ) );
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
}
