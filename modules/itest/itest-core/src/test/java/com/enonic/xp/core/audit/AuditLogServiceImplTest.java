package com.enonic.xp.core.audit;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.AuditLogIds;
import com.enonic.xp.audit.CleanUpAuditLogParams;
import com.enonic.xp.audit.FindAuditLogParams;
import com.enonic.xp.audit.FindAuditLogResult;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.impl.audit.AuditLogConstants;
import com.enonic.xp.core.impl.audit.AuditLogContext;
import com.enonic.xp.core.impl.audit.AuditLogRepoInitializer;
import com.enonic.xp.core.impl.audit.AuditLogServiceImpl;
import com.enonic.xp.core.impl.audit.config.AuditLogConfig;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuditLogServiceImplTest
    extends AbstractNodeTest
{
    AuditLogServiceImpl auditLogService;

    @BeforeEach
    void setUp()
    {
        AuditLogConfig config = Mockito.mock( AuditLogConfig.class );
        Mockito.when( config.isEnabled() ).thenReturn( true );
        Mockito.when( config.isOutputLogs() ).thenReturn( true );

        this.auditLogService = new AuditLogServiceImpl( config, this.nodeService );

        AuditLogRepoInitializer.create().setIndexService( indexService ).setRepositoryService( repositoryService ).build().initialize();
        AuditLogContext.createAdminContext()
            .runWith( () -> auditLogService.cleanUp( CleanUpAuditLogParams.create().ageThreshold( "-P1D" ).build() ) );
        refresh();
    }

    @Test
    void find_anonymous()
    {
        final Context context = ContextBuilder.create()
            .repositoryId( AuditLogConstants.AUDIT_LOG_REPO_ID )
            .branch( AuditLogConstants.AUDIT_LOG_BRANCH )
            .authInfo( AuthenticationInfo.create().principals( PrincipalKey.ofAnonymous() ).user( User.anonymous() ).build() )
            .build();

        AuditLogContext.createAdminContext().runWith( () -> {
            LogAuditLogParams params = LogAuditLogParams.create().type( "test" ).build();
            AuditLog log = auditLogService.log( params );
            refresh();

            context.runWith( () -> {
                FindAuditLogResult result =
                    auditLogService.find( FindAuditLogParams.create().ids( AuditLogIds.from( log.getId() ) ).build() );
                assertEquals( 0L, result.getHits().getSize() );
            } );
        } );
    }

    private FindAuditLogResult findAsAdmin( FindAuditLogParams params )
    {
        return AuditLogContext.createAdminContext().callWith( () -> auditLogService.find( params ) );
    }

    private AuditLog logAsAdmin( LogAuditLogParams params )
    {
        return AuditLogContext.createAdminContext().callWith( () -> auditLogService.log( params ) );
    }

    @Test
    void find()
    {
        LogAuditLogParams params = LogAuditLogParams.create().type( "test" ).build();
        AuditLog log = logAsAdmin( params );
        refresh();
        FindAuditLogResult result = findAsAdmin( FindAuditLogParams.create().ids( AuditLogIds.from( log.getId() ) ).build() );
        assertEquals( 1L, result.getHits().getSize() );
        assertEquals( log, result.getHits().first() );
    }

    @Test
    void find_all()
    {
        LogAuditLogParams params = LogAuditLogParams.create().type( "test" ).build();
        logAsAdmin( params );
        refresh();
        FindAuditLogResult result = findAsAdmin( FindAuditLogParams.create().build() );
        assertEquals( 1L, result.getHits().getSize() );
    }

    @Test
    void find_from()
    {
        AuditLog log = logAsAdmin( LogAuditLogParams.create().type( "test" ).time( Instant.now().minus( 30, ChronoUnit.DAYS ) ).build() );
        refresh();

        FindAuditLogResult result = findAsAdmin( FindAuditLogParams.create().from( Instant.now() ).build() );
        assertEquals( 0L, result.getHits().getSize() );

        result = findAsAdmin( FindAuditLogParams.create().from( Instant.now().minus( 31, ChronoUnit.DAYS ) ).build() );
        assertEquals( 1L, result.getHits().getSize() );
        assertEquals( log, result.getHits().first() );
    }

    @Test
    void find_to()
    {
        AuditLog log = logAsAdmin( LogAuditLogParams.create().type( "test" ).time( Instant.now() ).build() );
        refresh();

        FindAuditLogResult result = findAsAdmin( FindAuditLogParams.create().to( Instant.now().minus( 30, ChronoUnit.DAYS ) ).build() );
        assertEquals( 0L, result.getHits().getSize() );

        result = findAsAdmin( FindAuditLogParams.create().to( Instant.now() ).build() );
        assertEquals( 1L, result.getHits().getSize() );
        assertEquals( log, result.getHits().first() );
    }


    @Test
    void find_type()
    {
        AuditLog log1 = logAsAdmin( LogAuditLogParams.create().type( "type1" ).build() );

        AuditLog log2 = logAsAdmin( LogAuditLogParams.create().type( "type2" ).build() );
        refresh();

        FindAuditLogResult result = findAsAdmin( FindAuditLogParams.create().type( "type1" ).build() );
        assertEquals( 1L, result.getHits().getSize() );
        assertEquals( log1, result.getHits().first() );

        result = findAsAdmin( FindAuditLogParams.create().type( "type2" ).build() );
        assertEquals( 1L, result.getHits().getSize() );
        assertEquals( log2, result.getHits().first() );
    }

    @Test
    void find_source()
    {
        AuditLog log1 = logAsAdmin( LogAuditLogParams.create().type( "test" ).source( "source1" ).build() );

        AuditLog log2 = logAsAdmin( LogAuditLogParams.create().type( "test" ).source( "source2" ).build() );
        refresh();

        FindAuditLogResult result = findAsAdmin( FindAuditLogParams.create().source( "source1" ).build() );
        assertEquals( 1L, result.getHits().getSize() );
        assertEquals( log1, result.getHits().first() );

        result = findAsAdmin( FindAuditLogParams.create().source( "source2" ).build() );
        assertEquals( 1L, result.getHits().getSize() );
        assertEquals( log2, result.getHits().first() );
    }

    @Test
    void log_anonymous()
    {
        final Context context = ContextBuilder.create()
            .repositoryId( AuditLogConstants.AUDIT_LOG_REPO_ID )
            .branch( AuditLogConstants.AUDIT_LOG_BRANCH )
            .authInfo( AuthenticationInfo.create().principals( PrincipalKey.ofAnonymous() ).user( User.anonymous() ).build() )
            .build();

        context.runWith( () -> {
            LogAuditLogParams params = LogAuditLogParams.create().type( "test" ).build();

            assertNull( auditLogService.log( params ) );
        } );
    }

    @Test
    void get()
    {
        final LogAuditLogParams params = LogAuditLogParams.create().type( "test" ).build();

        final AuditLog log1 = AuditLogContext.createAdminContext().callWith( () -> auditLogService.log( params ) );
        final AuditLog log2 = AuditLogContext.createAdminContext().callWith( () -> auditLogService.get( log1.getId() ) );

        assertEquals( log1, log2 );
    }
}
