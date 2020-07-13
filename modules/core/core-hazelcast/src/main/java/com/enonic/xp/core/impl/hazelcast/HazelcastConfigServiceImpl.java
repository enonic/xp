package com.enonic.xp.core.impl.hazelcast;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.jetty.hazelcast.session.SessionDataSerializer;
import org.eclipse.jetty.server.session.SessionData;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.config.Config;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.RestApiConfig;
import com.hazelcast.config.RestEndpointGroup;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.kubernetes.HazelcastKubernetesDiscoveryStrategyFactory;
import com.hazelcast.kubernetes.KubernetesProperties;
import com.hazelcast.spi.properties.GroupProperty;

import com.enonic.xp.cluster.ClusterConfig;

import static java.util.Objects.requireNonNullElse;

@Component(configurationPid = "com.enonic.xp.hazelcast")
public class HazelcastConfigServiceImpl
    implements HazelcastConfigService
{
    private final ClusterConfig clusterConfig;

    private final HazelcastConfig hazelcastConfig;

    @Activate
    public HazelcastConfigServiceImpl( @Reference final ClusterConfig clusterConfig, final HazelcastConfig hazelcastConfig )
    {
        this.clusterConfig = clusterConfig;
        this.hazelcastConfig = hazelcastConfig;
    }

    @Override
    public boolean isHazelcastEnabled()
    {
        return clusterConfig.isEnabled();
    }

    @Override
    public Config configure()
    {
        Config config = new Config();

        config.setProperty( GroupProperty.LOGGING_TYPE.getName(), "slf4j" );

        config.setProperty( GroupProperty.SHUTDOWNHOOK_ENABLED.getName(), String.valueOf( false ) );

        config.setProperty( GroupProperty.INITIAL_MIN_CLUSTER_SIZE.getName(), String.valueOf( hazelcastConfig.system_hazelcast_initial_min_cluster_size() ) );

        config.setProperty( GroupProperty.MAX_NO_HEARTBEAT_SECONDS.getName(), String.valueOf( hazelcastConfig.system_hazelcast_max_no_heartbeat_seconds() ) );

        config.setProperty( GroupProperty.HEARTBEAT_INTERVAL_SECONDS.getName(), String.valueOf( hazelcastConfig.system_hazelcast_heartbeat_interval_seconds() ) );

        config.setProperty( GroupProperty.MASTERSHIP_CLAIM_TIMEOUT_SECONDS.getName(), String.valueOf( hazelcastConfig.system_hazelcast_mastership_claim_timeout_seconds() ) );

        config.setProperty( GroupProperty.PHONE_HOME_ENABLED.getName(), String.valueOf( hazelcastConfig.system_hazelcast_phone_home_enabled() ) );

        config.setProperty( GroupProperty.WAIT_SECONDS_BEFORE_JOIN.getName(), String.valueOf( hazelcastConfig.hazelcast_wait_seconds_before_join() ) );

        config.setProperty( GroupProperty.MAX_WAIT_SECONDS_BEFORE_JOIN.getName(), String.valueOf( hazelcastConfig.hazelcast_max_wait_seconds_before_join() ) );

        config.setClassLoader( HazelcastConfigServiceImpl.class.getClassLoader() );

        config.setLiteMember( hazelcastConfig.lightMember() );

        configureNetwork( config );

        configureSerialization( config );

        return config;
    }

    private void configureNetwork( Config config )
    {
        config.setProperty( GroupProperty.TCP_JOIN_PORT_TRY_COUNT.getName(),
                            String.valueOf( hazelcastConfig.system_hazelcast_tcp_join_port_try_count() ) );

        config.setProperty( GroupProperty.PREFER_IPv4_STACK.getName(),
                            String.valueOf( hazelcastConfig.system_hazelcast_prefer_ipv4_stack() ) );

        config.setProperty( GroupProperty.SOCKET_BIND_ANY.getName(), String.valueOf( hazelcastConfig.system_hazelcast_socket_bind_any() ) );

        final NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setPort( hazelcastConfig.network_port() );
        networkConfig.setPortCount( hazelcastConfig.network_portCount() );
        networkConfig.setPortAutoIncrement( hazelcastConfig.network_portAutoIncrement() );

        if ( hazelcastConfig.clusterConfigDefaults() )
        {
            config.setProperty( "hazelcast.local.localAddress", clusterConfig.networkHost() );
            config.setProperty( "hazelcast.local.publicAddress", clusterConfig.networkPublishHost() );
        }
        else
        {
            networkConfig.setPublicAddress( hazelcastConfig.network_publicAddress() );
        }

        configureInterfaces( config );

        configureJoin( config );

        configureRestApi( config );
    }

    private void configureJoin( Config config )
    {
        configureMulticast( config );

        configureTcpIp( config );

        configureKubernetes( config );
    }

    private void configureKubernetes( final Config config )
    {
        if ( hazelcastConfig.network_join_kubernetes_enabled() )
        {
            config.setProperty( GroupProperty.DISCOVERY_SPI_ENABLED.getName(), String.valueOf( true ) );

            final DiscoveryStrategyConfig discoveryStrategyConfig =
                new DiscoveryStrategyConfig( new HazelcastKubernetesDiscoveryStrategyFactory() );
            discoveryStrategyConfig.addProperty( KubernetesProperties.SERVICE_DNS.key(),
                                                 hazelcastConfig.network_join_kubernetes_serviceDns() );
            config.getNetworkConfig().getJoin().getDiscoveryConfig().addDiscoveryStrategyConfig( discoveryStrategyConfig );
        }
    }

    private void configureInterfaces( Config config )
    {
        if ( hazelcastConfig.network_interfaces_enabled() )
        {
            InterfacesConfig interfacesConfig = config.getNetworkConfig().getInterfaces();
            interfacesConfig.setEnabled( true );
            final String interfacesStr = requireNonNullElse( hazelcastConfig.network_interfaces(), "" ).trim();
            List<String> interfaces = Arrays.stream( interfacesStr.split( "," ) ).
                filter( Predicate.not( String::isBlank ) ).
                map( String::trim ).
                collect( Collectors.toUnmodifiableList() );
            interfacesConfig.setInterfaces( interfaces );
        }
    }

    private void configureRestApi( Config config )
    {
        if ( hazelcastConfig.network_restApi_enabled() )
        {
            final RestApiConfig restApiConfig = new RestApiConfig();
            restApiConfig.setEnabled( true );
            final String endpointGroupsConfig = requireNonNullElse( hazelcastConfig.network_restApi_restEndpointGroups(), "" );

            final List<RestEndpointGroup> restEndpointGroups = Arrays.stream( endpointGroupsConfig.split( "," ) ).
                filter( Predicate.not( String::isBlank ) ).
                map( String::trim ).
                map( RestEndpointGroup::valueOf ).collect( Collectors.toList() );

            restApiConfig.setEnabledGroups( restEndpointGroups );
            config.getNetworkConfig().setRestApiConfig( restApiConfig );
        }
    }

    private void configureMulticast( Config config )
    {
        MulticastConfig multicastConfig = config.getNetworkConfig().getJoin().getMulticastConfig();
        multicastConfig.setEnabled( hazelcastConfig.network_join_multicast_enabled() );
    }

    private void configureTcpIp( Config config )
    {
        TcpIpConfig tcpIpConfig = config.getNetworkConfig().getJoin().getTcpIpConfig();
        tcpIpConfig.setEnabled( hazelcastConfig.network_join_tcpIp_enabled() );

        if ( tcpIpConfig.isEnabled() )
        {
            final String membersConfig = requireNonNullElse( hazelcastConfig.network_join_tcpIp_members(), "" ).trim();
            final List<String> members;
            if ( !membersConfig.isEmpty() )
            {
                members = Arrays.stream( membersConfig.split( "," ) ).
                    filter( Predicate.not( String::isBlank ) ).
                    map( String::trim ).
                    collect( Collectors.toUnmodifiableList() );
            }
            else if ( hazelcastConfig.clusterConfigDefaults() )
            {
                members = clusterConfig.discovery().get().stream().
                    map( InetAddress::getHostAddress ).
                    collect( Collectors.toUnmodifiableList() );
            }
            else
            {
                members = List.of();
            }
            tcpIpConfig.setMembers( members );
        }
    }

    private void configureSerialization( Config config )
    {
        SerializationConfig serializationConfig = config.getSerializationConfig();
        SerializerConfig jettySerConfig =
            new SerializerConfig().setImplementation( new SessionDataSerializer() ).setTypeClass( SessionData.class );
        serializationConfig.addSerializerConfig( jettySerConfig );
    }
}
