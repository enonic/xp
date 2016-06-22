package com.enonic.xp.web.vhost.impl.config;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMappings;

@Component(immediate = true, configurationPid = "com.enonic.xp.web.vhost")
public final class VirtualHostConfigImpl
    implements VirtualHostConfig
{
    private final static Logger LOG = LoggerFactory.getLogger( VirtualHostConfigImpl.class );

    private VirtualHostMappings mappings;

    public VirtualHostConfigImpl()
    {
        this.mappings = new VirtualHostMappings();
    }

    @Override
    public VirtualHostMappings getMappings()
    {
        return this.mappings;
    }

    @Activate
    public void configure( final Map<String, String> config )
    {
        final VirtualHostConfigMap configMap = new VirtualHostConfigMap( config );
        final boolean enabled = configMap.isEnabled();

        if ( enabled )
        {
            LOG.info( "Virtual host is enabled and mappings updated." );
            this.mappings = configMap.buildMappings();
        }
        else
        {
            LOG.info( "Virtual host is disabled." );
            this.mappings = new VirtualHostMappings();
        }
    }
}
