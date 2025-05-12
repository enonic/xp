package com.enonic.xp.audit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuditLogIdsTest
{
    @Test
    public void double_entry()
    {
        AuditLogIds ids = AuditLogIds.from( AuditLogId.from( "some1" ), AuditLogId.from( "some2" ) );
        assertEquals( 2, ids.getSize() );
    }

    @Test
    public void duplicate_entry()
    {
        AuditLogIds ids = AuditLogIds.from( "some", "some" );
        assertEquals( 1, ids.getSize() );
    }

    @Test
    public void from_builder()
    {
        AuditLogIds ids = AuditLogIds.create().
            add( new AuditLogId() ).
            addAll( AuditLogIds.from( "a", "b" ) ).
            build();

        assertEquals( 3, ids.getSize() );
    }

    @Test
    public void empty()
    {
        AuditLogIds ids = AuditLogIds.empty();
        assertEquals( 0, ids.getSize() );
    }
}
