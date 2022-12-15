package com.enonic.xp.portal.impl.handler.mapping;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Multimap;
import com.google.common.net.UrlEscapers;

import com.enonic.xp.content.Content;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;

final class ControllerMappingsResolver
{
    private final SiteService siteService;

    ControllerMappingsResolver( final SiteService siteService )
    {
        this.siteService = siteService;
    }

    public Optional<ControllerMappingDescriptor> resolve( final String siteRelativePath, final Multimap<String, String> params,
                                                          final Content content, final SiteConfigs siteConfigs )
    {
        final String contentPath = siteRelativePath != null ? siteRelativePath : content.getPath().toString();
        final String contentUrl = contentPath + normalizedQueryParams( params );

        return siteConfigs.stream()
            .map( SiteConfig::getApplicationKey )
            .map( siteService::getDescriptor )
            .filter( Objects::nonNull )
            .flatMap( siteDescriptor -> siteDescriptor.getMappingDescriptors().stream() )
            .filter( d -> matchesUrlPattern( d, contentPath, contentUrl ) )
            .filter( d -> matchesContent( d, content ) )
            .min( Comparator.comparingInt( ControllerMappingDescriptor::getOrder ) );
    }

    private static String normalizedQueryParams( final Multimap<String, String> params )
    {
        if ( params.isEmpty() )
        {
            return "";
        }

        return params.entries()
            .stream()
            .sorted( Map.Entry.comparingByKey() )
            .map( entry -> urlEscape( entry.getKey() ) + "=" + urlEscape( entry.getValue() ) )
            .collect( Collectors.joining( "&", "?", "" ) );
    }

    private static String urlEscape( final String value )
    {
        return UrlEscapers.urlFormParameterEscaper().escape( value );
    }

    private boolean matchesUrlPattern( final ControllerMappingDescriptor descriptor, final String relativePath, final String relativeUrl )
    {
        final boolean patternWithQueryParameters = descriptor.getPattern().toString().contains( "\\?" );
        final boolean patternMatches = descriptor.getPattern().matcher( patternWithQueryParameters ? relativeUrl : relativePath ).matches();
        return descriptor.invertPattern() != patternMatches;
    }

    private boolean matchesContent( final ControllerMappingDescriptor descriptor, final Content content )
    {
        if ( descriptor.getContentConstraint() == null )
        {
            return true;
        }

        if ( content == null )
        {
            return false;
        }

        return descriptor.getContentConstraint().matches( content );
    }
}
