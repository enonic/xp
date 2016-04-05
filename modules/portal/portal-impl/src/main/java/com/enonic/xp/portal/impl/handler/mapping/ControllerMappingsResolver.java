package com.enonic.xp.portal.impl.handler.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    ControllerMappingsResolver( final SiteService siteService, final ContentService contentService )
    {
        this.siteService = siteService;
        this.contentService = contentService;
    }

    public ControllerMappingDescriptor resolve( final PortalRequest request )
    {
        if ( request.getMode() == RenderMode.ADMIN )
        {
            return null;
        }
        final Site site = getCurrentSite( request );
        if ( site == null )
        {
            return null;
        }

        final List<ControllerMappingDescriptor> descriptors = getMappingDescriptors( site );
        if ( descriptors.isEmpty() )
        {
            return null;
        }

        return getFirstMatchingDescriptor( request, site, descriptors );
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

    private Site getCurrentSite( final PortalRequest request )
    {
        if ( request.getContentPath() == null )
        {
            return null;
        }

        Content content = request.getContent();
        if ( content == null )
        {
            content = getContent( request );
            request.setContent( content );
        }

        Site site = request.getSite();
        if ( site == null )
        {
            site = getSite( content );
            request.setSite( site );
        }

        return site;
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
        return getContentByPath( contentPath );
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

    private boolean matchesUrlPattern( final ControllerMappingDescriptor descriptor, final PortalRequest request )
    {
        final boolean patternMatches = descriptor.getPattern().matcher( request.getPath() ).matches();
        return descriptor.invertPattern() ? !patternMatches : patternMatches;
    }

    private boolean matchesContent( final ControllerMappingDescriptor descriptor, final PortalRequest request )
    {
        if ( descriptor.getContentConstraint() == null )
        {
            return true;
        }
        return descriptor.getContentConstraint().matches( request.getContent() );
    }

}
