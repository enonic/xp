package com.enonic.xp.core.impl.hazelcast;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.jetty.hazelcast.session.SessionDataSerializer;
import org.eclipse.jetty.session.SessionData;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.config.Config;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.PartitionGroupConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.kubernetes.HazelcastKubernetesDiscoveryStrategyFactory;
import com.hazelcast.kubernetes.KubernetesProperties;
import com.hazelcast.spi.properties.ClusterProperty;

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

        config.setProperty( ClusterProperty.LOGGING_TYPE.getName(), "slf4j" );

        config.setProperty( ClusterProperty.SHUTDOWNHOOK_ENABLED.getName(), String.valueOf( false ) );

        config.setProperty( ClusterProperty.INITIAL_MIN_CLUSTER_SIZE.getName(),
                            String.valueOf( hazelcastConfig.system_hazelcast_initial_min_cluster_size() ) );

        config.setProperty( ClusterProperty.MAX_NO_HEARTBEAT_SECONDS.getName(),
                            String.valueOf( hazelcastConfig.system_hazelcast_max_no_heartbeat_seconds() ) );

        config.setProperty( ClusterProperty.HEARTBEAT_INTERVAL_SECONDS.getName(),
                            String.valueOf( hazelcastConfig.system_hazelcast_heartbeat_interval_seconds() ) );

        config.setProperty( ClusterProperty.MASTERSHIP_CLAIM_TIMEOUT_SECONDS.getName(),
                            String.valueOf( hazelcastConfig.system_hazelcast_mastership_claim_timeout_seconds() ) );

        config.setProperty( ClusterProperty.PHONE_HOME_ENABLED.getName(),
                            String.valueOf( hazelcastConfig.system_hazelcast_phone_home_enabled() ) );

        config.setProperty( ClusterProperty.HEALTH_MONITORING_LEVEL.getName(),
                            String.valueOf( hazelcastConfig.system_hazelcast_health_monitoring_level() ) );

        config.setProperty( ClusterProperty.HEALTH_MONITORING_THRESHOLD_CPU_PERCENTAGE.getName(),
                            String.valueOf( hazelcastConfig.system_hazelcast_health_monitoring_threshold_cpu_percentage() ) );

        config.setProperty( ClusterProperty.HEALTH_MONITORING_THRESHOLD_MEMORY_PERCENTAGE.getName(),
                            String.valueOf( hazelcastConfig.system_hazelcast_health_monitoring_threshold_memory_percentage() ) );

        config.setProperty( ClusterProperty.WAIT_SECONDS_BEFORE_JOIN.getName(),
                            String.valueOf( hazelcastConfig.hazelcast_wait_seconds_before_join() ) );

        config.setProperty( ClusterProperty.MAX_WAIT_SECONDS_BEFORE_JOIN.getName(),
                            String.valueOf( hazelcastConfig.hazelcast_max_wait_seconds_before_join() ) );

        config.setClassLoader( HazelcastConfigServiceImpl.class.getClassLoader() );

        config.setLiteMember( hazelcastConfig.liteMember() || hazelcastConfig.lightMember() );

        configureNetwork( config );

        configureSerialization( config );

        return config;
    }

    private void configureNetwork( Config config )
    {
        config.setProperty( ClusterProperty.TCP_JOIN_PORT_TRY_COUNT.getName(),
                            String.valueOf( hazelcastConfig.system_hazelcast_tcp_join_port_try_count() ) );

        config.setProperty( ClusterProperty.PREFER_IPv4_STACK.getName(),
                            String.valueOf( hazelcastConfig.system_hazelcast_prefer_ipv4_stack() ) );

        config.setProperty( ClusterProperty.SOCKET_BIND_ANY.getName(), String.valueOf( hazelcastConfig.system_hazelcast_socket_bind_any() ) );

        final NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setPort( hazelcastConfig.network_port() );
        networkConfig.setPortCount( hazelcastConfig.network_portCount() );
        networkConfig.setPortAutoIncrement( hazelcastConfig.network_portAutoIncrement() );

        if ( hazelcastConfig.clusterConfigDefaults() )
        {
            networkConfig.setPublicAddress( clusterConfig.networkPublishHost() );
        }
        else
        {
            networkConfig.setPublicAddress( hazelcastConfig.network_publicAddress() );
        }

        configureInterfaces( config );

        configureJoin( config );

        configurePartitionGroup( config );
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
            config.setProperty( ClusterProperty.DISCOVERY_SPI_ENABLED.getName(), String.valueOf( true ) );

            final DiscoveryStrategyConfig discoveryStrategyConfig =
                new DiscoveryStrategyConfig( new HazelcastKubernetesDiscoveryStrategyFactory() );
            discoveryStrategyConfig.addProperty( KubernetesProperties.NAMESPACE.key(),
                                                 requireNonNullElse( hazelcastConfig.network_join_kubernetes_namespace(), "" ) );
            discoveryStrategyConfig.addProperty( KubernetesProperties.SERVICE_NAME.key(),
                                                 requireNonNullElse( hazelcastConfig.network_join_kubernetes_serviceName(), "" ) );
            discoveryStrategyConfig.addProperty( KubernetesProperties.SERVICE_LABEL_NAME.key(),
                                                 requireNonNullElse( hazelcastConfig.network_join_kubernetes_serviceLabelName(), "" ) );
            discoveryStrategyConfig.addProperty( KubernetesProperties.SERVICE_LABEL_VALUE.key(),
                                                 requireNonNullElse( hazelcastConfig.network_join_kubernetes_serviceLabelValue(), "" ) );
            discoveryStrategyConfig.addProperty( KubernetesProperties.POD_LABEL_NAME.key(),
                                                 requireNonNullElse( hazelcastConfig.network_join_kubernetes_podLabelName(), "" ) );
            discoveryStrategyConfig.addProperty( KubernetesProperties.POD_LABEL_VALUE.key(),
                                                 requireNonNullElse( hazelcastConfig.network_join_kubernetes_podLabelValue(), "" ) );
            discoveryStrategyConfig.addProperty( KubernetesProperties.RESOLVE_NOT_READY_ADDRESSES.key(),
                                                 String.valueOf( hazelcastConfig.network_join_kubernetes_resolveNotReadyAddresses() ) );
            discoveryStrategyConfig.addProperty( KubernetesProperties.USE_NODE_NAME_AS_EXTERNAL_ADDRESS.key(),
                                                 String.valueOf( hazelcastConfig.network_join_kubernetes_useNodeNameAsExternalAddress() ) );
            discoveryStrategyConfig.addProperty( KubernetesProperties.KUBERNETES_API_RETIRES.key(),
                                                 String.valueOf( hazelcastConfig.network_join_kubernetes_kubernetesApiRetries() ) );
            discoveryStrategyConfig.addProperty( KubernetesProperties.SERVICE_DNS.key(),
                                                 requireNonNullElse( hazelcastConfig.network_join_kubernetes_serviceDns(), "" ) );
            config.getNetworkConfig().getJoin().getDiscoveryConfig().addDiscoveryStrategyConfig( discoveryStrategyConfig );
        }
    }

    private void configureInterfaces( Config config )
    {
        if ( hazelcastConfig.clusterConfigDefaults() )
        {
            InterfacesConfig interfacesConfig = config.getNetworkConfig().getInterfaces();
            interfacesConfig.setEnabled( true );
            interfacesConfig.setInterfaces( List.of( clusterConfig.networkHost() ) );
        }
        else
        {
            if ( hazelcastConfig.network_interfaces_enabled() )
            {
                InterfacesConfig interfacesConfig = config.getNetworkConfig().getInterfaces();
                interfacesConfig.setEnabled( true );
                final String interfacesStr = requireNonNullElse( hazelcastConfig.network_interfaces(), "" ).trim();
                List<String> interfaces = Arrays.stream( interfacesStr.split( "," ) )
                    .filter( Predicate.not( String::isBlank ) )
                    .map( String::trim )
                    .collect( Collectors.toUnmodifiableList() );
                interfacesConfig.setInterfaces( interfaces );
            }
        }
    }

    private void configurePartitionGroup( final Config config )
    {
        if ( hazelcastConfig.partition_group_enabled() )
        {
            final PartitionGroupConfig partitionGroupConfig = config.getPartitionGroupConfig();
            partitionGroupConfig.setEnabled( true );
            partitionGroupConfig.setGroupType(
                PartitionGroupConfig.MemberGroupType.valueOf( hazelcastConfig.partition_group_groupType() ) );
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
                members = Arrays.stream( membersConfig.split( "," ) )
                    .filter( Predicate.not( String::isBlank ) )
                    .map( String::trim )
                    .collect( Collectors.toUnmodifiableList() );
            }
            else if ( hazelcastConfig.clusterConfigDefaults() )
            {
                members =
                    clusterConfig.discovery().get().stream().map( InetAddress::getHostAddress ).collect( Collectors.toUnmodifiableList() );
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
