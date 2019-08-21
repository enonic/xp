package com.enonic.xp.core.impl.auditlog;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.auditlog.AuditLogParams;

import static org.junit.Assert.*;

public class AuditLogServiceImplDisabledTest
{
    private AuditLogServiceImpl auditLogService;

    @Before
    public void setUp()
        throws Exception
    {
        AuditLogConfig config = Mockito.mock( AuditLogConfig.class );
        Mockito.when( config.enabled() ).thenReturn( false );
        Mockito.when( config.outputLogs() ).thenReturn( true );

        auditLogService = new AuditLogServiceImpl();
        auditLogService.setConfig( config );
        auditLogService.initialize( config );
    }

    @Test
    public void log_when_disabled()
    {
        AuditLog log = auditLogService.log( AuditLogParams.create().type( "test" ).build() );
        assertNotNull( log.getId() );
        assertNotNull( log.getType() );
        assertNotNull( log.getTime() );
        assertNotNull( log.getSource() );
        assertNotNull( log.getUser() );
        assertNotNull( log.getMessage() );
        assertNotNull( log.getObjectUris() );
        assertNotNull( log.getData() );
    }
}