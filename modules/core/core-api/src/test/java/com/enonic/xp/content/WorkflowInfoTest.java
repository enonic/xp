package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorkflowInfoTest
{
    @Test
    public void given_no_state_then_NullPointerException_is_thrown()
    {
        assertThrows(NullPointerException.class, () -> WorkflowInfo.create().build() );
    }

    @Test
    public void given_no_checks_then_check_should_be_empty_map()
    {
        WorkflowInfo workflowInfo = WorkflowInfo.create().
            state( WorkflowState.READY ).
            build();
        assertTrue( workflowInfo.getChecks().isEmpty() );
    }
}
