package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkflowInfoTest
{
    @Test
    void given_no_state_then_NullPointerException_is_thrown()
    {
        assertThrows( NullPointerException.class, () -> WorkflowInfo.create().build() );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( WorkflowInfo.class ).withNonnullFields( "state" ).verify();
    }
}
