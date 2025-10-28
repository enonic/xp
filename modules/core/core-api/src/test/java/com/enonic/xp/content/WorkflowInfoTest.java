package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkflowInfoTest
{
    @Test
    void given_no_state_then_NullPointerException_is_thrown()
    {
        assertThrows(NullPointerException.class, () -> WorkflowInfo.create().build() );
    }

    @Test
    void given_no_checks_then_check_should_be_empty_map()
    {
        WorkflowInfo workflowInfo = WorkflowInfo.create().
            state( WorkflowState.READY ).
            build();
        assertTrue( workflowInfo.getChecks().isEmpty() );
    }

    @Test
    void equalsContract() {
        EqualsVerifier.forClass( WorkflowInfo.class ).withNonnullFields( "state", "checks" ).verify();
    }
}
