package com.enonic.xp.admin.impl.rest.resource.issue.json;

import org.junit.jupiter.api.Test;

import com.enonic.xp.issue.IssueStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListIssuesJsonTest
{
    @Test
    public void testParseIssueStatusOpen()
        throws Exception
    {
        final ListIssuesJson json = new ListIssuesJson( "OPEN", true, true, true, 0, 10 );

        assertEquals( json.getFindIssuesParams().getStatus(), IssueStatus.OPEN );
    }

    @Test
    public void testParseIssueStatusClosed()
        throws Exception
    {
        final ListIssuesJson json = new ListIssuesJson( "CLOSED", true, true, true, 0, 10 );

        assertEquals( json.getFindIssuesParams().getStatus(), IssueStatus.CLOSED );
    }

    @Test
    public void testParseIssueStatusNull()
        throws Exception
    {
        final ListIssuesJson json = new ListIssuesJson( null, true, true, true, 0, 10 );

        assertEquals( json.getFindIssuesParams().getStatus(), null );
    }

    @Test
    public void testParseIssueStatusUnknown()
        throws Exception
    {
        final ListIssuesJson json = new ListIssuesJson( "SomeUnknownStatus", true, true, true, 0, 10 );

        assertEquals( json.getFindIssuesParams().getStatus(), null );
    }
}
