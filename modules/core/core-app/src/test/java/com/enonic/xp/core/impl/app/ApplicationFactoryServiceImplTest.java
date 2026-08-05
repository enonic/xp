package com.enonic.xp.core.impl.app;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.MultiApplicationUrlResolver;
import com.enonic.xp.core.impl.app.resolver.NodeResourceApplicationUrlResolver;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.resource.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ApplicationFactoryServiceImplTest
    extends BundleBasedTest
{
    @Mock(stubOnly = true)
    private NodeService nodeService;

    @Test
    void lifecycle()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();
        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService );
        service.activate();

        final String appName = "app1";

        final Bundle bundle = deploy( appName, newBundle( appName, true ) );

        assertNotNull( service.getApplication( bundle ) );

        bundle.start();
        assertNotNull( service.getApplication( bundle ) );

        bundle.stop();
        assertNotNull( service.getApplication( bundle ) );

        service.deactivate();

        assertNull( service.getApplication( bundle ) );
    }

    @Test
    void findActiveApplication()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();
        when( nodeService.findByQuery( any( NodeQuery.class ) ) ).thenReturn( FindNodesByQueryResult.create().build() );
        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService );
        service.activate();

        final String appName = "app1";
        final ApplicationKey applicationKey = ApplicationKey.from( appName );

        final Bundle bundle = deploy( appName, newBundle( appName, true ) );

        assertThat( service.findActiveApplication( applicationKey ) ).isEmpty();

        bundle.start();
        assertThat( service.findActiveApplication( applicationKey ) ).isNotEmpty();

        bundle.stop();
        assertThat( service.findActiveApplication( applicationKey ) ).isEmpty();
    }

    @Test
    void findActiveResolver()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();
        when( nodeService.findByQuery( any( NodeQuery.class ) ) ).thenReturn( FindNodesByQueryResult.create().build() );
        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService );
        service.activate();

        final String appName = "app1";
        final ApplicationKey applicationKey = ApplicationKey.from( appName );

        final Bundle bundle = deploy( appName, newBundle( appName, true ) );

        assertThat( service.findResolver( applicationKey, null ) ).isEmpty();

        bundle.start();
        Optional<ApplicationUrlResolver> activeResolver = service.findResolver( applicationKey, null );
        assertThat( activeResolver ).isNotEmpty();
        assertThat( activeResolver.get() ).isInstanceOf( MultiApplicationUrlResolver.class );

        activeResolver = service.findResolver( applicationKey, "virtual" );
        assertThat( activeResolver ).isNotEmpty();
        assertThat( activeResolver.get() ).isInstanceOf( NodeResourceApplicationUrlResolver.class );

        bundle.stop();
        assertThat( service.findResolver( applicationKey, null ) ).isEmpty();
    }

    @Test
    void bundleApplicationWithNamespace_servesFakeCmsYaml()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();
        when( nodeService.findByQuery( any( NodeQuery.class ) ) ).thenReturn( FindNodesByQueryResult.create().build() );

        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );
        final NodePath appPath = new NodePath( NamespaceAppConstants.NAMESPACE_APP_ROOT_PARENT, NodeName.from( "app1" ) );
        when( nodeService.nodeExists( appPath ) ).thenReturn( true );
        when( nodeService.getByPath( appPath ) ).thenReturn(
            Node.create().id( NodeId.from( "app-node" ) ).name( "app1" ).parentPath( NodePath.ROOT ).timestamp( Instant.now() ).build() );

        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService );
        service.activate();

        final Bundle bundle = deploy( "app1", newBundle( "app1", true ) );
        bundle.start();

        final Optional<ApplicationUrlResolver> resolver = service.findResolver( applicationKey, null );
        assertThat( resolver ).isNotEmpty();

        final Resource resource = resolver.get().findResource( "/cms/cms.yaml" );
        assertThat( resource ).isNotNull();
        assertThat( resource.exists() ).isTrue();
    }

    @Test
    void findNamespaceApplicationResolver()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );
        final BundleContext bundleContext = getBundleContext();
        when( nodeService.nodeExists(
            new NodePath( NamespaceAppConstants.NAMESPACE_APP_ROOT_PARENT, NodeName.from( applicationKey.getName() ) ) ) ).thenReturn( true );

        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService );
        service.activate();

        assertThat( service.findActiveApplication( applicationKey ) ).isEmpty();

        final Optional<ApplicationUrlResolver> resolver = service.findResolver( applicationKey, null );
        assertThat( resolver ).isNotEmpty();
        assertThat( resolver.get() ).isInstanceOf( MultiApplicationUrlResolver.class );
    }
}
