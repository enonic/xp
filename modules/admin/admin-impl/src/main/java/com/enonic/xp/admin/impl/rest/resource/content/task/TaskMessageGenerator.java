package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.List;

import com.enonic.xp.content.ContentPath;

public abstract class TaskMessageGenerator<R extends RunnableTaskResult>
{
    public String generate( final R result )
    {
        final int total = result.getTotalCount();
        if ( total == 0 )
        {
            return getNoResultsMessage();
        }

        final int totalSuccess = result.getSuccessCount();
        final int totalFailed = result.getFailureCount();
        final StringBuilder builder = new StringBuilder();

        if ( totalSuccess == 1 )
        {
            appendMessageForSingleSuccess( builder, result );
        }
        else if ( totalSuccess > 1 )
        {
            appendMessageForMultipleSuccess( builder, result );
        }

        if ( totalFailed > 0 )
        {
            if ( totalSuccess > 0 )
            {
                builder.append( " " );
            }
            if ( totalFailed == 1 )
            {
                appendMessageForSingleFailure( builder, result );
            }
            else
            {
                appendMessageForMultipleFailure( builder, result );
            }
        }

        return builder.toString();
    }

    protected abstract String getNoResultsMessage();

    protected abstract void appendMessageForMultipleFailure( final StringBuilder builder, final R result );

    protected abstract void appendMessageForSingleFailure( final StringBuilder builder, final R result );

    protected abstract void appendMessageForMultipleSuccess( final StringBuilder builder, final R result );

    protected abstract void appendMessageForSingleSuccess( final StringBuilder builder, final R result );

    protected String getNameOrSize( final List<ContentPath> items )
    {
        return items.size() != 1 ? String.valueOf( items.size() ) : String.format( "\"%s\"", items.get( 0 ).getName() );
    }
}
