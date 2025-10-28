package com.enonic.xp.core.impl.audit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.core.impl.audit.config.AuditLogConfig;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.repository.RepositoryService;

import static org.junit.jupiter.api.Assertions.assertNull;

class AuditLogServiceImplDisabledTest
{
    private AuditLogServiceImpl auditLogService;

    @BeforeEach
    void setUp()
    {
        AuditLogConfig config = Mockito.mock( AuditLogConfig.class );
        Mockito.when( config.isEnabled() ).thenReturn( false );
        Mockito.when( config.isOutputLogs() ).thenReturn( true );
        IndexService indexService = Mockito.mock( IndexService.class );
        Mockito.when( indexService.waitForYellowStatus() ).thenReturn( true );
        Mockito.when( indexService.isMaster() ).thenReturn( true );
        RepositoryService repositoryService = Mockito.mock( RepositoryService.class );

        auditLogService = new AuditLogServiceImpl( config, null );
        AuditLogRepoInitializer.create().
            setIndexService( indexService ).
            setRepositoryService( repositoryService ).
            build().
            initialize();
    }

    @Test
    void log_when_disabled()
    {
        AuditLog log = auditLogService.log( LogAuditLogParams.create().type( "test" ).build() );
        assertNull( log );
    }
}
