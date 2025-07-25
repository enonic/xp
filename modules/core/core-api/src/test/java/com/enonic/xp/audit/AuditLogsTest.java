package com.enonic.xp.audit;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static com.enonic.xp.audit.AuditLogTestBuilder.getBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuditLogsTest
{
    @Test
    public void empty()
    {
        AuditLogs logs = AuditLogs.empty();
        assertEquals( 0, logs.getSize() );
    }

    @Test
    public void from()
    {
        AuditLogs logs1 = AuditLogs.from( getBuilder().build() );
        assertEquals( 1, logs1.getSize() );

        AuditLogs logs2 = AuditLogs.from( Arrays.asList( getBuilder().build() ) );
        assertEquals( 1, logs2.getSize() );
    }

    @Test
    public void builder()
    {
        AuditLogs.Builder builder = AuditLogs.create();
        builder.add( getBuilder().id( AuditLogId.from( 1 ) ).build() );
        builder.addAll( AuditLogs.from( getBuilder().id( AuditLogId.from( 2 ) ).build() ) );

        AuditLogs logs = builder.build();
        assertEquals( 2, logs.getSize() );
    }

    @Test
    public void getById()
    {
        AuditLogs.Builder builder = AuditLogs.create();

        for ( int i = 0; i < 10; i++ )
        {
            builder.add( getBuilder().id( AuditLogId.from( i ) ).build() );
        }

        AuditLogs logs = builder.build();
        assertEquals( 10, logs.getSize() );

        assertEquals( getBuilder().id( AuditLogId.from( 5 ) ).build(), logs.getAuditLogById( AuditLogId.from( 5 ) ) );
    }
}
