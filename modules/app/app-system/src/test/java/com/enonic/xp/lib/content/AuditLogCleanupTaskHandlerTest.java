package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.CleanUpAuditLogParams;
import com.enonic.xp.testing.ScriptTestSupport;

@ExtendWith(MockitoExtension.class)
public class AuditLogCleanupTaskHandlerTest
    extends ScriptTestSupport
{
    @Captor
    private ArgumentCaptor<CleanUpAuditLogParams> paramsCaptor;

    @Mock
    private AuditLogService auditLogService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        addService( AuditLogService.class, this.auditLogService );
    }

    @Test
    public void cleanUp()
        throws Exception
    {
        runFunction( "/test/AuditLogCleanupTaskHandlerTest.js", "cleanUp" );
        Mockito.verify( auditLogService, Mockito.times( 1 ) ).cleanUp( paramsCaptor.capture() );

        Assertions.assertEquals( "PT2s", paramsCaptor.getValue().getAgeThreshold() );
    }
}
