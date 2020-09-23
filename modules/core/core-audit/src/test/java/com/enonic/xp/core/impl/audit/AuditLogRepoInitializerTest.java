package com.enonic.xp.core.impl.audit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.core.impl.audit.config.AuditLogConfig;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuditLogRepoInitializerTest
{
    AuditLogServiceImpl auditLogService;

    @BeforeEach
    public void setUp()
    {
        RepositoryService repositoryService = Mockito.mock( RepositoryService.class );
        IndexService indexService = Mockito.mock( IndexService.class );

        AuditLogConfig config = Mockito.mock( AuditLogConfig.class );
        Mockito.when( config.isEnabled() ).thenReturn( true );
        Mockito.when( config.isOutputLogs() ).thenReturn( true );

        auditLogService = new AuditLogServiceImpl( config, indexService, repositoryService, null );

        Mockito.when( indexService.isMaster() ).thenReturn( true );
        Mockito.when( indexService.waitForYellowStatus() ).thenReturn( true );
        Mockito.when( repositoryService.createRepository( Mockito.any( CreateRepositoryParams.class ) ) ).thenReturn( null );
        Mockito.when( repositoryService.isInitialized( Mockito.any( RepositoryId.class ) ) ).thenReturn( false );
    }

    @Test
    public void do_initialize()
    {
        auditLogService.initialize();
        assertNotNull( auditLogService.getConfig() );
    }
}
