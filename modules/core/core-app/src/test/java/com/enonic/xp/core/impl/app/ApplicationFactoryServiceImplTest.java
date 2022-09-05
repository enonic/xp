package com.enonic.xp.core.impl.app;

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
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeId;
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
    public void init()
    {
        appConfig = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
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
    void findDisabledVirtualApplication()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();
        when( nodeService.findByQuery( any( NodeQuery.class ) ) ).thenReturn( FindNodesByQueryResult.create()
                                                                                  .addNodeHit( NodeHit.create()
                                                                                                   .nodeId( NodeId.from( "123" ) )
                                                                                                   .nodePath(
                                                                                                       NodePath.create( "/app1" ).build() )
                                                                                                   .build() )
                                                                                  .totalHits( 1 )
                                                                                  .hits( 1 )
                                                                                  .build() );
        when( appConfig.virtual_enabled() ).thenReturn( false );

        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService, appConfig );
        service.activate();

        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );

        assertTrue( service.findActiveApplication( applicationKey ).isEmpty() );
    }

    @Test
    void findVirtualApplication()
        throws Exception
    {
        final BundleContext bundleContext = getBundleContext();
        when( nodeService.findByQuery( any( NodeQuery.class ) ) ).thenReturn( FindNodesByQueryResult.create()
                                                                                  .addNodeHit( NodeHit.create()
                                                                                                   .nodeId( NodeId.from( "123" ) )
                                                                                                   .nodePath(
                                                                                                       NodePath.create( "/app1" ).build() )
                                                                                                   .build() )
                                                                                  .totalHits( 1 )
                                                                                  .hits( 1 )
                                                                                  .build() );

        when( appConfig.auditlog_enabled() ).thenReturn( true );
        when( appConfig.virtual_schema_override() ).thenReturn( false );

        final ApplicationFactoryServiceImpl service = new ApplicationFactoryServiceImpl( bundleContext, nodeService, appConfig );
        service.activate();

        final ApplicationKey applicationKey = ApplicationKey.from( "app1" );

        assertEquals( applicationKey, service.findActiveApplication( applicationKey ).get().getKey() );
    }
}
