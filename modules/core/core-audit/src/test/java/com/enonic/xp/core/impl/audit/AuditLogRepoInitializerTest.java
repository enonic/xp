package com.enonic.xp.core.impl.audit;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.core.impl.audit.config.AuditLogConfig;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.internal.InternalRepositoryService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuditLogRepoInitializerTest
{
    AuditLogServiceImpl auditLogService;


    @Test
    void do_initialize()
    {
        InternalRepositoryService repositoryService = Mockito.mock( InternalRepositoryService.class );
        IndexService indexService = Mockito.mock( IndexService.class );

        AuditLogConfig config = Mockito.mock( AuditLogConfig.class );
        Mockito.when( config.isEnabled() ).thenReturn( true );
        Mockito.when( config.isOutputLogs() ).thenReturn( true );

        auditLogService = new AuditLogServiceImpl( config, null );

        Mockito.when( indexService.isMaster() ).thenReturn( true );
        Mockito.when( indexService.waitForYellowStatus() ).thenReturn( true );
        Mockito.when( repositoryService.isInitialized( Mockito.any( RepositoryId.class ) ) ).thenReturn( false );

        AuditLogRepoInitializer.create().setIndexService( indexService ).setRepositoryService( repositoryService ).build().initialize();
        assertNotNull( auditLogService.getConfig() );
    }
}
