package com.enonic.xp.admin.impl.json.issue;

public class IssueStatsJson
{
    private final int assignedToMe;

    private final int createdByMe;

    private final int open;

    private final int closed;

    public IssueStatsJson( final int assignedToMe, final int createdByMe, final int open, final int closed )
    {
        this.assignedToMe = assignedToMe;
        this.createdByMe = createdByMe;
        this.open = open;
        this.closed = closed;
    }

    public int getAssignedToMe()
    {
        return assignedToMe;
    }

    public int getCreatedByMe()
    {
        return createdByMe;
    }

    public int getOpen()
    {
        return open;
    }

    public int getClosed()
    {
        return closed;
    }
}
