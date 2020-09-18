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

    private boolean enabled;

    private List<VirtualHost> virtualHosts;

    public VirtualHostServiceImpl()
    {
        this.enabled = false;
        this.virtualHosts = List.of();
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

    @Activate
    public void configure( final Map<String, String> config )
    {
        final VirtualHostConfigMap configMap = new VirtualHostConfigMap( config );
        this.enabled = configMap.isEnabled();
        this.virtualHosts = List.copyOf( configMap.buildMappings() );

        if ( this.enabled )
        {
            LOG.info( "Virtual host is enabled and mappings updated." );
        }
    }
}
