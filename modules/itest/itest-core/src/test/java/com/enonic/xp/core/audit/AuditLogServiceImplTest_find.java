package com.enonic.xp.core.audit;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.AuditLogIds;
import com.enonic.xp.audit.FindAuditLogParams;
import com.enonic.xp.audit.FindAuditLogResult;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.core.impl.audit.AuditLogContext;
import com.enonic.xp.repository.RepositoryNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuditLogServiceImplTest_find
    extends AbstractAuditLogServiceTest
{
    @Test
    public void find_anonymous()
    {
        assertThrows(RepositoryNotFoundException.class, () -> {
            LogAuditLogParams params = LogAuditLogParams.create().type( "test" ).build();
            AuditLog log = auditLogService.log( params );

            FindAuditLogResult result = auditLogService.find( FindAuditLogParams.create().ids( AuditLogIds.from( log.getId() ) ).build() );
            assertEquals( 0L, result.getCount() );
        });

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
    public void find()
    {
        LogAuditLogParams params = LogAuditLogParams.create().type( "test" ).build();
        AuditLog log = logAsAdmin( params );
        FindAuditLogResult result = findAsAdmin( FindAuditLogParams.create().
            ids( AuditLogIds.from( log.getId() ) ).
            build() );
        assertEquals( 1L, result.getCount() );
        assertEquals( log, result.getHits().first() );
    }

    @Test
    public void find_none()
    {
        LogAuditLogParams params = LogAuditLogParams.create().type( "test" ).build();
        logAsAdmin( params );
        FindAuditLogResult result = findAsAdmin( FindAuditLogParams.create().build() );
        assertEquals( 1L, result.getCount() );
    }

    @Test
    public void find_from()
    {
        AuditLog log = logAsAdmin( LogAuditLogParams.create().
            type( "test" ).
            time( Instant.now().minus( 30, ChronoUnit.DAYS ) ).
            build() );

        FindAuditLogResult result = findAsAdmin( FindAuditLogParams.create().
            from( Instant.now() ).
            build() );
        assertEquals( 0L, result.getCount() );

        result = findAsAdmin( FindAuditLogParams.create().
            from( Instant.now().minus( 31, ChronoUnit.DAYS ) ).
            build() );
        assertEquals( 1L, result.getCount() );
        assertEquals( log, result.getHits().first() );
    }

    @Test
    public void find_to()
    {
        AuditLog log = logAsAdmin( LogAuditLogParams.create().
            type( "test" ).
            time( Instant.now() ).
            build() );

        FindAuditLogResult result = findAsAdmin( FindAuditLogParams.create().
            to( Instant.now().minus( 30, ChronoUnit.DAYS ) ).
            build() );
        assertEquals( 0L, result.getCount() );

        result = findAsAdmin( FindAuditLogParams.create().
            to( Instant.now() ).
            build() );
        assertEquals( 1L, result.getCount() );
        assertEquals( log, result.getHits().first() );
    }


    @Test
    public void find_type()
    {
        AuditLog log1 = logAsAdmin( LogAuditLogParams.create().
            type( "type1" ).
            build() );

        AuditLog log2 = logAsAdmin( LogAuditLogParams.create().
            type( "type2" ).
            build() );

        FindAuditLogResult result = findAsAdmin( FindAuditLogParams.create().
            type( "type1" ).
            build() );
        assertEquals( 1L, result.getCount() );
        assertEquals( log1, result.getHits().first() );

        result = findAsAdmin( FindAuditLogParams.create().
            type( "type2" ).
            build() );
        assertEquals( 1L, result.getCount() );
        assertEquals( log2, result.getHits().first() );
    }

    @Test
    public void find_source()
    {
        AuditLog log1 = logAsAdmin( LogAuditLogParams.create().
            type( "test" ).
            source( "source1" ).
            build() );

        AuditLog log2 = logAsAdmin( LogAuditLogParams.create().
            type( "test" ).
            source( "source2" ).
            build() );

        FindAuditLogResult result = findAsAdmin( FindAuditLogParams.create().
            source( "source1" ).
            build() );
        assertEquals( 1L, result.getCount() );
        assertEquals( log1, result.getHits().first() );

        result = findAsAdmin( FindAuditLogParams.create().
            source( "source2" ).
            build() );
        assertEquals( 1L, result.getCount() );
        assertEquals( log2, result.getHits().first() );
    }
}
