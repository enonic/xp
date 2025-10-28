package com.enonic.xp.audit;

import org.junit.jupiter.api.Test;

import static com.enonic.xp.audit.AuditLogTestBuilder.getBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FindAuditLogResultTest
{

    @Test
    void no_hits_provided()
    {
        assertThrows( NullPointerException.class, () -> {
            FindAuditLogResult.create().build();
        } );
    }

    @Test
    void empty_hits()
    {
        FindAuditLogResult result = FindAuditLogResult.create().
            hits( AuditLogs.empty() ).
            total( 0L ).
            build();
        assertEquals( AuditLogs.empty(), result.getHits() );
    }

    @Test
    void some_hits()
    {
        FindAuditLogResult result = FindAuditLogResult.create().
            hits( AuditLogs.from( getBuilder().id( AuditLogId.from( 1 ) ).build(), getBuilder().id( AuditLogId.from( 2 ) ).build() ) ).
            total( 2L ).
            build();
        assertEquals( 2, result.getHits().getSize() );
    }

}
