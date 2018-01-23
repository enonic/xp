package com.enonic.xp.elasticsearch.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.ClusterNodeId;
import com.enonic.xp.cluster.NodeDiscovery;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.Configuration;

import static org.junit.Assert.*;

public class ClusterConfigMergerTest
{
    @Test
    public void merge_hosts()
        throws Exception

    {
        final Configuration config = ClusterConfigMerger.merge( createClusterConfig(), ConfigBuilder.create().
            add( "discovery.unicast.port", "9300" ).
            build() );
        assertEquals( "localhost[9300],192.168.0.1[9300]", config.get( "discovery.zen.ping.unicast.hosts" ) );
    }


    @Test
    public void merge_hosts_port_range_alt_format()
        throws Exception

    {
        final Configuration config = ClusterConfigMerger.merge( createClusterConfig(), ConfigBuilder.create().
            add( "discovery.unicast.port", "9300-9400" ).
            build() );
        assertEquals( "localhost[9300-9400],192.168.0.1[9300-9400]", config.get( "discovery.zen.ping.unicast.hosts" ) );
    }


    private ClusterConfig createClusterConfig()
    {
        return new ClusterConfig()
        {
            @Override
            public NodeDiscovery discovery()
            {
                return createNodeDiscovery();
            }

            @Override
            public ClusterNodeId name()
            {
                return ClusterNodeId.from( "fisk" );
            }
        };
    }

    private NodeDiscovery createNodeDiscovery()
    {
        return () -> {

            final InetAddress local;
            final InetAddress local2;
            try
            {
                local = InetAddress.getByName( "localhost" );
                local2 = InetAddress.getByName( "192.168.0.1" );
            }
            catch ( UnknownHostException e )
            {
                throw new RuntimeException( e );
            }

            return Lists.newArrayList( local, local2 );
        };
    }
}