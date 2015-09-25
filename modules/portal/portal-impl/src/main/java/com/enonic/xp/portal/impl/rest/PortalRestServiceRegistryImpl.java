package com.enonic.xp.portal.impl.rest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.portal.rest.PortalRestService;

@Component
public final class PortalRestServiceRegistryImpl
    implements PortalRestServiceRegistry
{
    Map<String, PortalRestService> portalRestServiceMap = new ConcurrentHashMap<>();

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addPortalRestService( final PortalRestService portalRestService )
    {
        portalRestServiceMap.put( portalRestService.getName(), portalRestService );
    }

    public void removePortalRestService( final PortalRestService portalRestService )
    {
        portalRestServiceMap.remove( portalRestService.getName() );
    }


    @Override
    public PortalRestService getPortalRestService( final String portalRestServiceName )
    {
        return portalRestServiceMap.get( portalRestServiceName );
    }
}
