package com.enonic.xp.core.impl.audit;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.audit.config.AuditLogConfigImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuditLogConfigImplTest
{
    @Test
    void configDefault()
    {
        final AuditLogConfigImpl auditLogConfig = new AuditLogConfigImpl();

        final Map<String, String> configMap = Map.of();
        auditLogConfig.configure( configMap );

        assertTrue( auditLogConfig.isEnabled() );
        assertFalse( auditLogConfig.isOutputLogs() );
        assertTrue( auditLogConfig.ageThreshold().isBlank() );
    }

    @Test
    void configCustom()
    {
        final AuditLogConfigImpl auditLogConfig = new AuditLogConfigImpl();

        final Map<String, String> configMap = Map.of( "enabled", "false", "outputLogs", "true", "ageThreshold", "PT1s" );
        auditLogConfig.configure( configMap );

        assertFalse( auditLogConfig.isEnabled() );
        assertTrue( auditLogConfig.isOutputLogs() );
        assertEquals( "PT1s", auditLogConfig.ageThreshold() );
    }

    @Test
    void configInvalid()
    {
        final AuditLogConfigImpl auditLogConfig = new AuditLogConfigImpl();

        final Map<String, String> configMap = Map.of( "enabled", "invalid", "outputLogs", "invalid", "ageThreshold", "invalid" );
        auditLogConfig.configure( configMap );

        assertFalse( auditLogConfig.isEnabled() );
        assertFalse( auditLogConfig.isOutputLogs() );
        assertEquals( "invalid", auditLogConfig.ageThreshold() );
    }
}
