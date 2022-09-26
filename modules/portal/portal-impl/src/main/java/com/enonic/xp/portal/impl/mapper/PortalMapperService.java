package com.enonic.xp.portal.impl.mapper;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import com.enonic.xp.portal.impl.PortalConfig;

@Component(immediate = true)
public class PortalMapperService
{
    @Activate
    @Modified
    public void activate( final PortalConfig config )
    {
        PortalRequestMapper.setLowercaseHeaders( config.lowercaseRequestHeaders() );
        PortalResponseMapper.setLowercaseHeaders( config.lowercaseResponseHeaders() );
    }
}
