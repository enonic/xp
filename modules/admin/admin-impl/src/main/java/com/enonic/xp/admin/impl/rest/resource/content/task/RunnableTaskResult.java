package com.enonic.xp.admin.impl.rest.resource.content.task;

enum TaskResultState
{
    SUCCESS, WARNING, ERROR
}

public class RunnableTaskResult
{
    RunnableTaskResult( Builder builder )
    {
    }

    public TaskResultState getState()
    {
        if ( getTotalCount() > 0 && getFailureCount() == 0 )
        {
            return TaskResultState.SUCCESS;
        }
        else if ( getTotalCount() > 0 && getSuccessCount() == 0 )
        {
            return TaskResultState.ERROR;
        }
        else
        {
            return TaskResultState.WARNING;
        }
    }

    public int getTotalCount()
    {
        return getSuccessCount() + getFailureCount();
    }

    public int getSuccessCount()
    {
        return 0;
    }

    public int getFailureCount()
    {
        return 0;
    }

    public String toJson()
    {
        return "{}";
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        Builder()
        {
        }

        public RunnableTaskResult build()
        {
            return new RunnableTaskResult( this );
        }
    }
}
