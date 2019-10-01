package com.enonic.xp.audit;

import java.util.Arrays;

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
    public void from_list_and_string_conversion()
    {
        AuditLogIds ids = AuditLogIds.from( Arrays.asList( new AuditLogId(), AuditLogId.from( "some" ), AuditLogId.from( 1 ) ) );
        assertEquals( 3, ids.getSize() );
        assertEquals( ids, AuditLogIds.from( ids.asStrings() ) );
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
