package com.enonic.xp.core.impl.hazelcast.status.objects;

public class ScheduledTaskReport
{
    private final String member;
    private final String taskName;
    private final long totalRuns;

    public ScheduledTaskReport( final String member, final String taskName, final long totalRuns )
    {
        this.member = member;
        this.taskName = taskName;
        this.totalRuns = totalRuns;
    }

    public String getMember()
    {
        return member;
    }

    public String getTaskName()
    {
        return taskName;
    }

    public long getTotalRuns()
    {
        return totalRuns;
    }
}
