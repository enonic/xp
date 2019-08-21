package com.enonic.xp.audit;

import org.junit.Test;

import static com.enonic.xp.audit.AuditLogTestBuilder.getBuilder;
import static org.junit.Assert.*;

public class AuditLogTest
{
    @Test(expected = NullPointerException.class)
    public void create_no_params()
    {
        AuditLog.create().build();
    }

    @Test
    public void create()
    {
        AuditLog log = getBuilder().build();

        assertEquals( log.getId(), AuditLogTestBuilder.id );
        assertEquals( log.getType(), AuditLogTestBuilder.type );
        assertEquals( log.getTime(), AuditLogTestBuilder.time );
        assertEquals( log.getSource(), AuditLogTestBuilder.source );
        assertEquals( log.getUser(), AuditLogTestBuilder.user );
        assertEquals( log.getMessage(), AuditLogTestBuilder.message );
        assertEquals( log.getObjectUris(), AuditLogTestBuilder.objectUris );
        assertEquals( log.getData(), AuditLogTestBuilder.data );
    }

    @Test
    public void equals()
    {
        AuditLog log1 = getBuilder().build();
        AuditLog log2 = getBuilder().build();

        assertEquals( log1.hashCode(), log2.hashCode() );
        assertTrue( log1.equals( log2 ) );
    }

    @Test
    public void equals_rainy()
    {
        AuditLog log1 = getBuilder().build();
        AuditLog log2 = getBuilder().id( AuditLogId.from( 234 ) ).build();

        assertNotEquals( log1.hashCode(), log2.hashCode() );
        assertFalse( log1.equals( log2 ) );
    }


}