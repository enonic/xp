package com.enonic.xp.web.vhost.impl.config;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostService;

@Component(immediate = true, configurationPid = "com.enonic.xp.web.vhost")
public final class VirtualHostServiceImpl
    implements VirtualHostService
{
    private static final Logger LOG = LoggerFactory.getLogger( VirtualHostServiceImpl.class );

    private final boolean enabled;

    private final List<VirtualHost> virtualHosts;

    @Activate
    public VirtualHostServiceImpl(final Map<String, String> config)
    {
        final VirtualHostConfigMap configMap = new VirtualHostConfigMap( config );
        this.enabled = configMap.isEnabled();
        this.virtualHosts = configMap.buildMappings();
        if ( this.enabled )
        {
            LOG.info( "Virtual host is enabled and mappings updated." );
        }
    }

    @Override
    public boolean isEnabled()
    {
        return this.enabled;
    }

    @Override
    public List<VirtualHost> getVirtualHosts()
    {
        return this.virtualHosts;
    }
}
