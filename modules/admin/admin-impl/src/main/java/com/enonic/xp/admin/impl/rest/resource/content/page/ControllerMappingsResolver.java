package com.enonic.xp.admin.impl.rest.resource.content.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.enonic.xp.content.Content;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.mapping.ContentMappingConstraint;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;

final class ControllerMappingsResolver
{
    private final SiteService siteService;

    ControllerMappingsResolver( final SiteService siteService )
    {
        this.siteService = siteService;
    }

    boolean canRender( final Content content, Site nearestSite )
    {
        final List<ControllerMappingDescriptor> descriptors = getMappingDescriptors( nearestSite );
        return !descriptors.isEmpty() && anyMatchingDescriptor( content, descriptors );
    }

    private boolean anyMatchingDescriptor( final Content content, final List<ControllerMappingDescriptor> descriptors )
    {
        return descriptors.stream().anyMatch( ( d ) -> matchesContent( d, content ) );
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

    private boolean matchesContent( final ControllerMappingDescriptor descriptor, final Content content )
    {
        final ContentMappingConstraint constraint = descriptor.getContentConstraint();
        return constraint != null && constraint.matches( content );
    }

}
