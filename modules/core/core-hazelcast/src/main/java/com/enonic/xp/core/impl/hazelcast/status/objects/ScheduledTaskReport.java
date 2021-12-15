package com.enonic.xp.core.impl.hazelcast.status.objects;

public class ScheduledTaskReport
{
    private final String member;

    private final String taskName;

    private final long totalRuns;

    private final long delaySeconds;

    private final Boolean done;

    private final Boolean cancelled;

    public ScheduledTaskReport( final String member, final String taskName, final long totalRuns, final long delaySeconds,
                                final Boolean isDone, final Boolean isCancelled )
    {
        this.member = member;
        this.taskName = taskName;
        this.totalRuns = totalRuns;
        this.delaySeconds = delaySeconds;
        this.done = isDone;
        this.cancelled = isCancelled;
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

    public long getDelaySeconds()
    {
        return delaySeconds;
    }

    public Boolean getDone()
    {
        return done;
    }

    public Boolean getCancelled()
    {
        return cancelled;
    }
}
