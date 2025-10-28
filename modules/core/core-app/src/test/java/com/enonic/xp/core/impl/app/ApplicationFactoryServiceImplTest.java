package com.enonic.xp.core.impl.app;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ApplicationFactoryServiceImplTest
    extends BundleBasedTest
{
    @Mock(stubOnly = true)
    private NodeService nodeService;

    private AppConfig appConfig;

    @BeforeEach
    void init()
    {
        appConfig = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( appConfig.virtual_enabled() ).thenReturn( true );
    }

    @Test
    void lifecycle()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();
        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService, appConfig );
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
        when( appConfig.virtual_enabled() ).thenReturn( true );

        final BundleContext bundleContext = getBundleContext();
        when( nodeService.findByQuery( any( NodeQuery.class ) ) ).thenReturn( FindNodesByQueryResult.create().build() );
        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService, appConfig );
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
        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService, appConfig );
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
    void findDisabledVirtualApplication()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );
        final BundleContext bundleContext = getBundleContext();
        when( nodeService.nodeExists(
            new NodePath( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT, NodeName.from( applicationKey.getName() ) ) ) ).thenReturn( true );

        when( appConfig.virtual_enabled() ).thenReturn( false );

        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService, appConfig );
        service.activate();


        assertTrue( service.findActiveApplication( applicationKey ).isEmpty() );
    }

    @Test
    void findVirtualApplication()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );
        final BundleContext bundleContext = getBundleContext();
        when( nodeService.nodeExists(
            new NodePath( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT, NodeName.from( applicationKey.getName() ) ) ) ).thenReturn( true );

        when( appConfig.virtual_enabled() ).thenReturn( true );
        when( appConfig.virtual_schema_override() ).thenReturn( false );

        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService, appConfig );
        service.activate();

        assertEquals( applicationKey, service.findActiveApplication( applicationKey ).get().getKey() );
    }
}
