package com.enonic.xp.issue;

import org.junit.Test;

import static org.junit.Assert.*;

public class IssueTest
{

    @Test
    public void testDefaultValuesSet()
    {
        Issue issue = Issue.create().
            title( "my issue" ).
            build();

        assertNotNull( issue.getId() );
        assertNotNull( issue.getName() );
        assertNotNull( issue.getApproverIds() );
        assertEquals( IssueStatus.Open, issue.getStatus() );
    }

    @Test
    public void testIdsNotEqual()
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
