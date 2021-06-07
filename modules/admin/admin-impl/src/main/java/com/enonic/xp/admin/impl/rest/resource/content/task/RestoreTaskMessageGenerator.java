package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.List;

import com.enonic.xp.content.ContentPath;

class RestoreTaskMessageGenerator
    extends TaskMessageGenerator<RestoreRunnableTaskResult>
{
    @Override
    String getNoResultsMessage()
    {
        return "Nothing was restored.";
    }

    @Override
    void appendMessageForSingleFailure( final StringBuilder builder, final RestoreRunnableTaskResult result )
    {
        final List<ContentPath> failed = result.getFailed();

        if ( failed != null && failed.size() == 1 )
        {
            builder.append( String.format( "Item \"%s\" could not be restored.", failed.get( 0 ).getName() ) );
        }
    }

    @Override
    void appendMessageForMultipleFailure( final StringBuilder builder, final RestoreRunnableTaskResult result )
    {
        builder.append( "Failed to restore " ).append( result.getFailureCount() ).append( " items." );
    }

    @Override
    void appendMessageForSingleSuccess( final StringBuilder builder, final RestoreRunnableTaskResult result )
    {
        final List<ContentPath> restored = result.getSucceeded();
        if ( restored != null && restored.size() == 1 )
        {
            builder.append( String.format( "Item \"%s\" is restored.", restored.get( 0 ).getName() ) );
        }
    }

    @Override
    void appendMessageForMultipleSuccess( final StringBuilder builder, final RestoreRunnableTaskResult result )
    {
        builder.append( result.getSuccessCount() ).append( " items were restored." );
    }

}
