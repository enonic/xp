package com.enonic.xp.portal.impl.processor;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.processor.ResponseProcessorDescriptor;
import com.enonic.xp.site.processor.ResponseProcessorDescriptors;

@Component(service = ProcessorChainResolver.class)
public class ProcessorChainResolver
{
    private SiteService siteService;

    public ProcessorChainResolver()
    {
    }

    public ResponseProcessorDescriptors resolve( PortalRequest request )
    {
        SiteDescriptor siteDescriptor;
        List<ResponseProcessorDescriptor> filterChain = new ArrayList<>();

        if ( request.getSite() != null )
        {
            for ( SiteConfig siteConfig : new SiteConfigsDataSerializer().fromProperties( request.getSite().getData().getRoot() ).build() )
            {
                siteDescriptor = siteService.getDescriptor( siteConfig.getApplicationKey() );
                if ( siteDescriptor != null )
                {
                    for ( ResponseProcessorDescriptor filterDescriptor : siteDescriptor.getResponseProcessors() )
                    {
                        filterChain.add( this.findIndexToInsert( filterDescriptor, filterChain ), filterDescriptor );
                    }
                }
            }
        }

        return ResponseProcessorDescriptors.from( filterChain );
    }

    private int findIndexToInsert( ResponseProcessorDescriptor fd, List<ResponseProcessorDescriptor> list )
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
