package com.enonic.xp.core.internal.net;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class NetworkInterfaceResolver
{
    private enum IPVersion
    {
        IPv4, IPv6
    }

    private final Supplier<String> autoResolver;

    public NetworkInterfaceResolver()
    {
        this( null );
    }

    /**
     * @param autoResolver callback invoked to resolve the "_auto_" alias. It must return a replacement expression
     *                     (for instance "_local_" or "0.0.0.0", but not "_auto_") that is resolved as usual.
     */
    public NetworkInterfaceResolver( final Supplier<String> autoResolver )
    {
        this.autoResolver = autoResolver;
    }

    /**
     * Resolve a single IP address for a network interface expression.
     * Resolves the same addresses as {@link #resolveAddresses(String)} and picks the one Java prefers:
     * preferred protocol family first, non link-local addresses before link-local ones.
     *
     * @param addressOrInterfaceName address, hostname or interface expression
     * @return single resolved ip address
     * @throws IllegalArgumentException if the expression could not be resolved
     */
    public String resolveAddress( final String addressOrInterfaceName )
    {
        return doResolve( addressOrInterfaceName ).stream().min( preferenceOrder() ).orElseThrow().getHostAddress();
    }

    /**
     * Resolve all IP addresses for a network interface expression.
     * If the value passed is not delimited by the underscore "_" character, it is assumed to be an address or hostname
     * and is resolved by name.
     * <p>
     * An optional suffix may be added to specify protocol version: ipv4, ipv6. Without it, addresses of both families are returned.
     * <p>
     * The "_auto_" alias is resolved by the callback passed to the constructor, if any.
     * <p>
     * Examples: "192.168.0.1", "_lo0:ipv6_", "_en0_", "_en4:ipv4_", "_local_", "_auto_"
     *
     * @param addressOrInterfaceName address, hostname or interface expression
     * @return all resolved ip addresses, never empty
     * @throws IllegalArgumentException if the expression could not be resolved
     */
    public List<String> resolveAddresses( final String addressOrInterfaceName )
    {
        return doResolve( addressOrInterfaceName ).stream().map( InetAddress::getHostAddress ).toList();
    }

    private List<InetAddress> doResolve( final String addressOrInterfaceName )
    {
        if ( autoResolver != null && "_auto_".equals( addressOrInterfaceName ) )
        {
            return doResolveExpression( autoResolver.get() );
        }

        return doResolveExpression( addressOrInterfaceName );
    }

    private List<InetAddress> doResolveExpression( final String addressOrInterfaceName )
    {
        if ( !addressOrInterfaceName.startsWith( "_" ) || !addressOrInterfaceName.endsWith( "_" ) )
        {
            try
            {
                return List.of( InetAddress.getAllByName( addressOrInterfaceName ) );
            }
            catch ( UnknownHostException e )
            {
                throw new IllegalArgumentException( "Interface address could not be resolved by name: " + addressOrInterfaceName );
            }
        }

        final String interfaceName = addressOrInterfaceName.substring( 1, addressOrInterfaceName.length() - 1 );

        final String ifaceName;
        final IPVersion ipVersion;

        final int index = interfaceName.indexOf( ':' );
        if ( index == -1 )
        {
            ifaceName = interfaceName;
            ipVersion = null;
        }
        else
        {
            ifaceName = interfaceName.substring( 0, index );
            final String ipProto = interfaceName.substring( index + 1 );
            ipVersion = parseIPVersion( ipProto );
            if ( ipVersion == null )
            {
                throw new IllegalArgumentException( "Invalid IP version: " + ipProto );
            }
        }

        try
        {
            final NetworkInterface netInf = findInterface( ifaceName );
            final List<InetAddress> addresses = netInf == null ? List.of() : getAddresses( netInf, ipVersion );
            if ( addresses.isEmpty() )
            {
                throw new IllegalArgumentException( "Interface address could not be resolved: " + addressOrInterfaceName );
            }
            return addresses;
        }
        catch ( UnknownHostException e )
        {
            throw new IllegalArgumentException( "Error resolving interface address: " + interfaceName, e );
        }
    }

    private NetworkInterface findInterface( final String ifaceName )
    {
        // only up and running interfaces are considered, the match with the lowest index wins
        return getInterfaces()
            .filter( NetworkInterfaceResolver::isUp )
            .filter( "local".equals( ifaceName )
                         ? NetworkInterfaceResolver::isLoopback
                         : netInf -> ifaceName.equals( netInf.getName() ) )
            .min( Comparator.comparingInt( NetworkInterface::getIndex ) )
            .orElse( null );
    }

    private static boolean isUp( final NetworkInterface networkInterface )
    {
        try
        {
            return networkInterface.isUp();
        }
        catch ( SocketException e )
        {
            return false;
        }
    }

    private static boolean isLoopback( final NetworkInterface networkInterface )
    {
        try
        {
            return networkInterface.isLoopback();
        }
        catch ( SocketException e )
        {
            return false;
        }
    }

    private static List<InetAddress> getAddresses( final NetworkInterface netInt, final IPVersion ipVersion )
        throws UnknownHostException
    {
        final List<InetAddress> addresses = new ArrayList<>();
        final List<InetAddress> linkLocal = new ArrayList<>();
        for ( InetAddress address : Collections.list( netInt.getInetAddresses() ) )
        {
            if ( matches( address, ipVersion ) )
            {
                if ( address.isLoopbackAddress() )
                {
                    // re-create from raw bytes to strip the zone/scope id some platforms attach to loopback addresses
                    addresses.add( InetAddress.getByAddress( address.getAddress() ) );
                }
                else if ( address.isLinkLocalAddress() )
                {
                    linkLocal.add( address );
                }
                else
                {
                    addresses.add( address );
                }
            }
        }
        // link-local addresses are a last resort
        return addresses.isEmpty() ? linkLocal : addresses;
    }

    private static boolean matches( final InetAddress address, final IPVersion ipVersion )
    {
        return switch ( ipVersion )
        {
            case null -> true;
            case IPv4 -> address instanceof Inet4Address;
            case IPv6 -> address instanceof Inet6Address;
        };
    }

    private static Comparator<InetAddress> preferenceOrder()
    {
        final boolean preferIPv6 = InetAddress.getLoopbackAddress() instanceof Inet6Address;
        return Comparator.<InetAddress, Boolean>comparing( address -> address instanceof Inet6Address != preferIPv6 )
            .thenComparing( InetAddress::isLinkLocalAddress );
    }

    private static Stream<NetworkInterface> getInterfaces()
    {
        try
        {
            return NetworkInterface.networkInterfaces().flatMap( NetworkInterfaceResolver::withSubInterfaces );
        }
        catch ( SocketException e )
        {
            throw new RuntimeException( "Error obtaining network interfaces", e );
        }
    }

    private static Stream<NetworkInterface> withSubInterfaces( final NetworkInterface netInf )
    {
        return Stream.concat( Stream.of( netInf ), netInf.subInterfaces().flatMap( NetworkInterfaceResolver::withSubInterfaces ) );
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
