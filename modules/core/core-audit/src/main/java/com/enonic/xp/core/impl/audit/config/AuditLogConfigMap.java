package com.enonic.xp.core.impl.audit.config;

import java.util.Map;

import static com.google.common.base.Strings.nullToEmpty;

public class AuditLogConfigMap
{

    private static final String ENABLED_PROPERTY = "enabled";

    private static final String OUTPUT_LOGS_PROPERTY = "outputLogs";

    private static final boolean ENABLED_DEFAULT_VALUE = true;

    private static final boolean OUTPUT_LOGS_DEFAULT_VALUE = false;

    private final Map<String, String> map;

    public AuditLogConfigMap( final Map<String, String> map )
    {
        this.map = map;
    }

    public boolean isEnabled()
    {
        return nullToEmpty( map.get( ENABLED_PROPERTY ) ).isBlank() ? ENABLED_DEFAULT_VALUE : "true".equals( map.get( ENABLED_PROPERTY ) );
    }

    public boolean isOutputLogs()
    {
        return !nullToEmpty( map.get( OUTPUT_LOGS_PROPERTY ) ).isBlank()
            ? "true".equals( map.get( OUTPUT_LOGS_PROPERTY ) )
            : OUTPUT_LOGS_DEFAULT_VALUE;
    }

}
