package com.enonic.xp.web.session.impl.hazelcast;

import org.eclipse.jetty.hazelcast.session.SessionDataSerializer;
import org.eclipse.jetty.server.session.SessionData;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.osgi.HazelcastOSGiInstance;
import com.hazelcast.osgi.HazelcastOSGiService;

@Component(immediate = true)
public class HazelcastInstanceActivator
{
    private final HazelcastOSGiService hazelcastOSGiService;

    private HazelcastOSGiInstance hazelcastOSGiInstance;

    @Activate
    public HazelcastInstanceActivator( @Reference final HazelcastOSGiService hazelcastOSGiService )
    {
        this.hazelcastOSGiService = hazelcastOSGiService;
    }

    @Activate
    public void activate()
    {
        SerializerConfig sc = new SerializerConfig().setImplementation( new SessionDataSerializer() ).setTypeClass( SessionData.class );
        Config config = new Config();
        config.getSerializationConfig().addSerializerConfig( sc );
        final JoinConfig join = config.getNetworkConfig().getJoin();
        join.getMulticastConfig().setEnabled( false );
        final TcpIpConfig tcpIpConfig = join.getTcpIpConfig();
        tcpIpConfig.setEnabled( true );
        tcpIpConfig.addMember( "10.0.2.4" );
        tcpIpConfig.addMember( "10.0.2.15" );

        hazelcastOSGiInstance = hazelcastOSGiService.newHazelcastInstance( config );
    }

    @Deactivate
    public void deactivate()
    {
        hazelcastOSGiService.shutdownHazelcastInstance( hazelcastOSGiInstance );
    }
}
