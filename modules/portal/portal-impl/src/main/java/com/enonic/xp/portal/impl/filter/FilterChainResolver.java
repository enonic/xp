package com.enonic.xp.portal.impl.filter;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.filter.FilterDescriptor;

public class FilterChainResolver
{
    private SiteService siteService;

    public List<FilterDescriptor> resolve( final PortalRequest request )
    {
        SiteDescriptor siteDescriptor;
        List<FilterDescriptor> filterChain = new ArrayList<>();

        for ( SiteConfig siteConfig : request.getSite().getSiteConfigs() )
        {
            siteDescriptor = siteService.getDescriptor( siteConfig.getApplicationKey() );
            if ( siteDescriptor != null )
            {
                for ( FilterDescriptor filterDescriptor : siteDescriptor.getFilterDescriptors() )
                {
                    filterChain.add( this.findIndexToInsert( filterDescriptor, filterChain ), filterDescriptor );
                }
            }
        }

        return filterChain;
    }

    private int findIndexToInsert( FilterDescriptor fd, List<FilterDescriptor> list )
    {
        int index = 0;
        // if list is empty or first item's order is bigger than the new ones, add to 0 index
        if ( !list.isEmpty() && list.get( 0 ).getOrder() <= fd.getOrder() )
        {
            // if last item's order is smaller then the new ones, it goes to the end of list
            if ( list.get( list.size() - 1 ).getOrder() <= fd.getOrder() )
            {
                index = list.size();
            }
            else
            {
                // otherwise it goes as last item of siblings with the same order
                for ( int i = 0; i < list.size(); i++ )
                {
                    if ( list.get( i ).getOrder() > fd.getOrder() )
                    {
                        index = i;
                        break;
                    }
                }
            }
        }
        return index;
    }

    @Reference
    public void setSiteService( final SiteService siteService )
    {
        this.siteService = siteService;
    }
}
