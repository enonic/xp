package com.enonic.xp.audit;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import com.enonic.xp.node.NodeVersionId;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuditLogIdTest
{
    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( AuditLogId.class ).withNonnullFields( "value" ).usingGetClass().verify();
    }

    @Test
    void valueBlank()
    {
        assertThrows( NullPointerException.class, () -> AuditLogId.from( null ) );
        assertThrows( IllegalArgumentException.class, () -> AuditLogId.from( "" ) );
        assertThrows( IllegalArgumentException.class, () -> AuditLogId.from( " " ) );
    }

    @Test
    void pattern()
    {
        assertThrows( IllegalArgumentException.class, () -> AuditLogId.from( "#abc#" ) );
    }

    @Test
    void fromObject()
    {
        assertDoesNotThrow( () -> NodeVersionId.from( java.util.UUID.randomUUID() ) );
    }
}