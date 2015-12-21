package com.enonic.xp.server.udc.impl;

import java.net.URL;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

final class UdcUrlBuilder
{
    private final String baseUrl;

    public UdcUrlBuilder( final String baseUrl )
    {
        this.baseUrl = baseUrl;
    }

    public URL build( final UdcInfo info )
        throws Exception
    {
        final Map<String, String> map = toMap( info );
        final String params = Joiner.on( '&' ).withKeyValueSeparator( "=" ).join( map );
        return new URL( this.baseUrl + "?" + params );
    }

    private Map<String, String> toMap( final UdcInfo info )
    {
        final Map<String, String> map = Maps.newHashMap();
        map.put( "product", info.getProduct() );
        map.put( "version", info.getVersion() );
        map.put( "version_hash", info.getVersionHash() );
        map.put( "java_version", info.getJavaVersion() );
        map.put( "os_name", info.getOsName() );
        map.put( "hardware_address", info.getHardwareAddress() );
        map.put( "num_cpu", String.valueOf( info.getNumCpu() ) );
        map.put( "max_memory", String.valueOf( info.getMaxMemory() ) );
        map.put( "ping_count", String.valueOf( info.getCount() ) );
        return map;
    }
}
