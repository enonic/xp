package com.enonic.xp.repo.impl.repository;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.DeleteBranchParams;
import com.enonic.xp.repository.DeleteRepositoryParams;
import com.enonic.xp.repository.RepositoryId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RepositoryAuditLogSupportImplTest
{
    private RepositoryAuditLogSupportImpl instance;

    private AuditLogService auditLogService;

    @BeforeEach
    void setUp()
    {
        this.auditLogService = mock( AuditLogService.class );

        RepositoryConfig config = mock( RepositoryConfig.class, invocation -> invocation.getMethod().getDefaultValue() );

        this.instance = new RepositoryAuditLogSupportImpl( auditLogService );
        this.instance.activate( config );
    }

    @Test
    void testCreateRepository()
    {
        instance.createRepository( CreateRepositoryParams.create()
                                       .repositoryId( RepositoryId.from( "test-repo" ) )
                                       .build() );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService ).log( argumentCaptor.capture() );
        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.repo.create", value.getType() );
        assertEquals( "test-repo", value.getObjectUris().stream().map( Objects::toString ).findFirst().orElseThrow() );
        assertEquals( "test-repo", value.getData().getSet( "params" ).getString( "id" ) );
    }

    @Test
    void testDeleteRepository()
    {
        instance.deleteRepository( DeleteRepositoryParams.from( "test-repo" ) );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService ).log( argumentCaptor.capture() );
        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.repo.delete", value.getType() );
        assertEquals( "test-repo", value.getObjectUris().stream().map( Objects::toString ).findFirst().orElseThrow() );
        assertEquals( "test-repo", value.getData().getSet( "params" ).getString( "id" ) );
    }

    @Test
    void testCreateBranch()
    {
        ContextBuilder.create().repositoryId( RepositoryId.from( "test-repo" ) ).build().runWith( () -> {
            instance.createBranch( CreateBranchParams.from( "test-branch" ) );
        } );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService ).log( argumentCaptor.capture() );
        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.repo.branch.create", value.getType() );
        assertEquals( "test-repo", value.getObjectUris().stream().map( Objects::toString ).findFirst().orElseThrow() );
        assertEquals( "test-repo", value.getData().getSet( "params" ).getString( "repositoryId" ) );
        assertEquals( "test-branch", value.getData().getSet( "params" ).getString( "branch" ) );
    }

    @Test
    void testDeleteBranch()
    {
        ContextBuilder.create().repositoryId( RepositoryId.from( "test-repo" ) ).build().runWith( () -> {
            instance.deleteBranch( DeleteBranchParams.from( "test-branch" ) );
        } );

        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        verify( auditLogService ).log( argumentCaptor.capture() );
        final LogAuditLogParams value = argumentCaptor.getValue();
        assertEquals( "system.repo.branch.delete", value.getType() );
        assertEquals( "test-repo", value.getObjectUris().stream().map( Objects::toString ).findFirst().orElseThrow() );
        assertEquals( "test-repo", value.getData().getSet( "params" ).getString( "repositoryId" ) );
        assertEquals( "test-branch", value.getData().getSet( "params" ).getString( "branch" ) );
    }
}
