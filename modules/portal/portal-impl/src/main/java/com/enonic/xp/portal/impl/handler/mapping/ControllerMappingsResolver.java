package com.enonic.xp.portal.impl.handler.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.Multimap;
import com.google.common.net.UrlEscapers;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;

final class ControllerMappingsResolver
{
    private final SiteService siteService;

    private final ContentService contentService;

    private Content contentResolved;

    private Site siteResolved;

    private String queryParams;

    ControllerMappingsResolver( final SiteService siteService, final ContentService contentService )
    {
        this.siteService = siteService;
        this.contentService = contentService;
    }

    public ControllerMappingDescriptor resolve( final PortalRequest request )
    {
        return resolve( request, true );
    }

    public boolean canHandle( final PortalRequest request )
    {
        return resolve( request, false ) != null;
    }

    private ControllerMappingDescriptor resolve( final PortalRequest request, final boolean updateRequest )
    {
        if ( request.getMode() == RenderMode.ADMIN )
        {
            return null;
        }

        contentResolved = request.getContent();
        siteResolved = request.getSite();

        resolveCurrentSite( request );
        if ( siteResolved == null )
        {
            return null;
        }

        final List<ControllerMappingDescriptor> descriptors = getMappingDescriptors( siteResolved );
        if ( descriptors.isEmpty() )
        {
            return null;
        }

        final ControllerMappingDescriptor mappingDescriptor = getFirstMatchingDescriptor( request, siteResolved, descriptors );
        if ( updateRequest && mappingDescriptor != null )
        {
            request.setContent( contentResolved );
            request.setSite( siteResolved );
        }
        return mappingDescriptor;
    }

    private ControllerMappingDescriptor getFirstMatchingDescriptor( final PortalRequest request, final Site site,
                                                                    final List<ControllerMappingDescriptor> descriptors )
    {
        final Map<ApplicationKey, Integer> appsOrder = getAppsOrder( site.getSiteConfigs() );

        return descriptors.stream().
            filter( ( d ) -> matchesUrlPattern( d, request ) ).
            filter( ( d ) -> matchesContent( d, request ) ).
            sorted( ( d1, d2 ) -> {
                if ( d2.compareTo( d1 ) == 0 )
                {
                    // if same order, use the apps order
                    int d1AppIndex = appsOrder.get( d1.getApplication() );
                    int d2AppIndex = appsOrder.get( d2.getApplication() );
                    return Integer.compare( d1AppIndex, d2AppIndex );
                }
                else
                {
                    return d2.compareTo( d1 );
                }
            } ).
            findFirst().
            orElse( null );
    }

    private List<ControllerMappingDescriptor> getMappingDescriptors( final Site site )
    {
        final List<ControllerMappingDescriptor> descriptors = new ArrayList<>();
        site.getSiteConfigs().stream().
            map( SiteConfig::getApplicationKey ).
            map( siteService::getDescriptor ).
            filter( Objects::nonNull ).
            forEach( ( siteDescriptor -> descriptors.addAll( siteDescriptor.getMappingDescriptors().getList() ) ) );
        return descriptors;
    }

    private void resolveCurrentSite( final PortalRequest request )
    {
        if ( request.getContentPath() == null )
        {
            return;
        }

        if ( contentResolved == null )
        {
            contentResolved = getContent( request );
        }

        if ( siteResolved == null )
        {
            siteResolved = getSite( contentResolved );
        }
    }

    private Content getContent( final PortalRequest request )
    {
        final String contentSelector = getContentSelector( request );
        if ( contentSelector == null || "/".equals( contentSelector ) )
        {
            return null;
        }
        final boolean inEditMode = ( request.getMode() == RenderMode.EDIT );
        if ( inEditMode )
        {
            final ContentId contentId = ContentId.from( contentSelector.substring( 1 ) );
            final Content contentById = getContentById( contentId );
            if ( contentById != null )
            {
                return contentById;
            }
        }

        final ContentPath contentPath = ContentPath.from( contentSelector ).asAbsolute();
        return resolveContentByPath( contentPath );
    }

    private String getContentSelector( final PortalRequest request )
    {
        return request.getContentPath() != null ? request.getContentPath().toString().trim() : null;
    }

    private Content getContentById( final ContentId contentId )
    {
        try
        {
            return this.contentService.getById( contentId );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    private Content resolveContentByPath( final ContentPath contentPath )
    {
        ContentPath path = contentPath;
        Content content = null;
        while ( content == null && path != null )
        {
            content = getContentByPath( path );
            if ( content == null )
            {
                path = path.getParentPath();
            }
        }
        return content;
    }

    private Content getContentByPath( final ContentPath contentPath )
    {
        try
        {
            return this.contentService.getByPath( contentPath );
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
    }

    private Site getSite( final Content content )
    {
        return content != null ? this.contentService.getNearestSite( content.getId() ) : null;
    }

    private Map<ApplicationKey, Integer> getAppsOrder( final SiteConfigs siteConfigs )
    {
        final Map<ApplicationKey, Integer> appsOrder = new HashMap<>();
        for ( int i = 0; i < siteConfigs.getSize(); i++ )
        {
            appsOrder.put( siteConfigs.get( i ).getApplicationKey(), i );
        }
        return appsOrder;
    }

    private String normalizedQueryParams( final PortalRequest request )
    {
        if ( queryParams != null )
        {
            return queryParams;
        }

        final Multimap<String, String> params = request.getParams();
        if ( params.isEmpty() )
        {
            queryParams = "";
            return queryParams;
        }

        final List<String> paramList = new ArrayList<>( params.size() );
        params.keySet().stream().
            sorted().
            map( this::urlEscape ).
            forEach( ( key ) -> {
                for ( String value : params.get( key ) )
                {
                    paramList.add( key + "=" + urlEscape( value ) );
                }
            } );
        queryParams = paramList.stream().collect( Collectors.joining( "&", "?", "" ) );
        return queryParams;
    }

    private String urlEscape( final String value )
    {
        return UrlEscapers.urlFormParameterEscaper().escape( value );
    }

    private String getSiteRelativePath( final PortalRequest request )
    {
        final ContentPath sitePath = siteResolved.getPath();
        final ContentPath path = RenderMode.EDIT == request.getMode() ? contentResolved.getPath() : request.getContentPath();
        final String relativePath = path.toString().substring( sitePath.toString().length() );
        return relativePath.isEmpty() ? "/" : relativePath;
    }

    private boolean matchesUrlPattern( final ControllerMappingDescriptor descriptor, final PortalRequest request )
    {
        String siteRelativePath = getSiteRelativePath( request );
        final boolean patternWithQueryParameters = descriptor.getPattern().toString().contains( "\\?" );
        if ( patternWithQueryParameters )
        {
            siteRelativePath += normalizedQueryParams( request );
        }

        final boolean patternMatches = descriptor.getPattern().
            matcher( siteRelativePath ).
            matches();
        return descriptor.invertPattern() ? !patternMatches : patternMatches;
    }

    private boolean matchesContent( final ControllerMappingDescriptor descriptor, final PortalRequest request )
    {
        final String endpointPath = request.getEndpointPath();
        if ( endpointPath != null && !endpointPath.isEmpty() )
        {
            return false;
        }

        if ( descriptor.getContentConstraint() == null )
        {
            return true;
        }
        return descriptor.getContentConstraint().matches( contentResolved );
    }

}
