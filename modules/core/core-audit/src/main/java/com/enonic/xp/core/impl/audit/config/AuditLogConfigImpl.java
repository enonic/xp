package com.enonic.xp.core.impl.audit.config;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, configurationPid = "com.enonic.xp.audit")
public class AuditLogConfigImpl
    implements AuditLogConfig
{
    private static final Logger LOG = LoggerFactory.getLogger( AuditLogConfigImpl.class );

    private boolean enabled;

    private boolean outputLogs;

    private String ageThreshold;

    @Activate
    public void configure( final Map<String, String> config )
    {
        final AuditLogConfigMap configMap = new AuditLogConfigMap( config );

        this.enabled = configMap.isEnabled();
        this.outputLogs = configMap.isOutputLogs();
        this.ageThreshold = configMap.ageThreshold();

        if ( this.enabled )
        {
            LOG.info( "Audit log is enabled and mappings updated." );
        }
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public boolean isOutputLogs()
    {
        return outputLogs;
    }

    @Override
    public String ageThreshold()
    {
        return ageThreshold;
    }

}
