package com.enonic.xp.audit;

import org.junit.jupiter.api.Test;

import static com.enonic.xp.audit.AuditLogTestBuilder.getBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FindAuditLogResultTest
{

    @Test
    public void no_hits_provided()
    {
        assertThrows( NullPointerException.class, () -> {
            FindAuditLogResult.create().build();
        } );
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
