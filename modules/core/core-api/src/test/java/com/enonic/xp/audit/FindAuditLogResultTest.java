package com.enonic.xp.audit;

import org.junit.Test;

import static com.enonic.xp.audit.AuditLogTestBuilder.getBuilder;
import static org.junit.Assert.*;

public class FindAuditLogResultTest
{

    @Test(expected = NullPointerException.class)
    public void no_hits_provided()
    {
        FindAuditLogResult.create().build();
    }

    @Test
    public void empty_hits()
    {
        FindAuditLogResult result = FindAuditLogResult.create().
            hits( AuditLogs.empty() ).
            total( 0L ).
            build();
        assertEquals( 0, result.getCount() );
        assertEquals( AuditLogs.empty(), result.getHits() );
    }

    @Test
    public void some_hits()
    {
        FindAuditLogResult result = FindAuditLogResult.create().
            hits( AuditLogs.from( getBuilder().id( AuditLogId.from( 1 ) ).build(), getBuilder().id( AuditLogId.from( 2 ) ).build() ) ).
            total( 2L ).
            build();
        assertEquals( 2, result.getCount() );
    }

}