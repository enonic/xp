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
        assertNotNull( issue.getPath() );
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

    @Test
    public void testIssuePathStartsWithPrefix()
    {
        Issue issue = Issue.create().
            title( "my issue" ).
            build();

        assertTrue( issue.getPath().getValue().startsWith( "/issue/" ) );
    }

    @Test
    public void testIssuePathIsBuiltFromId()
    {
        final IssueId issueId = IssueId.create();
        final Issue issue = Issue.create().
            id( issueId ).
            build();

        assertEquals( "/issue/" + issueId.toString(), issue.getPath().getValue() );
    }
}
