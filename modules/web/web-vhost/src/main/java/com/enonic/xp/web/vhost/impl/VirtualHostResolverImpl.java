package com.enonic.xp.web.vhost.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostResolver;
import com.enonic.xp.web.vhost.VirtualHostService;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostIdProvidersMapping;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

@Component(immediate = true)
public class VirtualHostResolverImpl
    implements VirtualHostResolver
{
    private final List<VirtualHostMatcher> virtualHostMappings;

    @Activate
    public VirtualHostResolverImpl( @Reference final VirtualHostService virtualHostService )
    {
        this.virtualHostMappings = virtualHostService.getVirtualHosts()
            .stream()
            .sorted( Comparator.comparing( VirtualHost::getOrder )
                         .thenComparing( VirtualHost::getSource, Comparator.comparing( String::length ).reversed() )
                         .thenComparing( VirtualHost::getSource ) )
            .flatMap( virtualHost -> Stream.of( virtualHost.getHost().split( " ", -1 ) )
                .map( String::trim )
                .map( host -> new VirtualHostMatcher( host, virtualHost ) ) )
            .collect( Collectors.toList() );
    }

    @Override
    public VirtualHost resolveVirtualHost( final HttpServletRequest req )
    {
        String serverName = req.getServerName();
        return virtualHostMappings.stream()
            .map( virtualHost -> virtualHost.matches( serverName, req.getRequestURI() ) )
            .filter( Objects::nonNull )
            .findFirst()
            .orElse( null );
    }

    private static final class VirtualHostMatcher
    {
        private final VirtualHost virtualHost;

        private final String originalHost;

        private final Pattern pattern;

        VirtualHostMatcher( String originalHost, VirtualHost virtualHost )
        {
            this.originalHost = originalHost;
            this.virtualHost = virtualHost;
            this.pattern = originalHost.startsWith( "~" ) ? Pattern.compile( originalHost.substring( 1 ), Pattern.CASE_INSENSITIVE ) : null;
        }

        VirtualHostMapping matches( String serverName, String requestURI )
        {
            if ( pattern != null )
            {
                Matcher matcher = pattern.matcher( serverName );
                if ( matcher.matches() && matchesSource( requestURI ) )
                {
                    return new VirtualHostMapping( virtualHost.getName(), serverName, virtualHost.getSource(),
                                                   matcher.replaceAll( virtualHost.getTarget() ), createIdProvidersMapping(),
                                                   virtualHost.getOrder(), virtualHost.getContext() );
                }
            }
            else if ( originalHost.equalsIgnoreCase( serverName ) && matchesSource( requestURI ) )
            {
                return new VirtualHostMapping( virtualHost.getName(), serverName, virtualHost.getSource(), virtualHost.getTarget(),
                                               createIdProvidersMapping(), virtualHost.getOrder(), virtualHost.getContext() );
            }
            return null;
        }

        boolean matchesSource( String requestURI )
        {
            return "/".equals( virtualHost.getSource() ) || requestURI.equals( virtualHost.getSource() ) ||
                requestURI.startsWith( virtualHost.getSource() + "/" );
        }

        VirtualHostIdProvidersMapping createIdProvidersMapping()
        {
            VirtualHostIdProvidersMapping.Builder idProvidersMapping = VirtualHostIdProvidersMapping.create();
            if ( virtualHost.getDefaultIdProviderKey() != null )
            {
                idProvidersMapping.setDefaultIdProvider( virtualHost.getDefaultIdProviderKey() );
            }
            if ( virtualHost.getIdProviderKeys() != null )
            {
                virtualHost.getIdProviderKeys().forEach( idProvidersMapping::addIdProviderKey );
            }
            return idProvidersMapping.build();
        }

    }
}
