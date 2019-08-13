package com.enonic.xp.core.impl.auditlog;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import com.enonic.xp.index.IndexService;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;

import static org.junit.Assert.*;

public class AuditLogRepoInitializerTest
{

    AuditLogServiceImpl auditLogService;

    @Before
    public void setUp()
        throws Exception
    {
        auditLogService = new AuditLogServiceImpl();

        RepositoryService repositoryService = Mockito.mock( RepositoryService.class );
        auditLogService.setRepositoryService( repositoryService );

        IndexService indexService = Mockito.mock( IndexService.class );
        auditLogService.setIndexService( indexService );

        Mockito.when( indexService.isMaster() ).thenReturn( true );
        Mockito.when( repositoryService.createRepository( Mockito.any( CreateRepositoryParams.class ) ) ).thenReturn( null );
        Mockito.when( repositoryService.isInitialized( Mockito.any( RepositoryId.class ) ) ).thenAnswer( initializationAnswer() );
    }

    @Test
    public void do_initialize()
    {
        AuditLogConfig config = Mockito.mock( AuditLogConfig.class );
        Mockito.when( config.enabled() ).thenReturn( true );
        Mockito.when( config.outputLogs() ).thenReturn( true );

        auditLogService.initialize( config );
        assertNotNull( auditLogService.getConfig() );
    }

    private static Answer<Boolean> initializationAnswer()
    {
        final AtomicBoolean calledOnce = new AtomicBoolean( false );
        return invocation -> {
            boolean c = calledOnce.get();
            if ( c )
            {
                return true;
            }
            calledOnce.set( true );
            return false;
        };
    }
}