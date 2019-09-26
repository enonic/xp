package com.enonic.xp.audit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuditLogParamsTest
{

    @Test
    public void create_empty()
    {
        assertThrows(NullPointerException.class, () -> {
            LogAuditLogParams.create().build();
        });
    }

    @Test
    public void create_only_type()
    {
        LogAuditLogParams params = LogAuditLogParams.create().type( AuditLogTestBuilder.type ).build();

        assertEquals( AuditLogTestBuilder.type, params.getType() );
        assertNotNull( params.getTime() );
        assertNotNull( params.getSource() );
        assertNotNull( params.getUser() );
        assertNotNull( params.getMessage() );
        assertNotNull( params.getObjectUris() );
        assertNotNull( params.getData() );
    }

    @Test
    public void create()
    {
        LogAuditLogParams params = LogAuditLogParams.create().
            type( AuditLogTestBuilder.type ).
            time( AuditLogTestBuilder.time ).
            source( AuditLogTestBuilder.source ).
            user( AuditLogTestBuilder.user ).
            message( AuditLogTestBuilder.message ).
            objectUris( AuditLogTestBuilder.objectUris ).
            data( AuditLogTestBuilder.data ).
            build();

        assertEquals( AuditLogTestBuilder.type, params.getType() );
        assertEquals( AuditLogTestBuilder.time, params.getTime() );
        assertEquals( AuditLogTestBuilder.source, params.getSource() );
        assertEquals( AuditLogTestBuilder.user, params.getUser() );
        assertEquals( AuditLogTestBuilder.message, params.getMessage() );
        assertEquals( AuditLogTestBuilder.objectUris, params.getObjectUris() );
        assertEquals( AuditLogTestBuilder.data, params.getData() );
    }

}