package com.enonic.xp.admin.impl.rest.resource.content.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

    public String getMessage()
    {
        return "";
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
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode map = mapper.createObjectNode();

        map.put( "state", getState().toString() );
        map.put( "message", getMessage() );

        return map.toString();
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
