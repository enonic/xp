package com.enonic.xp.core.impl.audit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.repository.RepositoryService;

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
        IndexService indexService = Mockito.mock( IndexService.class );
        Mockito.when( indexService.isMaster() ).thenReturn( true );
        RepositoryService repositoryService = Mockito.mock( RepositoryService.class );

        auditLogService = new AuditLogServiceImpl();
        auditLogService.setIndexService( indexService );
        auditLogService.setRepositoryService( repositoryService );
        auditLogService.initialize( config );
    }

    @Test
    public void log_when_disabled()
    {
        AuditLog log = auditLogService.log( LogAuditLogParams.create().type( "test" ).build() );
        assertNull( log );
    }
}