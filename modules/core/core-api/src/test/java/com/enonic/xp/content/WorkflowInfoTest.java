package com.enonic.xp.content;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import static org.junit.Assert.*;

public class WorkflowInfoTest
{
    @Test(expected = NullPointerException.class)
    public void given_no_state_then_NullPointerException_is_thrown()
    {
        WorkflowInfo.create().build();
    }

    @Test
    public void given_no_checks_then_check_should_be_empty_map()
    {
        WorkflowInfo workflowInfo = WorkflowInfo.create().
            state( WorkflowState.READY ).
            build();
        assertEquals( ImmutableMap.of(), workflowInfo.getChecks() );
    }
}
