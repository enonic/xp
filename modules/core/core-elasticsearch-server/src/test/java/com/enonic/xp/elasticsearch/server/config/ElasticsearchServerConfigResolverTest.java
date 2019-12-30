package com.enonic.xp.elasticsearch.server.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.elasticsearch.server.impl.ElasticsearchServerSettings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ElasticsearchServerConfigResolverTest
{

    @Mock
    ElasticsearchServerConfig serverConfig;

    private static String xpHomePath;

    @BeforeAll
    public static void beforeClass()
        throws IOException
    {
        final Path rootDirectory = Files.createTempDirectory( "es-fixture" );

        Files.createDirectories( rootDirectory.resolve( "elasticsearch" ) );

        final Path xpHome = rootDirectory.resolve( "xp-home" );
        Files.createDirectories( xpHome );

        xpHomePath = xpHome.toAbsolutePath().toString();

        final Path path = xpHome.resolve( "repo" ).resolve( "index" );
        Files.createDirectories( xpHome.resolve( "snapshots" ) );
        Files.createDirectories( path );
        Files.createDirectories( path.resolve( "data" ) );
        Files.createDirectories( path.resolve( "work" ) );
        Files.createDirectories( path.resolve( "logs" ) );
        Files.createDirectories( path.resolve( "plugins" ) );
        Files.createDirectories( path.resolve( "conf" ) );

        System.setProperty( "xp.home", xpHome.toAbsolutePath().toString() );
    }

    @Test
    public void testResolve()
    {
        Mockito.when( serverConfig.embeddedMode() ).thenReturn( true );
        Mockito.when( serverConfig.esServerDir() ).thenReturn( null );
        Mockito.when( serverConfig.cluster_routing_allocation_disk_thresholdEnabled() ).thenReturn( false );
        Mockito.when( serverConfig.cluster_name() ).thenReturn( null );
        Mockito.when( serverConfig.path_conf() ).thenReturn( null );
        Mockito.when( serverConfig.path_data() ).thenReturn( null );
        Mockito.when( serverConfig.path_logs() ).thenReturn( null );
        Mockito.when( serverConfig.path_repo() ).thenReturn( null );
        Mockito.when( serverConfig.path_work() ).thenReturn( null );
        Mockito.when( serverConfig.cluster_name() ).thenReturn( "cluster_name" );
        Mockito.when( serverConfig.cluster_routing_allocation_disk_thresholdEnabled() ).thenReturn( true );
        Mockito.when( serverConfig.gateway_expectedNodes() ).thenReturn( 1 );
        Mockito.when( serverConfig.gateway_recoverAfterTime() ).thenReturn( "5m" );
        Mockito.when( serverConfig.gateway_recoverAfterNodes() ).thenReturn( 1 );
        Mockito.when( serverConfig.http_port() ).thenReturn( "9200" );
        Mockito.when( serverConfig.transport_port() ).thenReturn( "9300" );

        final ElasticsearchServerConfigResolver instance = new ElasticsearchServerConfigResolver( serverConfig );

        final ElasticsearchServerSettings result = instance.resolve();

        assertEquals( xpHomePath + "/../elasticsearch", instance.resolveElasticServerDir() );
        assertEquals( xpHomePath + "/repo/index/conf", result.getPathConf() );
        assertEquals( xpHomePath + "/repo/index/data", result.getPathData() );
        assertEquals( xpHomePath + "/repo/index/logs", result.getPathLogs() );
        assertEquals( xpHomePath + "/snapshots", result.getPathRepo() );
        assertEquals( xpHomePath + "/repo/index/work", result.getPathWork() );
        assertEquals( "cluster_name", result.getClusterName() );
        assertTrue( result.getClusterRoutingAllocationDiskThresholdEnabled() );
        assertEquals( 1, result.getGatewayExpectedNodes() );
        assertEquals( 1, result.getGatewayRecoverAfterNodes() );
        assertEquals( "5m", result.getGatewayRecoverAfterTime() );
        assertEquals( "9200", result.getHttpPort() );
        assertEquals( "9300", result.getTransportPort() );
    }

}
