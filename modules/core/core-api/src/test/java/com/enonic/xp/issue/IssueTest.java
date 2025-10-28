package com.enonic.xp.issue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IssueTest
{

    @Test
    void testDefaultValuesSet()
    {
        Issue issue = Issue.create().
            title( "my issue" ).
            build();

        assertNotNull( issue.getId() );
        assertNotNull( issue.getName() );
        assertNotNull( issue.getApproverIds() );
        assertEquals( IssueStatus.OPEN, issue.getStatus() );
    }

    @Test
    void testIdsNotEqual()
    {
        Issue issue1 = Issue.create().
            title( "my issue" ).
            build();

        Issue issue2 = Issue.create().
            title( "my issue" ).
            build();

        assertNotEquals( issue1.getId(), issue2.getId() );
    }
}
