package com.enonic.xp.web.vhost.impl.config;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.google.common.collect.ImmutableList;

@Component(immediate = true, configurationPid = "com.enonic.xp.web.vhost")
public final class VirtualHostConfigImpl
    implements VirtualHostConfig
{
    private boolean enabled;

    private boolean requireMapping;

    private ImmutableList<VirtualHostMapping> mappings;

    public VirtualHostConfigImpl()
    {
        this.enabled = false;
        this.requireMapping = false;
        this.mappings = ImmutableList.of();
    }

    @Override
    public boolean isEnabled()
    {
        return this.enabled;
    }

    @Override
    public boolean isRequireMapping()
    {
        return this.requireMapping;
    }

    @Override
    public List<VirtualHostMapping> getMappings()
    {
        return this.mappings;
    }

    @Activate
    public void configure( final Map<String, String> config )
    {
        final VirtualHostConfigMap configMap = new VirtualHostConfigMap( config );
        this.enabled = configMap.isEnabled();
        this.requireMapping = configMap.isRequireMapping();
        this.mappings = configMap.buildMappings();
    }
}
