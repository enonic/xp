package com.enonic.xp.cluster.impl;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang.StringUtils.substringBefore;
import static org.apache.commons.lang.StringUtils.substringBetween;

final class NetworkInterfaceResolver
{
    private enum IPVersion
    {
        IPv4, IPv6
    }

    private final Map<String, NetworkInterface> interfaces;

    public NetworkInterfaceResolver()
    {
        interfaces = load();
    }

    /**
     * Resolve the IP address for a network interface.
     * If the value passed is not delimited by the underscore "_" character, it is assumed to be an address and returned unchanged.
     * <p>
     * An optional suffix may be added to specify protocol version: ipv4, ipv6
     * <p>
     * Examples: "192.168.0.1", "lo0:ipv6", "en0", "en4:ipv4", "local"
     *
     * @param addressOrInterfaceName name of the network interface
     * @return ip address resolved if it was an interface expression, null otherwise
     * @throws IllegalArgumentException if an interface expression could not be resolved
     */
    public String resolveAddress( final String addressOrInterfaceName )
    {
        if ( !addressOrInterfaceName.startsWith( "_" ) || !addressOrInterfaceName.endsWith( "_" ) )
        {
            return addressOrInterfaceName;
        }

        final String interfaceName = substringBetween( addressOrInterfaceName, "_" );

        final String ifaceName;
        final IPVersion ipVersion;
        if ( interfaceName.contains( ":" ) )
        {
            ifaceName = substringBefore( interfaceName, ":" );
            String ipProto = substringAfter( interfaceName, ":" );
            ipVersion = parseIPVersion( ipProto );
            if ( ipVersion == null )
            {
                throw new IllegalArgumentException( "Invalid IP version: " + ipProto );
            }
        }
        else
        {
            ifaceName = interfaceName;
            ipVersion = null;
        }

        try
        {
            final String address = doResolveAddress( ifaceName, ipVersion );
            if ( address == null )
            {
                throw new IllegalArgumentException( "Interface address could not be resolved: " + addressOrInterfaceName );
            }
            return address;
        }
        catch ( UnknownHostException | SocketException e )
        {
            throw new IllegalArgumentException( "Error resolving interface address: " + interfaceName, e );
        }
    }

    private String doResolveAddress( final String ifaceName, final IPVersion ipVersion )
        throws UnknownHostException, SocketException
    {
        final InetAddress address;
        if ( "local".equals( ifaceName ) )
        {
            address = getLocalhost( IPVersion.IPv4 );

        }
        else
        {
            final NetworkInterface netInf = interfaces.get( ifaceName );
            if ( netInf == null )
            {
                return null;
            }
            address = resolveInterfaceAddress( netInf, ipVersion );
        }

        return address == null ? null : address.getHostAddress();
    }

    private InetAddress resolveInterfaceAddress( final NetworkInterface netInf, final IPVersion ipVersion )
        throws UnknownHostException, SocketException
    {
        InetAddress address;
        if ( netInf.isLoopback() )
        {
            address = getLocalhost( ipVersion );
        }
        else if ( ipVersion == null )
        {
            address = getFirstAddress( netInf, IPVersion.IPv4 );
            if ( address == null )
            {
                address = getFirstAddress( netInf, IPVersion.IPv6 );
            }
        }
        else
        {
            address = getFirstAddress( netInf, ipVersion );
        }

        return address;
    }

    private Map<String, NetworkInterface> load()
    {
        final Map<String, NetworkInterface> map = new HashMap<>();
        final List<NetworkInterface> nets = getInterfaces();

        for ( NetworkInterface netInt : nets )
        {
            map.put( netInt.getName(), netInt );

        }
        return map;
    }

    private InetAddress getFirstAddress( NetworkInterface netInt, IPVersion ipVersion )
    {
        for ( InetAddress address : Collections.list( netInt.getInetAddresses() ) )
        {
            if ( ( address instanceof Inet4Address && ipVersion == IPVersion.IPv4 ) ||
                ( address instanceof Inet6Address && ipVersion == IPVersion.IPv6 ) )
            {
                return address;
            }
        }
        return null;
    }

    private InetAddress getLocalhost( IPVersion ipVersion )
        throws UnknownHostException
    {
        if ( ipVersion == IPVersion.IPv4 )
        {
            return InetAddress.getByName( "127.0.0.1" );
        }
        else
        {
            return InetAddress.getByName( "::1" );
        }
    }

    private List<NetworkInterface> getInterfaces()
    {
        final List<NetworkInterface> all = new ArrayList<>();
        try
        {
            addAllInterfaces( all, Collections.list( NetworkInterface.getNetworkInterfaces() ) );
        }
        catch ( SocketException e )
        {
            throw new RuntimeException( "Error obtaining network interfaces", e );
        }
        all.sort( Comparator.comparingInt( NetworkInterface::getIndex ) );
        return all;
    }

    private void addAllInterfaces( final List<NetworkInterface> target, final List<NetworkInterface> level )
    {
        if ( !level.isEmpty() )
        {
            target.addAll( level );
            for ( NetworkInterface intf : level )
            {
                addAllInterfaces( target, Collections.list( intf.getSubInterfaces() ) );
            }
        }
    }

    private IPVersion parseIPVersion( final String value )
    {
        if ( value == null )
        {
            return null;
        }
        if ( "ipv4".equalsIgnoreCase( value.trim() ) )
        {
            return IPVersion.IPv4;
        }
        if ( "ipv6".equalsIgnoreCase( value.trim() ) )
        {
            return IPVersion.IPv6;
        }
        return null;
    }
}
