package com.enonic.xp.auditlog;

import org.junit.Test;

import static org.junit.Assert.*;

public class AuditLogParamsTest
{

    @Test(expected = NullPointerException.class)
    public void create_empty()
    {
        AuditLogParams.create().build();
    }

    @Test
    public void create_only_type()
    {
        AuditLogParams params = AuditLogParams.create().type( AuditLogTestBuilder.type ).build();

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
        AuditLogParams params = AuditLogParams.create().
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